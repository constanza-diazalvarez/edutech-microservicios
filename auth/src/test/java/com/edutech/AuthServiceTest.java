package com.edutech.controller;

import com.edutech.dto.LoginRequest;
import com.edutech.dto.LoginResponse;
import com.edutech.model.Rol;
import com.edutech.model.Usuario;
import com.edutech.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import utils.JwtUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest // Anotación que carga el contexto completo de Spring para la prueba
public class AuthServiceTest {

    // Inyectamos el controlador real que queremos probar
    @Autowired
    private AuthController authController;

    // Creamos un mock del servicio de usuario para simular su comportamiento
    @MockBean
    private UsuarioService usuarioService;
    @Test
    public void pruebaLoginExitoso() {
        // crear rol de administrador
        Rol rolAdmin = Rol.builder()
                .id(1)
                .rol("ADMIN")
                .build();

        // un usuario mock (simulado) para la prueba
        Usuario usuarioMock = Usuario.builder()
                .id(1)
                .nombre("admin")
                .password("123456") // contraseña "correcta"
                .rol(rolAdmin)
                .build();

        //la solicitud de login que se envia
        LoginRequest request = new LoginRequest();
        request.setNombre("admin");
        request.setPassword("123456");

        //simular el comportamiento del servicio
        // cuando alguien llame a findByNombre("admin"), devolvera el usuario mock
        when(usuarioService.findByNombre("admin")).thenReturn(Optional.of(usuarioMock));
        // llamar al metodo real a probar
        LoginResponse response = authController.login(request);

        // verificaciones
        // el response no debe ser nulo
        assertNotNull(response);

        // el token generado debe ser válido = verificar con JwtUtil
        assertTrue(JwtUtil.validarToken(response.getToken(), "admin"));

        // el rol en la respuesta debe ser "ADMIN"
        assertEquals("ADMIN", response.getRol());

        // el ID de usuario debe ser 1
        assertEquals(1, response.getIdUsuario());
    }

    @Test
    public void pruebaLoginContraseñaIncorrecta() {
        // crear usuario
        Usuario usuarioMock = Usuario.builder()//builder genera automáticamente un "constructor paso a paso".
                .nombre("usuario")
                .password("contraseñaCorrecta") // contraseña guardada en DB
                .build();
        // solicitud de login que se envia
        LoginRequest request = new LoginRequest();
        request.setNombre("usuario");
        request.setPassword("contraseñaIncorrecta"); // Contraseña que se envia

        // mock = qué debe devolver el servicio mockeado.
        when(usuarioService.findByNombre("usuario")).thenReturn(Optional.of(usuarioMock));

        // debe lanzar una excepción cuando la contraseña no coincide
        assertThrows(RuntimeException.class, () -> {
            authController.login(request);
        });
    }

    @Test
    public void pruebaLoginUsuarioNoEncontrado() {
        // simular que no encuentra el usuario
        when(usuarioService.findByNombre("inexistente")).thenReturn(Optional.empty());
        // solicitud de login que se envia
        LoginRequest request = new LoginRequest();
        request.setNombre("inexistente");
        request.setPassword("cualquiercontraseña");
        // debe lanzar una excepción cuando el usuario no existe
        assertThrows(RuntimeException.class, () -> {
            authController.login(request);
        });
    }

    @Test
    public void pruebaRegistrarUsuario() {
        Usuario nuevoUsuario = Usuario.builder()
                .nombre("nuevoUsuario")
                .password("pass123")
                .build();

        // cuando llama a save, devolvera el mismo usuario
        when(usuarioService.save(any(Usuario.class))).thenReturn(nuevoUsuario);

        Usuario resultado = authController.registrarUsuario(nuevoUsuario);

        //verificar que el usuario no es nulo
        assertNotNull(resultado);
        assertEquals("nuevoUsuario", resultado.getNombre());

        // verificar que se llamó al metodo save 1 vez
        verify(usuarioService, times(1)).save(any(Usuario.class));
    }
}