package com.example.backAnana.Entities.Dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
public class CierreCajaDTO {

    private LocalDate desde;
    private LocalDate hasta;
    private double totalGeneral;
    @Builder.Default
    private Map<String, Double> totalPorFormaPago = new HashMap<>();

    public void agregarFormaPago(String formaPago, double monto) {
        totalPorFormaPago.merge(formaPago, monto, Double::sum);
        totalGeneral += monto;
    }

}
