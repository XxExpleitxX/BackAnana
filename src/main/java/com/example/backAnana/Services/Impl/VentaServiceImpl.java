package com.example.backAnana.Services.Impl;

import com.example.backAnana.Entities.*;
import com.example.backAnana.Repositories.DetalleVentaRepository;
import com.example.backAnana.Repositories.ProductoRepository;
import com.example.backAnana.Repositories.PromocionRepository;
import com.example.backAnana.Repositories.VentaRepository;
import com.example.backAnana.Services.DetalleVentaService;
import com.example.backAnana.Services.VentaService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VentaServiceImpl extends BaseServiceImpl<Venta, Long> implements VentaService {

    private final VentaRepository repository;
    private final DetalleVentaRepository detalleRepository;
    private final DetalleVentaService detalleVentaService;
    private final ProductoRepository productoRepository;
    private final PromocionRepository promocionRepository;

    @Autowired
    public VentaServiceImpl(VentaRepository repository, DetalleVentaRepository detalleRepository, DetalleVentaService detalleVentaService, ProductoRepository productoRepository, PromocionRepository promocionRepository) {
        super(repository);
        this.repository = repository;
        this.detalleRepository = detalleRepository;
        this.detalleVentaService = detalleVentaService;
        this.productoRepository = productoRepository;
        this.promocionRepository = promocionRepository;
    }

    @Override
    @Transactional
    public Venta save(Venta venta) throws Exception {
        try {
            // Asignar fecha real de Argentina siempre al crear
            venta.setFecha(LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")));

            return repository.save(venta);
        } catch (Exception e) {
            throw new Exception("Error al guardar la venta: " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public Venta update(Venta venta) throws Exception {
        try {
            Venta ventaExistente = repository.findById(venta.getId())
                    .orElseThrow(() -> new Exception("Venta no encontrada"));

            // Actualizamos solo campos generales
            ventaExistente.setFecha(LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")));
            ventaExistente.setEnvio(venta.getEnvio());
            ventaExistente.setEstadoVenta(venta.getEstadoVenta());
            ventaExistente.setFormaPago(venta.getFormaPago());
            ventaExistente.setObservaciones(venta.getObservaciones());

            // (Opcional) recalcular total sumando subtotales de los detalles actuales
            List<DetalleVenta> detalles = detalleVentaService.findAllByVentaId(venta.getId());
            double total = detalles.stream()
                    .mapToDouble(DetalleVenta::getSubTotal)
                    .sum();
            ventaExistente.setTotal(total + (venta.getEnvio() != null ? venta.getEnvio() : 0));

            return repository.save(ventaExistente);

        } catch (Exception e) {
            throw new Exception("Error actualizando la venta: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean delete(Long idVenta) {
        try {
            Venta venta = repository.findById(idVenta)
                    .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

            List<DetalleVenta> detalles = detalleVentaService.findAllByVentaId(idVenta);

            for (DetalleVenta detalle : detalles) {
                detalleVentaService.delete(detalle.getId());
            }

            repository.delete(venta);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public Venta agregarPromocionAVenta(Long ventaId, Long promocionId) {
        try {
            Venta venta = repository.findById(ventaId)
                    .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

            Promocion promocion = promocionRepository.findById(promocionId)
                    .orElseThrow(() -> new RuntimeException("Promoción no encontrada"));

            // Paso 1: Calcular el valor total original de todos los productos en la promoción
            double precioOriginalTotal = 0.0;
            for (DetallePromocion dp : promocion.getDetallePromociones()) {
                Producto producto = dp.getProducto();
                precioOriginalTotal += producto.getPrecio() * dp.getCantidad();
            }

            // Paso 2: Repartir el precioPromocional proporcionalmente
            for (DetallePromocion dp : promocion.getDetallePromociones()) {
                Producto producto = dp.getProducto();
                int cantidad = dp.getCantidad();

                if (producto.getStock() < cantidad) {
                    throw new RuntimeException("Stock insuficiente para el producto: " + producto.getDenominacion());
                }

                // Calcular proporción del producto dentro del total original
                double valorProductoOriginal = producto.getPrecio() * cantidad;
                double proporcion = valorProductoOriginal / precioOriginalTotal;

                // Calcular el precio total asignado a este producto dentro de la promo
                double totalAsignado = promocion.getPrecioPromocional() * proporcion;

                // Precio unitario con promo
                double precioUnitarioAplicado = totalAsignado / cantidad;

                // Crear detalle de venta
                DetalleVenta dv = new DetalleVenta();
                dv.setProducto(producto);
                dv.setCantidad(cantidad);
                dv.setVenta(venta);
                dv.setPrecioAplicado(precioUnitarioAplicado);

                // Actualizar stock y ventas
                producto.setStock(producto.getStock() - cantidad);
                producto.setCantidadVendida(producto.getCantidadVendida() + cantidad);
                productoRepository.save(producto);

                // Agregar a la venta
                venta.addDetalleVenta(dv);
            }

            // Calcular total de la venta
            double totalVenta = venta.getDetalleVentas().stream()
                    .mapToDouble(DetalleVenta::getSubTotal)
                    .sum();

            venta.setTotal(totalVenta);
            venta.setFecha(LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")));

            return repository.save(venta);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public List<Venta> findByUserId(Long userId) {
        try {
            return repository.findByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Transactional
    public boolean eliminarVentasEntreFechas(LocalDateTime desde, LocalDateTime hasta) {
        try {
            List<Venta> ventas = repository.findAllByFechaBetween(desde, hasta);

            for (Venta venta : ventas) {
                repository.delete(venta);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
