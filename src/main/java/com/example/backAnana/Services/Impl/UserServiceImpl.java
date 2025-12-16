package com.example.backAnana.Services.Impl;

import com.example.backAnana.Entities.User;
import com.example.backAnana.Repositories.UserRepository;
import com.example.backAnana.Services.PasswordService;
import com.example.backAnana.Services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, Long> implements UserService {

    private final UserRepository repository;
    private final PasswordService passwordService;

    public UserServiceImpl(UserRepository repository, PasswordService passwordService) {
        super(repository);
        this.repository = repository;
        this.passwordService = passwordService;
    }

    @Override
    @Transactional
    public User save(User entity) throws Exception {
        User existente = repository.findByUsuario(entity.getUsuario());
        if (existente != null && (entity.getId() == null || !existente.getId().equals(entity.getId()))) {
            throw new Exception("El nombre de usuario ya existe");
        }

        if (entity.getId() == null) {
            // Nuevo usuario → siempre hashear con BCrypt
            if (entity.getClave() == null || entity.getClave().isBlank()) {
                throw new Exception("La contraseña es obligatoria");
            }
            entity.setClave(passwordService.encodeBCrypt(entity.getClave()));
        } else {
            // Actualización
            User existingUser = repository.findById(entity.getId())
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));

            if (entity.getClave() != null && !entity.getClave().isBlank()) {
                // Nueva clave → rehashear con BCrypt
                entity.setClave(passwordService.encodeBCrypt(entity.getClave()));
            } else {
                // Mantener la clave existente
                entity.setClave(existingUser.getClave());
            }
        }

        return super.save(entity);
    }

    /**
     * Autenticación con migración progresiva de SHA-1 → BCrypt
     */
    @Transactional
    public User authenticate(String usuario, String passwordPlain) {
        User user = repository.findByUsuario(usuario);
        if (user == null) return null;

        if (!passwordService.matches(passwordPlain, user.getClave())) return null;

        // Migrar SHA-1 a BCrypt si corresponde
        migrateSha1ToBcrypt(user, passwordPlain);

        return user;
    }

    private void migrateSha1ToBcrypt(User user, String passwordPlain) {
        if (!passwordService.isBCrypt(user.getClave())) {
            user.setClave(passwordService.encodeBCrypt(passwordPlain));
            repository.save(user);
        }
    }
}
