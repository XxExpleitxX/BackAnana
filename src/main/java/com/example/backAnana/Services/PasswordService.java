package com.example.backAnana.Services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;

@Service
public class PasswordService {

    private final BCryptPasswordEncoder encoder;

    public PasswordService() {
        // strength 10 por defecto (podés subirlo para mayor seguridad)
        this.encoder = new BCryptPasswordEncoder();
    }

    /** Hashear con BCrypt */
    public String encodeBCrypt(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /** Verifica: primero intenta con BCrypt, si no, con SHA-1 (legacy) */
    public boolean matches(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) return false;

        if (isBCrypt(storedHash)) {
            return encoder.matches(rawPassword, storedHash);
        } else {
            return sha1Hex(rawPassword).equalsIgnoreCase(storedHash);
        }
    }

    /** Detecta si un hash es BCrypt */
    public boolean isBCrypt(String hash) {
        if (hash == null) return false;
        return hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$");
    }

    /** SHA-1 en hexadecimal (solo para compatibilidad) */
    public String sha1Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generando SHA-1", e);
        }
    }
}
