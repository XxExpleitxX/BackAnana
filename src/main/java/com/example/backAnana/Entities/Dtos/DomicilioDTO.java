package com.example.backAnana.Entities.Dtos;

import com.example.backAnana.Entities.Domicilio;
import com.example.backAnana.Entities.Enums.Localidad;
import com.example.backAnana.Entities.Enums.Provincia;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DomicilioDTO {
    private Long id;
    private String calle;
    private Integer numero;
    private String localidad; // Ej: "CAPITAL"
    private String provincia; // Ej: "MENDOZA"
}
