package com.example.backAnana.Entities;

import com.example.backAnana.Entities.Enums.RolesUsuario;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "user")
public class User extends Base {

    @Column(nullable = false, unique = true)
    private String usuario;

    @Column(nullable = false)
    private String clave;

    @Enumerated(EnumType.STRING)
    private RolesUsuario rol;

}
