package com.example.backAnana.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Config extends Base{

    // Porcentaje de descuento global
    @Column(nullable = false)
    private double descuentoGlobal;

}
