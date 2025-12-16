package com.example.backAnana.Services.Impl;

import com.example.backAnana.Entities.DetalleVenta;
import com.example.backAnana.Entities.Producto;
import com.example.backAnana.Entities.Venta;
import com.example.backAnana.Repositories.DetalleVentaRepository;
import com.example.backAnana.Repositories.ProductoRepository;
import com.example.backAnana.Repositories.VentaRepository;
import com.example.backAnana.Services.DetalleVentaService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DetalleVentaServiceImpl extends BaseServiceImpl<DetalleVenta, Long> implements DetalleVentaService {

    @Autowired
    private DetalleVentaRepository repository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ConfigServiceImpl configService;

    public DetalleVentaServiceImpl(DetalleVentaRepository repository) {
        super(repository);
    }

    @Transactional
    public List<DetalleVenta> findAllByVentaId(Long id) throws Exception{
        return repository.findAllByVentaId(id);
    }

    private void recalcularTotalVenta(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        List<DetalleVenta> detalles = repository.findAllByVentaId(ventaId);
        double nuevoTotal = detalles.stream()
                .mapToDouble(DetalleVenta::getSubTotal)
                .sum();

        nuevoTotal += venta.getEnvio() != null ? venta.getEnvio() : 0;
        venta.setTotal(nuevoTotal);
        ventaRepository.save(venta);
    }

    @Override
    @Transactional
    public DetalleVenta save(DetalleVenta entity) throws Exception {
        try {
            // Buscar producto original desde BD
            Producto producto = productoRepository.findById(entity.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Validar stock
            int nuevaCantidadVendida = producto.getCantidadVendida() + entity.getCantidad();
            int nuevoStock = producto.getStock() - entity.getCantidad();

            if (nuevoStock < 0) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getDenominacion());
            }

            if (entity.getPrecioAplicado() == null || entity.getPrecioAplicado() <= 0) {
                double precioOriginal = producto.getPrecio();
                double descuento = configService.getDescuentoGlobal();
                double precioConDescuento = precioOriginal * (1 - descuento / 100);

                entity.setPrecioAplicado(precioConDescuento);
            }

            // Actualizar stock y cantidad vendida
            producto.setCantidadVendida(nuevaCantidadVendida);
            producto.setStock(nuevoStock);
            productoRepository.save(producto);

            // Guardar el detalle
            DetalleVenta detalleGuardado = super.save(entity);

            // Recalcular el total de la venta
            recalcularTotalVenta(detalleGuardado.getVenta().getId());

            return detalleGuardado;

        } catch (Exception e) {
            throw new Exception("Error al guardar el detalle de venta: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public DetalleVenta update(DetalleVenta entity) throws Exception {
        try {
            DetalleVenta detalleOriginal = repository.findById(entity.getId())
                    .orElseThrow(() -> new RuntimeException("Detalle de venta no encontrado"));

            Producto producto = productoRepository.findById(entity.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            int cantidadAnterior = detalleOriginal.getCantidad();
            int nuevaCantidad = entity.getCantidad();
            int diferencia = nuevaCantidad - cantidadAnterior;

            int nuevoStock = producto.getStock() - diferencia;
            int nuevaCantidadVendida = producto.getCantidadVendida() + diferencia;

            if (nuevoStock < 0) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getDenominacion());
            }

            producto.setStock(nuevoStock);
            producto.setCantidadVendida(Math.max(0, nuevaCantidadVendida));
            productoRepository.save(producto);

            // Actualizar el detalle
            detalleOriginal.setCantidad(nuevaCantidad);
            detalleOriginal.setProducto(producto);
            detalleOriginal.setVenta(entity.getVenta()); // si puede cambiar de venta
            DetalleVenta detalleGuardado = repository.save(detalleOriginal);

            recalcularTotalVenta(detalleGuardado.getVenta().getId());

            return detalleGuardado;

        } catch (Exception e) {
            throw new Exception("Error al actualizar el detalle de venta: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean delete(Long idDetalleVenta) {
        DetalleVenta detalle = repository.findById(idDetalleVenta)
                .orElseThrow(() -> new RuntimeException("DetalleVenta no encontrado"));

        Producto producto = detalle.getProducto();
        if (producto != null && detalle.getCantidad() > 0) {
            // Devolver stock
            producto.setStock(producto.getStock() + detalle.getCantidad());
            // Restar la cantidad vendida
            producto.setCantidadVendida(producto.getCantidadVendida() - detalle.getCantidad());
            productoRepository.save(producto);
        }

        repository.delete(detalle);

        // ✅ Recalcular total
        recalcularTotalVenta(detalle.getVenta().getId());

        return true;
    }

}
