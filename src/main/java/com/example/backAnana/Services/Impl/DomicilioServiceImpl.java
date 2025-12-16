package com.example.backAnana.Services.Impl;

import com.example.backAnana.Entities.Domicilio;
import com.example.backAnana.Entities.Dtos.DomicilioDTO;
import com.example.backAnana.Entities.Enums.Localidad;
import com.example.backAnana.Entities.Enums.Provincia;
import com.example.backAnana.Entities.Mappers.DomicilioMapper;
import com.example.backAnana.Repositories.DomicilioRepository;
import com.example.backAnana.Services.DomicilioService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DomicilioServiceImpl extends BaseServiceImpl<Domicilio, Long> implements DomicilioService {

    private final DomicilioMapper domicilioMapper;

    public DomicilioServiceImpl(DomicilioRepository repository, DomicilioMapper domicilioMapper) {
        super(repository);
        this.domicilioMapper = domicilioMapper;
    }

    @Transactional
    public Domicilio saveFromDTO(DomicilioDTO dto) throws Exception {
        try {
            Domicilio domicilio = domicilioMapper.toEntity(dto);
            return super.save(domicilio);
        } catch (IllegalArgumentException e) {
            throw new Exception("Localidad o Provincia inválida: " + e.getMessage());
        }
    }

    @Transactional
    public Domicilio updateFromDTO(DomicilioDTO dto) throws Exception {
        if (dto.getId() == null) {
            throw new Exception("No se puede actualizar un domicilio sin ID");
        }

        Domicilio existente = baseRepository.findById(dto.getId())
                .orElseThrow(() -> new Exception("Domicilio no encontrado"));

        // Mapear los nuevos datos del DTO sobre la entidad existente
        Domicilio actualizado = domicilioMapper.toEntity(dto);
        actualizado.setId(existente.getId()); // aseguramos conservar el ID

        return super.update(actualizado);
    }

    public DomicilioDTO toDTO(Domicilio domicilio) {
        return domicilioMapper.toDTO(domicilio);
    }

    public List<DomicilioDTO> toDTOList(List<Domicilio> domicilios) {
        return domicilioMapper.toDTOList(domicilios);
    }

}
