package com.example.backAnana.Entities.Dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalleVentaDTO {
    private int cantidad;
    private Double precioAplicado;
    private Long productoId;
    private Long ventaId;
}
