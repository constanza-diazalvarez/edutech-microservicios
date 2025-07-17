package com.edutech.service;

import com.edutech.model.Comentario;
import com.edutech.repository.ComentarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ComentarioTest {

    @MockBean
    private ComentarioRepository comentarioRepository;

    @Autowired
    private ComentarioService comentarioService;

    private Comentario comentario;
    private List<Comentario> listaComentarios;

    @BeforeEach
    void setUp() {
        comentario = new Comentario();
        comentario.setIdComentario(1);
        comentario.setIdCurso(101);
        comentario.setIdUsuario(1);
        comentario.setComentario("Este es un comentario de prueba");

        Comentario comentario2 = new Comentario();
        comentario2.setIdComentario(2);
        comentario2.setIdCurso(102);
        comentario2.setIdUsuario(2);
        comentario2.setComentario("Este es otro comentario de prueba");

        listaComentarios = Arrays.asList(comentario, comentario2);
    }

    @Test
    void listarComentarios() {
        when(comentarioRepository.findAll()).thenReturn(listaComentarios);

        List<Comentario> resultado = comentarioService.listarComentarios();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(comentario.getIdComentario(), resultado.get(0).getIdComentario());
        assertEquals(comentario.getComentario(), resultado.get(0).getComentario());
        verify(comentarioRepository, times(1)).findAll();
    }

    @Test
    void listarComentariosVacia() {
        when(comentarioRepository.findAll()).thenReturn(Arrays.asList());

        List<Comentario> resultado = comentarioService.listarComentarios();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(comentarioRepository, times(1)).findAll();
    }

    @Test
    void crearComentario() {
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(comentario);

        Comentario resultado = comentarioService.crearComentario(comentario);

        assertNotNull(resultado);
        assertEquals(comentario.getIdComentario(), resultado.getIdComentario());
        assertEquals(comentario.getIdCurso(), resultado.getIdCurso());
        assertEquals(comentario.getIdUsuario(), resultado.getIdUsuario());
        assertEquals(comentario.getComentario(), resultado.getComentario());
        verify(comentarioRepository, times(1)).save(comentario);
    }

    @Test
    void crearComentarioNulo() {
        Comentario comentarioNulo = null;

        when(comentarioRepository.save(any(Comentario.class))).thenReturn(comentarioNulo);

        Comentario resultado = comentarioService.crearComentario(comentarioNulo);

        assertNull(resultado);
        verify(comentarioRepository, times(1)).save(comentarioNulo);
    }
}