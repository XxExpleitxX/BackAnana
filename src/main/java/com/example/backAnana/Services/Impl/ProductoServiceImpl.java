package com.example.backAnana.Services.Impl;

import com.example.backAnana.Entities.Dtos.ProductoConDescuentoDTO;
import com.example.backAnana.Entities.Producto;
import com.example.backAnana.Repositories.ProductoRepository;
import com.example.backAnana.Services.FileStorageService;
import com.example.backAnana.Services.ProductoService;
import com.example.backAnana.Specifications.ProductoSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl extends BaseServiceImpl<Producto, Long> implements ProductoService {

    private final ProductoRepository repository;
    private final FileStorageService fileStorageService;

    @Autowired
    private ConfigServiceImpl configService;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif"
    );
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB

    @Autowired
    public ProductoServiceImpl(ProductoRepository repository,
                               FileStorageService fileStorageService) {
        super(repository);
        this.repository = repository;
        this.fileStorageService = fileStorageService;
    }

    public Producto save(Producto producto, MultipartFile imagenFile) throws Exception {
        validateProduct(producto);

        if (imagenFile != null && !imagenFile.isEmpty()) {
            validateImageFile(imagenFile);
            String fileName = storeProductImage(imagenFile);
            producto.setImagen(fileName);
        }

        producto.calcularPrecio();
        return super.save(producto);
    }

    private void validateProduct(Producto producto) {
        if (producto.getCosto() <= 0 || producto.getPorcentaje() <= 0) {
            throw new RuntimeException("Costo y porcentaje deben ser mayores a cero");
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new RuntimeException("La imagen no debe superar los 5MB");
        }

        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("Solo se permiten imágenes JPEG, PNG o GIF");
        }
    }

    private String storeProductImage(MultipartFile file) {
        try {
            return fileStorageService.storeFile(file);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen: " + e.getMessage());
        }
    }

    // Resto de los métodos existentes...
    public Producto updateStock(String codigo, int stock) {
        Producto producto = repository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con código: " + codigo));

        producto.setStock(producto.getStock() + stock);
        return repository.save(producto);
    }

    public void validarCodigoUnico(String codigo, Long idExcluir) {
        if (repository.existsByCodigo(codigo)) {
            if (idExcluir == null || !repository.findByCodigo(codigo).get().getId().equals(idExcluir)) {
                throw new RuntimeException("El código de producto ya está en uso");
            }
        }
    }

    public Producto addImageToProduct(String codigo, MultipartFile file) throws IOException {
        validateImageFile(file);

        Producto producto = repository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con código: " + codigo));

        String fileName = fileStorageService.storeFile(file);
        producto.setImagen(fileName);

        return repository.save(producto);
    }

    public Producto removeImageFromProduct(String codigo) {
        Producto producto = repository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con código: " + codigo));

        producto.setImagen(null);
        return repository.save(producto);
    }

    @Override
    public Producto findByCodigo(String codigo) {
        return repository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con código: " + codigo));
    }

    @Override
    public Producto findByDenominacion(String denominacion) {
        return repository.findByDenominacion(denominacion)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con denominación: " + denominacion));
    }

    public Page<ProductoConDescuentoDTO> getProductosConDescuento(
            int page,
            int size,
            String marca,
            String categoria,
            String busqueda,
            String ordenPrecio // "asc" | "desc" | null
    ) {
        double descuentoGlobal = configService.getDescuentoGlobal();

        // Sort por precio (precio original). Si querés ordenar por precio con descuento,
        // como el descuento es global, el orden es el mismo.
        Sort sort = Sort.by("precio");
        if ("desc".equalsIgnoreCase(ordenPrecio)) sort = sort.descending();
        else sort = sort.ascending();

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);

        Specification<Producto> spec = Specification.where(null);
        spec = spec.and(ProductoSpecifications.hasMarca(marca))
                .and(ProductoSpecifications.hasCategoriaDenominacion(categoria))
                .and(ProductoSpecifications.nombreContains(busqueda))
                .and(ProductoSpecifications.hasStockAvailable());

        Page<Producto> productosPage = repository.findAll(spec, pageable);

        // Mapear manteniendo la paginación
        Page<ProductoConDescuentoDTO> dtoPage = productosPage.map(prod -> {
            double precioOriginal = prod.getPrecio();
            int descCategoria = Optional.ofNullable(prod.getCategoria())
                    .map(c -> Math.max(0, c.getDescCategoria()))
                    .orElse(0);
            double descuentoAplicado = descCategoria > 0 ? descCategoria : descuentoGlobal;
            double precioFinal = precioOriginal * (1 - descuentoAplicado / 100.0);
            return new ProductoConDescuentoDTO(
                    prod.getId(),
                    prod.getDenominacion(),
                    prod.getMarca(),
                    prod.getCodigo(),
                    precioOriginal,
                    precioFinal,
                    prod.getImagen(),
                    prod.getCategoria(),
                    descCategoria,
                    prod.getStock()
            );
        });

        return dtoPage;
    }

    public List<String> getMarcas() {
        return repository.findDistinctMarcas()
                .stream()
                .filter(m -> m != null && !m.isBlank())
                .map(m -> m.substring(0, 1).toUpperCase() + m.substring(1).toLowerCase())
                .distinct()
                .sorted(String::compareToIgnoreCase)
                .toList();
    }

    /*public List<ProductoConDescuentoDTO> getProductosConDescuento() {
        double descuento = configService.getDescuentoGlobal();

        return repository.findAll().stream()
                .map(prod -> {
                    double precioOriginal = prod.getPrecio();
                    double precioFinal = precioOriginal * (1 - descuento / 100);
                    return new ProductoConDescuentoDTO(
                            prod.getId(),
                            prod.getDenominacion(),
                            prod.getMarca(),
                            prod.getCodigo(),
                            precioOriginal,
                            precioFinal,
                            prod.getImagen(),
                            prod.getCategoria(),
                            prod.getStock()
                    );
                })
                .toList();
    }*/
}
