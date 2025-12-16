package com.example.backAnana.Controllers;

import com.example.backAnana.Entities.Dtos.LoginRequest;
import com.example.backAnana.Entities.Dtos.LoginResponse;
import com.example.backAnana.Entities.User;
import com.example.backAnana.Services.Impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserServiceImpl userServiceImpl;

    public AuthController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Login attempt for user: {}", loginRequest.getUsuario());

            if (loginRequest.getUsuario() == null || loginRequest.getUsuario().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Usuario es requerido");
                return ResponseEntity.badRequest().body(response);
            }

            if (loginRequest.getClave() == null || loginRequest.getClave().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Contraseña es requerida");
                return ResponseEntity.badRequest().body(response);
            }

            User user = userServiceImpl.authenticate(
                    loginRequest.getUsuario().trim(),
                    loginRequest.getClave()
            );

            if (user != null) {
                logger.info("Login successful for user: {}", user.getUsuario());
                response.put("success", true);
                response.put("message", "Login exitoso");
                response.put("data", new LoginResponse(
                        user.getId(),
                        user.getUsuario(),
                        "Login exitoso",
                        user.getRol()
                ));
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Login failed for user: {}", loginRequest.getUsuario());
                response.put("success", false);
                response.put("message", "Usuario o contraseña incorrectos");
                return ResponseEntity.status(401).body(response);
            }

        } catch (Exception e) {
            logger.error("Internal server error during login", e);
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(500).body(response);
        }
    }
}
