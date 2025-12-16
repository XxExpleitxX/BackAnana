package com.example.backAnana.Services;

import com.example.backAnana.Entities.DetalleVenta;
import com.example.backAnana.Entities.Dtos.CierreCajaDTO;
import com.example.backAnana.Entities.Enums.FormaPago;
import com.example.backAnana.Entities.Producto;
import com.example.backAnana.Entities.Venta;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VentaPrintManager {

    @Autowired
    private VentaService ventaService;

    String urlConexion = System.getenv("DB_URL");
    String usuario = System.getenv("DB_USER_NAME");
    String clave = System.getenv("DB_PASSWORD");

    public void imprimirExcelVentas(LocalDate desde, LocalDate hasta, HttpServletResponse response) throws IOException {
        String titulo = "Informe Detallado de Ventas desde " + desde + " hasta " + hasta;

        // Crear el libro de Excel
        SXSSFWorkbook libro = new SXSSFWorkbook(50);
        SXSSFSheet hoja = libro.createSheet("Ventas");

        // Definir encabezados
        String[] headers = {
                "ID Venta", "Fecha y Hora", "Forma de Pago",
                "Cliente", "Total", "Envío", "Cantidad de Productos"
        };

        for (int i = 0; i < headers.length; i++) {
            hoja.trackColumnForAutoSizing(i);
        }

        // Crear estilos
        CellStyle headerStyle = crearEstiloEncabezado(libro);
        CellStyle dateTimeStyle = crearEstiloFechaHora(libro);
        CellStyle currencyStyle = crearEstiloMoneda(libro);

        int fila = 0;

        // Título
        SXSSFRow tituloRow = hoja.createRow(fila++);
        SXSSFCell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue(titulo);
        tituloCell.setCellStyle(headerStyle);

        fila++; // espacio

        // Encabezados
        SXSSFRow headerRow = hoja.createRow(fila++);
        for (int i = 0; i < headers.length; i++) {
            SXSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Obtener ventas del rango
        List<Venta> ventas = getVentasFromRangeOfDates(desde, hasta);

        // ✅ Ordenar cronológicamente (fecha y hora)
        ventas.sort(Comparator.comparing(Venta::getFecha));

        // Cargar datos
        for (Venta venta : ventas) {
            SXSSFRow row = hoja.createRow(fila++);
            int col = 0;

            row.createCell(col++).setCellValue(venta.getId());

            // ✅ Fecha y hora completas
            Cell fechaCell = row.createCell(col++);
            fechaCell.setCellValue(Timestamp.valueOf(venta.getFecha()));
            fechaCell.setCellStyle(dateTimeStyle);

            row.createCell(col++).setCellValue(venta.getFormaPago().toString());
            row.createCell(col++).setCellValue(venta.getUser() != null ? venta.getUser().getUsuario() : "—");

            Cell totalCell = row.createCell(col++);
            totalCell.setCellValue(venta.getTotal());
            totalCell.setCellStyle(currencyStyle);

            Cell envioCell = row.createCell(col++);
            envioCell.setCellValue(venta.getEnvio());
            envioCell.setCellStyle(currencyStyle);

            row.createCell(col++).setCellValue(venta.getDetalleVentas().size());
        }

        // Resumen
        fila++;
        SXSSFRow resumenRow = hoja.createRow(fila++);
        resumenRow.createCell(0).setCellValue("TOTAL VENTAS:");
        resumenRow.createCell(1).setCellValue(ventas.size());

        fila++;
        SXSSFRow totalRow = hoja.createRow(fila++);
        totalRow.createCell(0).setCellValue("MONTO TOTAL:");
        Cell totalMontoCell = totalRow.createCell(1);
        totalMontoCell.setCellValue(ventas.stream().mapToDouble(Venta::getTotal).sum());
        totalMontoCell.setCellStyle(currencyStyle);

        // Autoajustar columnas
        for (int i = 0; i < headers.length; i++) {
            hoja.autoSizeColumn(i);
        }

        // Configurar respuesta HTTP
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String nombreArchivo = "informe_ventas_" + desde + "_a_" + hasta + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=" + nombreArchivo);

        // Escribir el archivo
        try (ServletOutputStream out = response.getOutputStream()) {
            libro.write(out);
        } finally {
            libro.dispose();
        }
    }

    public List<Venta> getVentasFromRangeOfDates(final LocalDate fechaDesde, final LocalDate fechaHasta) {
        List<Venta> allVentas = new ArrayList<>();
        LocalDate fechaHastaIncrementada = fechaHasta.plusDays(1);

        try {
            allVentas = getVentasFromRangeOfDatesUsingSQL(fechaDesde, fechaHastaIncrementada);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return allVentas.stream()
                .filter(venta ->
                        !venta.getFecha().toLocalDate().isBefore(fechaDesde) &&
                                !venta.getFecha().toLocalDate().isAfter(fechaHasta)
                )
                .collect(Collectors.toList());
    }

    private List<Venta> getVentasFromRangeOfDatesUsingSQL(LocalDate fechaDesde, LocalDate fechaHasta)
            throws SQLException, ClassNotFoundException {

        Map<Long, Venta> ventaMap = new HashMap<>();
        Connection conexion = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(urlConexion, usuario, clave);

            String query = "SELECT v.id as venta_id, v.fecha, v.total, v.formaPago, v.envio, " +
                    "p.denominacion, p.marca, p.codigo, p.precio, dv.cantidad " +
                    "FROM venta v " +
                    "JOIN detalleventa dv ON v.id = dv.ventaId " +
                    "JOIN producto p ON dv.productoId = p.id " +
                    "WHERE v.fecha >= ? AND v.fecha <= ? " +
                    "ORDER BY v.fecha ASC"; // 🕒 importante para ordenar por fecha+hora

            PreparedStatement ps = conexion.prepareStatement(query);
            // Usamos Timestamp para conservar hora
            ps.setTimestamp(1, Timestamp.valueOf(fechaDesde.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(fechaHasta.atTime(23, 59, 59)));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Long ventaId = rs.getLong("venta_id");

                Venta venta = ventaMap.get(ventaId);
                if (venta == null) {
                    venta = new Venta();
                    venta.setId(ventaId);
                    // ✅ obtener fecha completa con hora
                    venta.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                    venta.setTotal(rs.getDouble("total"));
                    venta.setEnvio(rs.getDouble("envio"));
                    venta.setFormaPago(FormaPago.valueOf(rs.getString("formaPago")));
                    ventaMap.put(ventaId, venta);
                }

                DetalleVenta detalle = new DetalleVenta();
                detalle.setCantidad(rs.getInt("cantidad"));
                detalle.setProducto(new Producto(
                        rs.getString("denominacion"),
                        rs.getString("marca"),
                        rs.getString("codigo"),
                        rs.getDouble("precio")
                ));
                detalle.setVenta(venta);

                venta.getDetalleVentas().add(detalle);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (conexion != null) {
                conexion.close();
            }
        }

        return new ArrayList<>(ventaMap.values());
    }

    public CierreCajaDTO generarCierreCajaDelDia(LocalDate fecha) {
        return generarResumenVentas(fecha, fecha);
    }

    public CierreCajaDTO generarCierreCajaEntreFechas(LocalDate desde, LocalDate hasta) {
        return generarResumenVentas(desde, hasta);
    }

    private CierreCajaDTO generarResumenVentas(LocalDate desde, LocalDate hasta) {
        List<Venta> ventas = getVentasFromRangeOfDates(desde, hasta);
        CierreCajaDTO cierre = CierreCajaDTO.builder().desde(desde).hasta(hasta).build();

        for (Venta venta : ventas) {
            String formaPago = venta.getFormaPago().toString();
            cierre.agregarFormaPago(formaPago, venta.getTotal());
        }

        return cierre;
    }

    public SXSSFWorkbook exportarCierreCajaExcel(CierreCajaDTO cierre) {
        SXSSFWorkbook libro = new SXSSFWorkbook(10);
        SXSSFSheet hoja = libro.createSheet("Cierre de Caja");

        hoja.trackAllColumnsForAutoSizing();

        int fila = 0;
        SXSSFRow row;

        // Estilo general
        CellStyle headerStyle = libro.createCellStyle();
        Font font = libro.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        // Rango de fechas
        row = hoja.createRow(fila++);
        row.createCell(0).setCellValue("Desde:");
        row.createCell(1).setCellValue(cierre.getDesde().toString());

        row = hoja.createRow(fila++);
        row.createCell(0).setCellValue("Hasta:");
        row.createCell(1).setCellValue(cierre.getHasta().toString());

        fila++; // espacio

        // Encabezado
        row = hoja.createRow(fila++);
        SXSSFCell cell0 = row.createCell(0);
        cell0.setCellValue("Forma de Pago");
        cell0.setCellStyle(headerStyle);

        SXSSFCell cell1 = row.createCell(1);
        cell1.setCellValue("Total");
        cell1.setCellStyle(headerStyle);

        // Detalle por forma de pago
        for (Map.Entry<String, Double> entry : cierre.getTotalPorFormaPago().entrySet()) {
            row = hoja.createRow(fila++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
        }

        fila++; // espacio

        // Total general
        row = hoja.createRow(fila++);
        row.createCell(0).setCellValue("TOTAL GENERAL:");
        row.createCell(1).setCellValue(cierre.getTotalGeneral());

        // Autoajuste
        hoja.autoSizeColumn(0);
        hoja.autoSizeColumn(1);

        return libro;
    }

    public SXSSFWorkbook generarInformeDiarioDetalle(LocalDate fecha) throws IOException {
        return generarInformeDetallado(fecha, fecha, "Informe Diario - " + fecha);
    }

    public SXSSFWorkbook generarInformeMensualDetalle(int año, int mes) throws IOException {
        LocalDate primeraFecha = LocalDate.of(año, mes, 1);
        LocalDate ultimaFecha = primeraFecha.withDayOfMonth(primeraFecha.lengthOfMonth());

        String nombreMes = primeraFecha.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        String titulo = "Informe Mensual - " + nombreMes + " " + año;

        return generarInformeDetallado(primeraFecha, ultimaFecha, titulo);
    }

    private SXSSFWorkbook generarInformeDetallado(LocalDate desde, LocalDate hasta, String titulo) throws IOException {
        // Se crea el libro
        SXSSFWorkbook libro = new SXSSFWorkbook(50);
        SXSSFSheet hoja = libro.createSheet("Detalle Ventas");

        // Definir headers EARLY para poder trackear antes de escribir filas
        String[] headers = {
                "ID Venta", "Fecha y Hora", "Forma de Pago",
                "Producto", "Marca", "Código", "Cantidad",
                "Precio Unitario", "Subtotal", "Envío", "Total Venta"
        };

        // TRACK: habilitar tracking sólo en las columnas que vamos a autosizear
        for (int i = 0; i < headers.length; i++) {
            hoja.trackColumnForAutoSizing(i);
        }

        // Estilos
        CellStyle headerStyle = crearEstiloEncabezado(libro);
        CellStyle dateTimeStyle = crearEstiloFechaHora(libro); // ✅ nuevo estilo con hora
        CellStyle currencyStyle = crearEstiloMoneda(libro);

        int fila = 0;

        // Título
        SXSSFRow tituloRow = hoja.createRow(fila++);
        SXSSFCell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue(titulo);
        tituloCell.setCellStyle(headerStyle);

        fila++; // espacio

        // Encabezados
        SXSSFRow headerRow = hoja.createRow(fila++);
        for (int i = 0; i < headers.length; i++) {
            SXSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Obtener ventas
        List<Venta> ventas = getVentasFromRangeOfDates(desde, hasta);

        // ✅ Ordenar cronológicamente por fecha y hora
        ventas.sort(Comparator.comparing(Venta::getFecha));

        // Datos
        for (Venta venta : ventas) {
            for (DetalleVenta detalle : venta.getDetalleVentas()) {
                SXSSFRow dataRow = hoja.createRow(fila++);
                int columna = 0;

                // Datos de la venta
                dataRow.createCell(columna++).setCellValue(venta.getId());

                // ✅ Mostrar fecha + hora completa en una sola celda
                Cell fechaCell = dataRow.createCell(columna++);
                fechaCell.setCellValue(Timestamp.valueOf(venta.getFecha()));
                fechaCell.setCellStyle(dateTimeStyle);

                dataRow.createCell(columna++).setCellValue(venta.getFormaPago().toString());

                // Datos del producto
                Producto producto = detalle.getProducto();
                dataRow.createCell(columna++).setCellValue(producto.getDenominacion());
                dataRow.createCell(columna++).setCellValue(producto.getMarca());
                dataRow.createCell(columna++).setCellValue(producto.getCodigo());
                dataRow.createCell(columna++).setCellValue(detalle.getCantidad());

                Cell precioCell = dataRow.createCell(columna++);
                precioCell.setCellValue(producto.getPrecio());
                precioCell.setCellStyle(currencyStyle);

                Cell subtotalCell = dataRow.createCell(columna++);
                subtotalCell.setCellValue(detalle.getSubTotal());
                subtotalCell.setCellStyle(currencyStyle);

                Cell envioCell = dataRow.createCell(columna++);
                envioCell.setCellValue(venta.getEnvio());
                envioCell.setCellStyle(currencyStyle);

                Cell totalCell = dataRow.createCell(columna++);
                totalCell.setCellValue(venta.getTotal());
                totalCell.setCellStyle(currencyStyle);
            }
        }

        // Resumen al final
        fila++;
        SXSSFRow resumenRow = hoja.createRow(fila++);
        resumenRow.createCell(0).setCellValue("TOTAL VENTAS:");
        resumenRow.createCell(1).setCellValue(ventas.size());

        fila++;
        SXSSFRow totalRow = hoja.createRow(fila++);
        totalRow.createCell(0).setCellValue("MONTO TOTAL:");
        Cell totalMontoCell = totalRow.createCell(1);
        totalMontoCell.setCellValue(ventas.stream().mapToDouble(Venta::getTotal).sum());
        totalMontoCell.setCellStyle(currencyStyle);

        // Autoajustar columnas (funcionará porque trackeamos antes)
        for (int i = 0; i < headers.length; i++) {
            hoja.autoSizeColumn(i);
        }

        return libro;
    }

    private CellStyle crearEstiloFechaHora(SXSSFWorkbook libro) {
        CellStyle style = libro.createCellStyle();
        CreationHelper createHelper = libro.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy HH:mm:ss"));
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle crearEstiloEncabezado(SXSSFWorkbook libro) {
        CellStyle style = libro.createCellStyle();
        Font font = libro.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle crearEstiloMoneda(SXSSFWorkbook libro) {
        CellStyle style = libro.createCellStyle();
        CreationHelper createHelper = libro.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("$#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }


}