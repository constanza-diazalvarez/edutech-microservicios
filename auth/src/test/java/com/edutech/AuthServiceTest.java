package com.edutech.service;

import com.edutech.dto.LoginRequest;
import com.edutech.dto.LoginResponse;
import com.edutech.dto.UsuarioDTO;
import com.edutech.model.Rol;
import com.edutech.model.Usuario;
import com.edutech.repository.RolRepository;
import com.edutech.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import utils.JwtUtil;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private AuthService authService;

    private Usuario usuario;
    private Rol rolAdmin;

    @BeforeEach
    void setUp() {
        Mockito.reset(usuarioRepository);
        rolAdmin = new Rol();
        rolAdmin.setId(1);
        rolAdmin.setRol("ADMIN");

        usuario = new Usuario();
        usuario.setId(1);
        usuario.setCorreo("admin@example.com");
        usuario.setPassword("123456");
        usuario.setRol(rolAdmin);
    }

    @Test
    public void loginExitoso() {
        // configurar el request de login
        LoginRequest request = new LoginRequest();
        request.setCorreo("admin@example.com");
        request.setPassword("123456");

        // configurar el mock del usuario
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(1);
        usuarioMock.setCorreo("admin@example.com");
        usuarioMock.setPassword("123456"); // Misma contraseña que el request
        usuarioMock.setRol(new Rol(1, "ADMIN"));

        // configurar el mock del repositorio
        when(usuarioRepository.findByCorreo("admin@example.com"))
                .thenReturn(Optional.of(usuarioMock));

        // ejecutar el metodo
        LoginResponse response = authService.login(request);

        // verificaciones
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("ADMIN", response.getRol());
        assertEquals(1, response.getIdUsuario());

        // verificar que el token es válido
        assertTrue(JwtUtil.validarToken(response.getToken(), "admin@example.com"));
    }

    @Test
    public void loginContrasenaIncorrecta() {
        // configurar el request con credenciales
        LoginRequest request = new LoginRequest();
        request.setCorreo("admin@example.com");
        request.setPassword("123456");  // Contraseña que el usuario está intentando usar

        // crear un usuario mock con contraseña completamente distinta
        Usuario usuarioMock = new Usuario();
        usuarioMock.setCorreo("admin@example.com");
        usuarioMock.setPassword("passwordCorrecta");  // Contraseña real almacenada

        // configurar el mock del repositorio
        when(usuarioRepository.findByCorreo("admin@example.com"))
                .thenReturn(Optional.of(usuarioMock));

        // ejecutar y verificar que se lanza la excepción
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(request)
        );

        // verificar detalles de la excepción
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Credenciales incorrectas", exception.getReason());

        // verificar interacción con el repositorio
        verify(usuarioRepository, times(1)).findByCorreo("admin@example.com");
    }

    @Test
    public void registrarUsuarioExitoso() {
        //mock de un usuario
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setCorreo("new@example.com");
        usuarioDTO.setPassword("newpassword");
        //mock de un rol
        Rol rolEstudiante = new Rol();
        rolEstudiante.setId(4);
        rolEstudiante.setRol("ESTUDIANTE");

        when(rolRepository.findById(4)).thenReturn(Optional.of(rolEstudiante));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(2); // Simular ID generado
            return u;
        });

        // Ejecutar el metodo
        ResponseEntity<?> response = authService.registrar(usuarioDTO);

        // Verificaciones
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UsuarioDTO);

        UsuarioDTO responseDTO = (UsuarioDTO) response.getBody();
        assertEquals("new@example.com", responseDTO.getCorreo());
        assertEquals("ACTIVO", responseDTO.getEstado());

        // Verificar que se llamó al servicio externo
        verify(restTemplate, times(1)).postForEntity(
                eq("http://localhost:8080/api/usuarios/registrar"),
                any(UsuarioDTO.class),
                eq(Void.class));
    }

    @Test
    public void registrarUsuarioRolNoEncontrado() {
        // Configurar los mocks
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setCorreo("new@example.com");
        usuarioDTO.setPassword("newpassword");

        when(rolRepository.findById(4)).thenReturn(Optional.empty());

        // Ejecutar y verificar la excepción
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.registrar(usuarioDTO));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Rol no encontrado", exception.getReason());
    }


}