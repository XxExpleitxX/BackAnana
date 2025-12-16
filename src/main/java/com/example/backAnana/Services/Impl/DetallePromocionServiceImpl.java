package com.example.backAnana.Services.Impl;

import com.example.backAnana.Entities.DetallePromocion;
import com.example.backAnana.Entities.Producto;
import com.example.backAnana.Entities.Promocion;
import com.example.backAnana.Repositories.DetallePromocionRepository;
import com.example.backAnana.Repositories.ProductoRepository;
import com.example.backAnana.Repositories.PromocionRepository;
import com.example.backAnana.Services.DetallePromocionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DetallePromocionServiceImpl extends BaseServiceImpl<DetallePromocion, Long> implements DetallePromocionService {

    @Autowired
    private DetallePromocionRepository repository;

    @Autowired
    private PromocionRepository promocionRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public DetallePromocionServiceImpl(DetallePromocionRepository repository) {
        super(repository);
    }

    @Transactional
    public List<DetallePromocion> findAllByPromocionId(Long id) throws Exception{
        return repository.findAllByPromocionId(id);
    }

    @Override
    @Transactional
    public DetallePromocion save(DetallePromocion detalle) {
        Promocion promocion = promocionRepository.findById(detalle.getPromocion().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Promoción no encontrada"));

        Producto producto = productoRepository.findById(detalle.getProducto().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        detalle.setPromocion(promocion);
        detalle.setProducto(producto);

        DetallePromocion guardado = repository.save(detalle);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado).getBody();
    }

}
