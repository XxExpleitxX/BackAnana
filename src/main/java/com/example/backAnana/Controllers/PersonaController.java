package com.example.backAnana.Controllers;

import com.example.backAnana.Entities.Persona;
import com.example.backAnana.Services.Impl.PersonaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/persona")
public class PersonaController extends BaseControllerImpl<Persona, PersonaServiceImpl> {

    @Autowired
    private PersonaServiceImpl personaServiceImpl;

    private PersonaController(PersonaServiceImpl service) {
        super(service);
    }

    @GetMapping("/user/{userId}")
    public Optional<Persona> findByUserId(@PathVariable Long userId) throws Exception {
        Persona persona = personaServiceImpl.findByUserId(userId);
        return Optional.ofNullable(persona);
    }

}
