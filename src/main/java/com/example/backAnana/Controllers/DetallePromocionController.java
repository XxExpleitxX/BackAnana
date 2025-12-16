package com.example.backAnana.Controllers;

import com.example.backAnana.Entities.DetallePromocion;
import com.example.backAnana.Services.Impl.DetallePromocionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/detallePromocion")
public class DetallePromocionController extends BaseControllerImpl<DetallePromocion, DetallePromocionServiceImpl> {

    @Autowired
    private DetallePromocionServiceImpl detallePromocionServiceImpl;

    private DetallePromocionController(DetallePromocionServiceImpl service) {
        super(service);
    }

    @GetMapping("/promocion/{promocionId}")
    public ResponseEntity<List<DetallePromocion>> findAllByPromocionId(@PathVariable Long promocionId) throws Exception {
        List<DetallePromocion> detalles = detallePromocionServiceImpl.findAllByPromocionId(promocionId);
        return new ResponseEntity<>(detalles, HttpStatus.OK);
    }

}
