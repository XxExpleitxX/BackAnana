package com.example.backAnana.Entities.Mappers;

import com.example.backAnana.Entities.Domicilio;
import com.example.backAnana.Entities.Dtos.DomicilioDTO;
import com.example.backAnana.Entities.Enums.Localidad;
import com.example.backAnana.Entities.Enums.Provincia;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class DomicilioMapper {

    // Convierte DTO → Entidad
    public Domicilio toEntity(DomicilioDTO dto) {
        if (dto == null) return null;

        Domicilio domicilio = new Domicilio();
        domicilio.setId(dto.getId());
        domicilio.setCalle(dto.getCalle());
        domicilio.setNumero(dto.getNumero() != null ? Integer.parseInt(String.valueOf(dto.getNumero())) : 0);

        try {
            if (dto.getLocalidad() != null)
                domicilio.setLocalidad(Localidad.valueOf(dto.getLocalidad().toUpperCase()).getCode());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Localidad inválida: " + dto.getLocalidad());
        }

        try {
            if (dto.getProvincia() != null)
                domicilio.setProvincia(Provincia.valueOf(dto.getProvincia().toUpperCase()).getCode());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Provincia inválida: " + dto.getProvincia());
        }

        return domicilio;
    }

    // Convierte Entidad → DTO
    public DomicilioDTO toDTO(Domicilio entity) {
        if (entity == null) return null;

        DomicilioDTO dto = new DomicilioDTO();
        dto.setId(entity.getId());
        dto.setCalle(entity.getCalle());
        dto.setNumero(entity.getNumero());

        try {
            dto.setLocalidad(Localidad.fromCode(entity.getLocalidad()).name());
        } catch (IllegalArgumentException e) {
            dto.setLocalidad(null);
        }

        try {
            dto.setProvincia(Provincia.fromCode(entity.getProvincia()).name());
        } catch (IllegalArgumentException e) {
            dto.setProvincia(null);
        }

        return dto;
    }

    // Convierte lista de Entidades → lista de DTOs
    public List<DomicilioDTO> toDTOList(List<Domicilio> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
}