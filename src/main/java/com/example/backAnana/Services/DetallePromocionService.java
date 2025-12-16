package com.example.backAnana.Services;

import com.example.backAnana.Entities.DetallePromocion;

import java.util.List;

public interface DetallePromocionService extends BaseService<DetallePromocion, Long> {

    List<DetallePromocion> findAllByPromocionId(Long id) throws Exception;

}
