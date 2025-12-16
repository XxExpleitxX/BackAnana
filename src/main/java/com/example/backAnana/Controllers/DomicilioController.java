package com.example.backAnana.Controllers;

import com.example.backAnana.Entities.Domicilio;
import com.example.backAnana.Entities.Dtos.DomicilioDTO;
import com.example.backAnana.Services.Impl.DomicilioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/domicilio")
public class DomicilioController {

    private final DomicilioServiceImpl servicio;

    @Autowired
    public DomicilioController(DomicilioServiceImpl servicio) {
        this.servicio = servicio;
    }

    // GET ALL → lista de DTOs
    @GetMapping("")
    public ResponseEntity<List<DomicilioDTO>> getAll() {
        try {
            List<DomicilioDTO> listaDTO = servicio.toDTOList(servicio.findAll());
            return ResponseEntity.ok(listaDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    // GET ONE → DTO
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<DomicilioDTO> getOne(@PathVariable Long id) {
        try {
            Domicilio domicilio = servicio.findById(id);
            return ResponseEntity.ok(servicio.toDTO(domicilio));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    // POST → recibe DTO
    @PostMapping("")
    public ResponseEntity<DomicilioDTO> save(@RequestBody DomicilioDTO dto) {
        try {
            Domicilio domicilio = servicio.saveFromDTO(dto);
            return ResponseEntity.ok(servicio.toDTO(domicilio));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // PUT → recibe DTO
    @PutMapping("/{id:[0-9]+}")
    public ResponseEntity<DomicilioDTO> update(@PathVariable Long id, @RequestBody DomicilioDTO dto) {
        try {
            dto.setId(id);
            Domicilio domicilio = servicio.updateFromDTO(dto);
            return ResponseEntity.ok(servicio.toDTO(domicilio));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // DELETE
    @DeleteMapping("/{id:[0-9]+}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            servicio.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
