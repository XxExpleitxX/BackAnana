package com.example.backAnana.Controllers;

import com.example.backAnana.Entities.Categoria;
import com.example.backAnana.Services.Impl.CategoriaServiceImpl;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/categoria")
public class CategoriaController extends BaseControllerImpl<Categoria, CategoriaServiceImpl> {

    private CategoriaController(CategoriaServiceImpl service) {
        super(service);
    }

}
