package com.example.backAnana.Specifications;

import com.example.backAnana.Entities.Producto;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.JoinType;

public class ProductoSpecifications {

    public static Specification<Producto> hasMarca(String marca) {
        return (root, query, cb) -> {
            if (marca == null || marca.isBlank()) return null;
            return cb.equal(cb.lower(root.get("marca")), marca.toLowerCase());
        };
    }

    public static Specification<Producto> hasCategoriaDenominacion(String categoria) {
        return (root, query, cb) -> {
            if (categoria == null || categoria.isBlank()) return null;
            // join con categoria
            return cb.equal(cb.lower(root.join("categoria", JoinType.LEFT).get("denominacion")), categoria.toLowerCase());
        };
    }

    public static Specification<Producto> nombreContains(String texto) {
        return (root, query, cb) -> {
            if (texto == null || texto.isBlank()) return null;
            String like = "%" + texto.toLowerCase() + "%";
            return cb.like(cb.lower(root.get("denominacion")), like);
        };
    }

    public static Specification<Producto> hasStockAvailable() {
        return (root, query, cb) -> {
            Path<Number> stockPath = root.get("stock");
            Expression<Number> stockCoalesced = cb.coalesce(stockPath, 0);
            return cb.gt(stockCoalesced, 0);
        };
    }

}
