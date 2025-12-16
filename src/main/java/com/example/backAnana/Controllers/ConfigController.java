package com.example.backAnana.Controllers;

import com.example.backAnana.Services.Impl.ConfigServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/config")
@CrossOrigin(origins = "*")
public class ConfigController {

    private final ConfigServiceImpl configService;

    public ConfigController(ConfigServiceImpl configService) {
        this.configService = configService;
    }

    @GetMapping("/discount")
    public double getDiscount() {
        return configService.getDescuentoGlobal();
    }

    @PutMapping("/discount")
    public void setDiscount(@RequestBody Map<String, Double> body) {
        double porcentaje = body.get("porcentaje");
        configService.setDescuentoGlobal(porcentaje);
    }

}
