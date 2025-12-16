package com.example.backAnana.Repositories;

import com.example.backAnana.Entities.Venta;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends BaseRepository<Venta, Long> {

    List<Venta> findByUserId(Long userId);

    @Query("SELECT v FROM Venta v WHERE v.fecha BETWEEN :desde AND :hasta")
    List<Venta> findAllByFechaBetween(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

}
