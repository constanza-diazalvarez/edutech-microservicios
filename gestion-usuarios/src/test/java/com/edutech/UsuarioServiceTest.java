package com.edutech.service;

import com.edutech.dto.UsuarioDTO;
import com.edutech.model.Usuario;
import com.edutech.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import utils.JwtUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UsuarioServiceTest {

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private HttpServletRequest request;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioDTO usuarioDTO;
    private String validToken;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("Usuario Test");
        usuario.setEstado("ACTIVO");

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(1);
        usuarioDTO.setNombre("Usuario Test");
        usuarioDTO.setEstado("ACTIVO");

        validToken = JwtUtil.generarToken("Usuario Test", "ADMIN", 1);
    }

    @Test
    void guardarUsuario() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.save(usuario);

        assertNotNull(resultado);
        assertEquals(usuario.getId(), resultado.getId());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void buscarUsuarioPorIdExistente() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.findUsuarioById(1);

        assertNotNull(resultado);
        assertEquals(usuario.getId(), resultado.getId());
        verify(usuarioRepository, times(1)).findById(1);
    }

    @Test
    void buscarUsuarioPorIdInexistente() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException excepcion = assertThrows(ResponseStatusException.class,
                () -> usuarioService.findUsuarioById(1));

        assertEquals(HttpStatus.NOT_FOUND, excepcion.getStatusCode());
        assertEquals("Usuario no encontrado", excepcion.getReason());
    }

    @Test
    void eliminarUsuario() {
        usuarioService.deleteById(1);
        verify(usuarioRepository, times(1)).deleteById(1);
    }

    @Test
    void buscarUsuarioPorNombre() {
        when(usuarioRepository.findByNombre("Usuario Test")).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.findByNombre("Usuario Test");

        assertTrue(resultado.isPresent());
        assertEquals(usuario.getNombre(), resultado.get().getNombre());
    }

    @Test
    void registrarUsuario() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.registrarUsuario(usuarioDTO);

        assertNotNull(resultado);
        assertEquals(usuarioDTO.getId(), resultado.getId());
        assertEquals(usuarioDTO.getNombre(), resultado.getNombre());
    }

    @Test
    void crearUsuarioConPermisos() {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.crearUsuario(request, usuario);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void crearUsuarioSinPermisos() {
        //token con rol USER para que falle la validacin
        String tokenSinPermisos = JwtUtil.generarToken("Usuario Test", "USER", 1);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + tokenSinPermisos);

        ResponseStatusException excepcion = assertThrows(ResponseStatusException.class,
                () -> usuarioService.crearUsuario(request, usuario));

        assertEquals(HttpStatus.FORBIDDEN, excepcion.getStatusCode());
    }

    @Test
    void modificarUsuario() {
        Map<String, Object> updates = Map.of("nombre", "Nuevo Nombre");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        ResponseEntity<?> resultado = usuarioService.modificarUsuario(updates, request);

        assertEquals(ResponseEntity.ok().build(), resultado);
        assertEquals("Nuevo Nombre", usuario.getNombre());
    }
}