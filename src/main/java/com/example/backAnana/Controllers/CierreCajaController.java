package com.example.backAnana.Controllers;

import com.example.backAnana.Entities.Dtos.CierreCajaDTO;
import com.example.backAnana.Services.VentaPrintManager;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/cierre-caja")
public class CierreCajaController {

    @Autowired
    private VentaPrintManager ventaPrintManager;

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportarCierreCajaExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        try {
            CierreCajaDTO cierre = ventaPrintManager.generarCierreCajaEntreFechas(desde, hasta);
            SXSSFWorkbook workbook = ventaPrintManager.exportarCierreCajaExcel(cierre);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            byte[] excelData = outputStream.toByteArray();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cierre_caja_" + desde + "_a_" + hasta + ".xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelData);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/exportar-hoy")
    public ResponseEntity<byte[]> exportarCierreHoy() {
        LocalDate hoy = LocalDate.now();
        return exportarCierreCajaExcel(hoy, hoy);
    }

    @GetMapping("/informe-diario-detalle")
    public ResponseEntity<byte[]> exportarInformeDiarioDetalle(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        try {
            SXSSFWorkbook workbook = ventaPrintManager.generarInformeDiarioDetalle(fecha);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            byte[] excelData = outputStream.toByteArray();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=informe_diario_detalle_" + fecha + ".xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelData);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/informe-mensual-detalle")
    public ResponseEntity<byte[]> exportarInformeMensualDetalle(
            @RequestParam int año,
            @RequestParam int mes) {

        try {
            SXSSFWorkbook workbook = ventaPrintManager.generarInformeMensualDetalle(año, mes);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            byte[] excelData = outputStream.toByteArray();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=informe_mensual_detalle_" + año + "_" + mes + ".xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelData);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/informe-diario-hoy")
    public ResponseEntity<byte[]> exportarInformeDiarioHoy() {
        LocalDate hoy = LocalDate.now();
        return exportarInformeDiarioDetalle(hoy);
    }

    @GetMapping("/informe-mensual-actual")
    public ResponseEntity<byte[]> exportarInformeMensualActual() {
        LocalDate hoy = LocalDate.now();
        return exportarInformeMensualDetalle(hoy.getYear(), hoy.getMonthValue());
    }

}
