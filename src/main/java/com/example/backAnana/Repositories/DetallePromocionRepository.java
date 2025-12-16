package com.example.backAnana.Repositories;

import com.example.backAnana.Entities.DetallePromocion;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePromocionRepository extends BaseRepository<DetallePromocion, Long> {

    List<DetallePromocion> findAllByPromocionId(Long id);

}
