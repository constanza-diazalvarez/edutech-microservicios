package com.edutech.configuration;

import com.edutech.model.Rol;
import com.edutech.model.Usuario;
import com.edutech.repository.RolRepository;
import com.edutech.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import net.datafaker.Faker;

import java.util.Locale;
import java.util.Optional;

@Profile("test")
@Component
@Transactional
public class DataLoaderUsuarios implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Override
    public void run(String... args) throws Exception {
        // cargar roles básicos si no existen
        cargarRolesBasicos();

        // generar usuarios de prueba
        generarUsuarios();
    }

    private void cargarRolesBasicos() {
        crearRolSiNoExiste("ADMIN");
        crearRolSiNoExiste("GERENTE_CURSOS");
        crearRolSiNoExiste("INSTRUCTOR");
        crearRolSiNoExiste("ESTUDIANTE");
        crearRolSiNoExiste("SOPORTE");
    }

    private void crearRolSiNoExiste(String nombreRol) {
        if (!rolRepository.findByRol(nombreRol).isPresent()) {
            Rol rol = Rol.builder().rol(nombreRol).build(); // Deja que JPA asigne el ID
            rolRepository.save(rol);
        }
    }

    private void generarUsuarios() {
        Faker faker = new Faker(new Locale("es"));

        for (int i = 0; i < 20; i++) {
            Usuario usuario = new Usuario();
            String nombre = faker.name().firstName();
            String apellido = faker.name().lastName();

            usuario.setNombre(nombre + " " + apellido);
            usuario.setCorreo(generarMailUnico(nombre + apellido.toLowerCase() + "@gmail.com"));
            usuario.setPassword(faker.internet().password(6, 8, false, false, false));//LargoMinimo-LargoMaximo-Mayusculas-LetrasEspeciales-Digitos

            // Asignar rol aleatorio (1 a 5)
            int rolId = faker.number().numberBetween(1, 6);
            Optional<Rol> rol = rolRepository.findById(rolId);//asigna un valor aleatorio para el rol
            if (rol == null) {
                throw new RuntimeException("Rol no encontrado");
            }
            usuario.setRol(rol.get());//se obtiene la entidad rol dentro del optional
            usuarioRepository.save(usuario);
        }
        System.out.println("20 usuarios generados");
    }

    private String generarMailUnico(String baseMail) {
        String email = baseMail;
        int contador = 1;
        while(usuarioRepository.findByCorreo(email).isPresent()) {
            email = baseMail.replace("@", contador + "@");
            contador++;
        }
        return email;
    }
}