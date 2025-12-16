package com.example.backAnana.Repositories;

import com.example.backAnana.Entities.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends BaseRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {

    // Método para verificar si el código ya existe
    boolean existsByCodigo(String codigo);

    // Método para buscar por código
    Optional<Producto> findByCodigo(String codigo);

    //Método para buscar por denominacion
    Optional<Producto> findByDenominacion(String denominacion);

    @EntityGraph(attributePaths = {"categoria"})
    Page<Producto> findAll(Specification<Producto> spec, Pageable pageable);

    @Query("SELECT DISTINCT LOWER(p.marca) FROM Producto p WHERE p.marca IS NOT NULL")
    List<String> findDistinctMarcas();

}
