package com.example.backAnana.Repositories;

import com.example.backAnana.Entities.DetalleVenta;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends BaseRepository<DetalleVenta, Long> {

    List<DetalleVenta> findAllByVentaId(Long id);

}
