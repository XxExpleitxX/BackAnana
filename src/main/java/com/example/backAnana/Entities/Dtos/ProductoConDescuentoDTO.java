package com.example.backAnana.Entities.Dtos;

import com.example.backAnana.Entities.Categoria;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductoConDescuentoDTO {
    private Long id;
    private String denominacion;
    private String marca;
    private String codigo;
    private double precioOriginal;
    private double precioConDescuento;
    private String imagen;
    private Categoria categoria;
    private int stock;
}
