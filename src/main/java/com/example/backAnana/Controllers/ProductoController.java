package com.example.backAnana.Controllers;

import com.example.backAnana.Entities.Dtos.ProductoConDescuentoDTO;
import com.example.backAnana.Entities.Producto;
import com.example.backAnana.Services.Impl.ProductoServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/producto")
public class ProductoController extends BaseControllerImpl<Producto, ProductoServiceImpl> {

    @Autowired
    private ProductoServiceImpl service;

    private final ObjectMapper objectMapper;

    public ProductoController(ProductoServiceImpl service, ObjectMapper objectMapper) {
        super(service);
        this.objectMapper = objectMapper;
    }

    // Endpoint para guardar/actualizar producto con imagen
    @PostMapping(path = "/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveWithImage(
            @RequestPart("producto") String productoStr,
            @RequestPart(value = "imagenFile", required = false) MultipartFile imagenFile) {

        try {
            Producto producto = objectMapper.readValue(productoStr, Producto.class);

            if(producto.getId() == null) {
                service.validarCodigoUnico(producto.getCodigo(), null);
            } else {
                service.validarCodigoUnico(producto.getCodigo(), producto.getId());
            }

            Producto productoGuardado = service.save(producto, imagenFile);
            return ResponseEntity.ok(productoGuardado);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping(path = "/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateWithImage(
            @RequestPart("producto") String productoStr,
            @RequestPart(value = "imagenFile", required = false) MultipartFile imagenFile) {

        try {
            Producto producto = objectMapper.readValue(productoStr, Producto.class);

            if (producto.getId() == null) {
                return ResponseEntity.badRequest().body("{\"error\":\"ID del producto requerido para actualizar\"}");
            }

            service.validarCodigoUnico(producto.getCodigo(), producto.getId());

            Producto productoGuardado = service.save(producto, imagenFile);
            return ResponseEntity.ok(productoGuardado);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // Endpoint para actualizar stock
    @PatchMapping("/{codigo}/stock")
    public ResponseEntity<?> actualizarStock(@PathVariable String codigo, @RequestParam int cantidad) {
        try {
            Producto producto = service.updateStock(codigo, cantidad);
            return ResponseEntity.ok(producto);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Endpoint para subir imagen a un producto existente
    @PostMapping("/{codigo}/imagen")
    public ResponseEntity<?> uploadProductImage(
            @PathVariable String codigo,
            @RequestParam("file") MultipartFile file) {

        try {
            Producto producto = service.addImageToProduct(codigo, file);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // Endpoint para eliminar imagen de un producto
    @DeleteMapping("/{codigo}/imagen")
    public ResponseEntity<?> removeProductImage(@PathVariable String codigo) {
        try {
            Producto producto = service.removeImageFromProduct(codigo);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // Métodos existentes...
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<?> buscarPorCodigo(@PathVariable String codigo) {
        try {
            Producto producto = service.findByCodigo(codigo);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/nombre/{denominacion}")
    public ResponseEntity<?> buscarPorDenominacion(@PathVariable String denominacion) {
        try {
            Producto producto = service.findByDenominacion(denominacion);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/con-descuento")
    public Page<ProductoConDescuentoDTO> getProductosConDescuento(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false, name = "orden") String ordenPrecio // asc|desc
    ) {
        return service.getProductosConDescuento(page, size, marca, categoria, busqueda, ordenPrecio);
    }

    @GetMapping("/marcas")
    public List<String> getMarcas() {
        return service.getMarcas();
    }

    /*@GetMapping("/con-descuento")
    public ResponseEntity<List<ProductoConDescuentoDTO>> getProductosConDescuento() {
        return ResponseEntity.ok(service.getProductosConDescuento());
    }*/

}
