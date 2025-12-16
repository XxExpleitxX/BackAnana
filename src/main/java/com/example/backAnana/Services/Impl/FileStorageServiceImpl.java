package com.example.backAnana.Services.Impl;

import com.example.backAnana.Services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;

    // Inyectamos la ruta desde application.properties
    public FileStorageServiceImpl(@Value("${app.upload.dir}") String uploadDir) {
        // Validamos que la ruta no esté vacía
        if(uploadDir == null || uploadDir.trim().isEmpty()) {
            throw new RuntimeException("La ruta de almacenamiento no está configurada (app.upload.dir)");
        }

        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear el directorio para guardar archivos en: " +
                    this.fileStorageLocation, ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) throws IOException {
        // Normaliza el nombre del archivo
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        // Valida el nombre del archivo
        if(fileName.contains("..")) {
            throw new RuntimeException("Nombre de archivo no válido: " + fileName);
        }

        // Copia el archivo a la ubicación objetivo
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }
}
