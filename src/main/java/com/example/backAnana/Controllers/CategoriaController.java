package com.example.backAnana.Controllers;

import com.example.backAnana.Entities.Categoria;
import com.example.backAnana.Entities.Dtos.CategoriaDTO;
import com.example.backAnana.Services.Impl.CategoriaServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/categoria")
public class CategoriaController extends BaseControllerImpl<Categoria, CategoriaServiceImpl> {

    private CategoriaServiceImpl categoriaService;

    private CategoriaController(CategoriaServiceImpl service) {
        super(service);
    }

    @PutMapping
    public ResponseEntity<?> update(Long id, CategoriaDTO dto) throws Exception {
        Categoria categoria = categoriaService.findById(id);
        categoria.setDenominacion(dto.getDenominacion());
        return super.update(id, categoria);
    }
}
