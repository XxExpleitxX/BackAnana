package com.example.backAnana.Entities.Enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Localidad {
    CAPITAL(1, "Ciudad de Mendoza"),
    GODOY_CRUZ(2, "Godoy Cruz"),
    GUAYMALLEN(3, "Guaymallén"),
    LAS_HERAS(4, "Las Heras"),
    LUJAN_DE_CUYO(5, "Luján de Cuyo"),
    MAIPU(6, "Maipú"),
    MENDOZA(7, "Mendoza"),
    SAN_MARTIN(8, "San Martín"),
    SAN_RAFAEL(9, "San Rafael"),
    TUNUYAN(10, "Tunuyán"),
    TUPUNGATO(11, "Tupungato"),
    GENERAL_ALVEAR(12, "General Alvear"),
    JUNIN(13, "Junín"),
    LA_PAZ(14, "La Paz"),
    LAVALLE(15, "Lavalle"),
    MALARGUE(16, "Malargüe"),
    RIVADAVIA(17, "Rivadavia"),
    SAN_CARLOS(18, "San Carlos"),
    SANTA_ROSA(19, "Santa Rosa");

    private final int code;
    private final String nombre;

    Localidad(int code, String nombre) {
        this.code = code;
        this.nombre = nombre;
    }

    public int getCode() { return code; }
    public String getNombre() { return nombre; }

    // 👇 cuando se serializa se devuelve un objeto JSON con key y nombre
    @JsonValue
    public Object toJson() {
        return new LocalidadJson(this.name(), this.nombre);
    }

    // 👇 cuando se recibe un string como "CAPITAL" se convierte a enum
    @JsonCreator
    public static Localidad fromJson(String value) {
        return Localidad.valueOf(value.toUpperCase());
    }

    public static Localidad fromCode(int code) {
        for (Localidad l : Localidad.values()) {
            if (l.code == code) return l;
        }
        throw new IllegalArgumentException("Código inválido de localidad: " + code);
    }

    // record para devolver key + nombre
    private record LocalidadJson(String key, String nombre) {}
}