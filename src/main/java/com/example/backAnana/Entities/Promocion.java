package com.example.backAnana.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "promocion")
public class Promocion extends Base{

    private String denominacion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String descripcion;
    private Double precioPromocional;

    @OneToMany(mappedBy = "promocion", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Builder.Default
    @JsonIgnoreProperties("promocion")
    private Set<DetallePromocion> detallePromociones = new HashSet<>();

}
