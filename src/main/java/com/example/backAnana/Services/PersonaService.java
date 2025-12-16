package com.example.backAnana.Services;

import com.example.backAnana.Entities.Persona;

public interface PersonaService extends BaseService<Persona, Long> {

    Persona findByUserId(Long id) throws Exception;

}
