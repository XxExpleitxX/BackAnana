package com.example.backAnana.Entities.Enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Provincia {
    BUENOS_AIRES(1, "Buenos Aires"),
    CATAMARCA(2, "Catamarca"),
    CHACO(3, "Chaco"),
    CHUBUT(4, "Chubut"),
    CIUDAD_AUTONOMA_BUENOS_AIRES(5, "Ciudad Autónoma de Buenos Aires"),
    CORDOBA(6, "Córdoba"),
    CORRIENTES(7, "Corrientes"),
    ENTRE_RIOS(8, "Entre Ríos"),
    FORMOSA(9, "Formosa"),
    JUJUY(10, "Jujuy"),
    LA_PAMPA(11, "La Pampa"),
    LA_RIOJA(12, "La Rioja"),
    MENDOZA(13, "Mendoza"),
    MISIONES(14, "Misiones"),
    NEUQUEN(15, "Neuquén"),
    RIO_NEGRO(16, "Río Negro"),
    SALTA(17, "Salta"),
    SAN_JUAN(18, "San Juan"),
    SAN_LUIS(19, "San Luis"),
    SANTA_CRUZ(20, "Santa Cruz"),
    SANTA_FE(21, "Santa Fe"),
    SANTIAGO_DEL_ESTERO(22, "Santiago del Estero"),
    TIERRA_DEL_FUEGO(23, "Tierra del Fuego, Antártida e Islas del Atlántico Sur"),
    TUCUMAN(24, "Tucumán");

    private final int code;
    private final String nombreCompleto;

    Provincia(int code, String nombreCompleto) {
        this.code = code;
        this.nombreCompleto = nombreCompleto;
    }

    public int getCode() { return code; }
    public String getNombreCompleto() { return nombreCompleto; }

    // Para serializar: devolvemos key + nombre
    @JsonValue
    public Object toJson() {
        return new ProvinciaJson(this.name(), this.nombreCompleto);
    }

    // Para deserializar: recibir "MENDOZA" → Provincia.MENDOZA
    @JsonCreator
    public static Provincia fromJson(String value) {
        return Provincia.valueOf(value.toUpperCase());
    }

    // Para mapear int de la BD a enum
    public static Provincia fromCode(int code) {
        for (Provincia p : Provincia.values()) {
            if (p.code == code) return p;
        }
        throw new IllegalArgumentException("Código inválido de provincia: " + code);
    }

    private record ProvinciaJson(String key, String nombre) {}
}