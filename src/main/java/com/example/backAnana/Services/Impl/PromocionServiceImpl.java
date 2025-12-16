package com.example.backAnana.Services.Impl;

import com.example.backAnana.Entities.Promocion;
import com.example.backAnana.Repositories.PromocionRepository;
import com.example.backAnana.Services.PromocionService;
import org.springframework.stereotype.Service;

@Service
public class PromocionServiceImpl extends BaseServiceImpl<Promocion, Long> implements PromocionService {

    public PromocionServiceImpl(PromocionRepository repository) {
        super(repository);
    }

}
