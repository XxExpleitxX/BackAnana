package com.example.backAnana.Entities.Dtos;

import com.example.backAnana.Entities.Enums.RolesUsuario;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    private Long id;
    private String usuario;
    private String message;
    private RolesUsuario rol;

    public LoginResponse(Long id, String usuario, String message, RolesUsuario rol) {
        this.id = id;
        this.usuario = usuario;
        this.message = message;
        this.rol = rol;
    }

}
