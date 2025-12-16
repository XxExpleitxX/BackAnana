package com.example.backAnana.Services;

import com.example.backAnana.Entities.Venta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface VentaService extends BaseService<Venta, Long> {

    List<Venta> findByUserId(Long userId) throws Exception;

    boolean eliminarVentasEntreFechas(LocalDateTime desde, LocalDateTime hasta) throws Exception;

}
