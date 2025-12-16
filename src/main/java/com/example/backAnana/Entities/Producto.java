package com.example.backAnana.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "producto")
public class Producto extends Base {

    private String denominacion;
    private String marca;
    private String codigo;
    private String imagen;
    private Double precio;
    private Double costo;
    private Double porcentaje;
    private int stock;
    private int cantidadVendida;
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "categoriaId")
    @JsonIgnoreProperties("productos")
    private Categoria categoria;

    public Producto(String denominacion, String marca, String codigo, double precio) {
        this.denominacion = denominacion;
        this.marca = marca;
        this.codigo = codigo;
        this.precio = precio;
    }

    public void calcularPrecio() {
        if (costo != null && porcentaje != null) {
            this.precio = costo + (costo * porcentaje / 100);
        }
    }

}
