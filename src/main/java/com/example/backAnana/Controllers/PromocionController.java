package com.example.backAnana.Controllers;

import com.example.backAnana.Entities.Promocion;
import com.example.backAnana.Services.Impl.PromocionServiceImpl;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/promocion")
public class PromocionController extends BaseControllerImpl<Promocion, PromocionServiceImpl> {

    public PromocionController(PromocionServiceImpl service) {
        super(service);
    }

}
