package com.example.backAnana.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "persona")
public class Persona extends Base {

    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Domicilio domicilio;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "usuario_id")
    private User user;

}
