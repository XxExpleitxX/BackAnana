package com.example.backAnana;

import com.example.backAnana.Entities.Categoria;
import com.example.backAnana.Entities.Enums.RolesUsuario;
import com.example.backAnana.Entities.Producto;
import com.example.backAnana.Entities.User;
import com.example.backAnana.Repositories.CategoriaRepository;
import com.example.backAnana.Repositories.ProductoRepository;
import com.example.backAnana.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackAnanaApplication {

	/*@Autowired
	CategoriaRepository categoriaRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ProductoRepository productoRepository;*/

	public static void main(String[] args) {
		SpringApplication.run(BackAnanaApplication.class, args);
		System.out.println("¡La aplicación se ha iniciado correctamente!");
	}

	@Bean
	@Transactional
	CommandLineRunner init(CategoriaRepository categoriaRepository, UserRepository userRepository, ProductoRepository productoRepository) {
		return args -> {
			/*User admin = User.builder().usuario("Admin").rol(RolesUsuario.ADMIN).build();
			admin.setClave("admin_123");

			userRepository.save(admin);

			Categoria jugueteria = Categoria.builder().denominacion("Articulos de juguetería").build();
			Categoria libreria = Categoria.builder().denominacion("Articulos de libreria").build();

			categoriaRepository.save(jugueteria);
			categoriaRepository.save(libreria);

			Producto bibliorato = Producto.builder()
					.denominacion("Bibliorato The Pel")
					.marca("The Pel").codigo("codigo_1")
					.imagen("bibliorato.png")
					.precio(4500.00)
					.stock(5)
					.cantidadVendida(3)
					.descripcion("Descripcion del producto")
					.build();

			bibliorato.setCategoria(libreria);

			productoRepository.save(bibliorato);

			Producto resaltador = Producto.builder()
					.denominacion("Resaltador Filgo")
					.marca("Filgo")
					.codigo("codigo_2")
					.imagen("resaltador.png")
					.precio(1300.00)
					.stock(50)
					.cantidadVendida(16)
					.descripcion("Descripcion del producto")
					.build();

			resaltador.setCategoria(libreria);

			productoRepository.save(resaltador);

			Producto tren = Producto.builder()
					.denominacion("Tren eléctrico Cat")
					.marca("Cat")
					.codigo("codigo_3")
					.imagen("tren.png")
					.precio(110000.00)
					.stock(5)
					.cantidadVendida(1)
					.descripcion("Descripcion del producto")
					.build();

			tren.setCategoria(jugueteria);

			productoRepository.save(tren);

			Producto plasticola = Producto.builder()
					.denominacion("Plasticola Boligoma")
					.marca("Boligoma")
					.codigo("codigo_4")
					.imagen("plasticola.png")
					.precio(800.00)
					.stock(33)
					.cantidadVendida(27)
					.descripcion("Descripcion del producto")
					.build();

			plasticola.setCategoria(libreria);

			productoRepository.save(plasticola);*/

		};
	}
}
