package com.example.backAnana.Controllers;

import com.example.backAnana.Entities.PreferenceMP;
import com.example.backAnana.Entities.Venta;
import com.example.backAnana.Services.Impl.VentaServiceImpl;
import com.example.backAnana.Services.MPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/mercadoPago")
public class MPController {

    @Autowired
    private MPService mercadoPagoService;

    @Autowired
    private VentaServiceImpl ventaService;

    @PostMapping("/createPreference")
    public ResponseEntity<?> createPreference(@RequestBody Map<String, Object> body) {
        try {
            Long id = Long.valueOf(body.get("id").toString());
            Optional<Venta> ventaOptional = Optional.ofNullable(ventaService.findById(id));

            if (ventaOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("Venta no encontrada");
            }

            PreferenceMP preference = mercadoPagoService.createPreference(ventaOptional.get());

            if (preference == null) {
                return ResponseEntity.status(500).body("Error al generar preferencia");
            }

            return ResponseEntity.ok(preference);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> recibirWebhook(
            @RequestParam(name = "id") String id,
            @RequestParam(name = "topic") String topic) {
        String resultado = mercadoPagoService.procesarWebhook(topic, id);
        return ResponseEntity.ok(resultado);
    }

}
