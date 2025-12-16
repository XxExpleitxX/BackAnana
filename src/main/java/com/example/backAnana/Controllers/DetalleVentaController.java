package com.example.backAnana.Controllers;

import com.example.backAnana.Entities.DetalleVenta;
import com.example.backAnana.Entities.Dtos.DetalleVentaDTO;
import com.example.backAnana.Entities.Producto;
import com.example.backAnana.Entities.Venta;
import com.example.backAnana.Repositories.ProductoRepository;
import com.example.backAnana.Repositories.VentaRepository;
import com.example.backAnana.Services.Impl.DetalleVentaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/detalleVenta")
public class DetalleVentaController extends BaseControllerImpl<DetalleVenta, DetalleVentaServiceImpl> {

    @Autowired
    private DetalleVentaServiceImpl detalleVentaServiceImpl;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    private DetalleVentaController(DetalleVentaServiceImpl service) {
        super(service);
    }

    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<List<DetalleVenta>> findAllByVentaId(@PathVariable Long ventaId) throws Exception {
        List<DetalleVenta> detalles = detalleVentaServiceImpl.findAllByVentaId(ventaId);
        return new ResponseEntity<>(detalles, HttpStatus.OK);
    }

    @PostMapping("/dto")
    public ResponseEntity<?> crearDetalleDesdeDTO(@RequestBody DetalleVentaDTO dto) {
        try {
            DetalleVenta detalle = new DetalleVenta();

            // Buscar venta
            Venta venta = ventaRepository.findById(dto.getVentaId())
                    .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

            // Buscar producto
            Producto producto = productoRepository.findById(dto.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Setear datos
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(dto.getCantidad());
            detalle.setPrecioAplicado(dto.getPrecioAplicado());

            // Guardar
            DetalleVenta guardado = detalleVentaServiceImpl.save(detalle);
            return ResponseEntity.ok(guardado);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al guardar detalle: " + e.getMessage());
        }
    }

}
