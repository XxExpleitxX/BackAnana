package com.example.backAnana.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(name = "detalleVenta")
public class DetalleVenta extends Base{

    private int cantidad;
    private Double precioAplicado;

    @ManyToOne
    @JoinColumn(name = "ventaId")
    @JsonIgnoreProperties("detalleVentas")
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "productoId")
    @JsonIgnoreProperties(value = {"detalleVentas"}, allowSetters = true)
    private Producto producto;

    //Método subtotal
    public Double getSubTotal() {
        if (precioAplicado != null) {
            return cantidad * precioAplicado;
        } else {
            return 0.0;
        }
    }

}
