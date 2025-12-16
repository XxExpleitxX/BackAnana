package com.example.backAnana.Services.Impl;

import com.example.backAnana.Entities.Domicilio;
import com.example.backAnana.Entities.Dtos.DomicilioDTO;
import com.example.backAnana.Entities.Enums.Localidad;
import com.example.backAnana.Entities.Enums.Provincia;
import com.example.backAnana.Entities.Mappers.DomicilioMapper;
import com.example.backAnana.Entities.Persona;
import com.example.backAnana.Entities.User;
import com.example.backAnana.Repositories.PersonaRepository;
import com.example.backAnana.Services.PersonaService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonaServiceImpl extends BaseServiceImpl<Persona, Long> implements PersonaService {

    @Autowired
    private PersonaRepository repository;
    @Autowired
    private DomicilioServiceImpl domicilioService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private DomicilioMapper domicilioMapper;

    public PersonaServiceImpl(PersonaRepository repository) {
        super(repository);
    }

    @Override
    @Transactional
    public Persona save(Persona entity) throws Exception {
        // Manejar domicilio
        if (entity.getDomicilio() != null) {
            DomicilioDTO dto = domicilioMapper.toDTO(entity.getDomicilio());

            Domicilio domicilioGuardado =
                    (dto.getId() == null)
                            ? domicilioService.saveFromDTO(dto)
                            : domicilioService.updateFromDTO(dto);

            entity.setDomicilio(domicilioGuardado);
        }

        // Manejar usuario
        if (entity.getUser() != null) {
            User userGuardado = userService.save(entity.getUser());
            entity.setUser(userGuardado);
        }

        return super.save(entity);
    }

    @Override
    @Transactional
    public Persona update(Persona entity) throws Exception {
        if (entity.getId() == null) {
            throw new Exception("La Persona a actualizar debe tener un ID.");
        }

        return save(entity);
    }

    @Transactional
    public Persona findByUserId(Long id) throws Exception{
        return repository.findByUserId(id);
    }

}
