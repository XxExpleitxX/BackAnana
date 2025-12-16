package com.example.backAnana.Services.Impl;

import com.example.backAnana.Entities.Categoria;
import com.example.backAnana.Repositories.CategoriaRepository;
import com.example.backAnana.Services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoriaServiceImpl extends BaseServiceImpl<Categoria, Long> implements CategoriaService {

    @Autowired
    private CategoriaRepository repository;

    public CategoriaServiceImpl(CategoriaRepository repository) {
        super(repository);
    }

}
