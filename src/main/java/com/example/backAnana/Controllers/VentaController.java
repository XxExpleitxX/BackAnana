package com.example.backAnana.Controllers;

import com.example.backAnana.Entities.Venta;
import com.example.backAnana.Services.Impl.VentaServiceImpl;
import com.example.backAnana.Services.VentaPrintManager;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/venta")
public class VentaController extends BaseControllerImpl<Venta, VentaServiceImpl> {

    @Autowired
    private VentaServiceImpl service;

    public VentaController(VentaServiceImpl service) {
        super(service);
    }

    @GetMapping("/downloadExcel")
    public void downloadExcelVentas(
            @RequestParam(name = "fechaDesde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(name = "fechaHasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            HttpServletResponse response) throws IOException, SQLException {

        VentaPrintManager ventaPrintManager = new VentaPrintManager();
        ventaPrintManager.imprimirExcelVentas(fechaDesde, fechaHasta, response);
    }

    @PutMapping("/agregar-promocion/{ventaId}/{promocionId}")
    public ResponseEntity<Venta> agregarPromocion(
            @PathVariable Long ventaId,
            @PathVariable Long promocionId) {
        Venta ventaActualizada = service.agregarPromocionAVenta(ventaId, promocionId);
        return ResponseEntity.ok(ventaActualizada);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Venta>> obtenerVentasUsuario(@PathVariable Long userId) {
        try {
            List<Venta> ventas = service.findByUserId(userId);
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @DeleteMapping("/eliminar-entre-fechas")
    public ResponseEntity<String> eliminarVentasPorFechas(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        boolean eliminado = service.eliminarVentasEntreFechas(desde, hasta);
        if (eliminado) {
            return ResponseEntity.ok("Ventas eliminadas exitosamente.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al eliminar las ventas.");
        }
    }

}
