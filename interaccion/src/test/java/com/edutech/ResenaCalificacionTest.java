package com.edutech.service;

import com.edutech.model.ResenaCalificacion;
import com.edutech.repository.ResenaCalificacionRepository;
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
public class ResenaCalificacionTest {

    @MockBean
    private ResenaCalificacionRepository resenaCalificacionRepository;

    @Autowired
    private ResenaCalificacionService resenaCalificacionService;

    private ResenaCalificacion resenaCalificacion;
    private List<ResenaCalificacion> listaResenasCalificaciones;

    @BeforeEach
    void setUp() {
        resenaCalificacion = new ResenaCalificacion();
        resenaCalificacion.setIdCurso(101);
        resenaCalificacion.setNombreCurso("Curso de Java");
        resenaCalificacion.setResena("Excelente curso, muy recomendado");
        resenaCalificacion.setCalificacion(5);

        ResenaCalificacion resenaCalificacion2 = new ResenaCalificacion();
        resenaCalificacion2.setIdCurso(102);
        resenaCalificacion2.setNombreCurso("Curso de Python");
        resenaCalificacion2.setResena("Buen curso para principiantes");
        resenaCalificacion2.setCalificacion(4);

        listaResenasCalificaciones = Arrays.asList(resenaCalificacion, resenaCalificacion2);
    }

    @Test
    void listarResCal() {
        when(resenaCalificacionRepository.findAll()).thenReturn(listaResenasCalificaciones);

        List<ResenaCalificacion> resultado = resenaCalificacionService.listarResCal();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(resenaCalificacion.getIdCurso(), resultado.get(0).getIdCurso());
        assertEquals(resenaCalificacion.getNombreCurso(), resultado.get(0).getNombreCurso());
        assertEquals(resenaCalificacion.getResena(), resultado.get(0).getResena());
        assertEquals(resenaCalificacion.getCalificacion(), resultado.get(0).getCalificacion());
        verify(resenaCalificacionRepository, times(1)).findAll();
    }

    @Test
    void listarResCalVacia() {
        when(resenaCalificacionRepository.findAll()).thenReturn(Arrays.asList());

        List<ResenaCalificacion> resultado = resenaCalificacionService.listarResCal();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(resenaCalificacionRepository, times(1)).findAll();
    }

    @Test
    void crearResCal() {
        when(resenaCalificacionRepository.save(any(ResenaCalificacion.class))).thenReturn(resenaCalificacion);

        ResenaCalificacion resultado = resenaCalificacionService.crearResCal(resenaCalificacion);

        assertNotNull(resultado);
        assertEquals(resenaCalificacion.getIdCurso(), resultado.getIdCurso());
        assertEquals(resenaCalificacion.getNombreCurso(), resultado.getNombreCurso());
        assertEquals(resenaCalificacion.getResena(), resultado.getResena());
        assertEquals(resenaCalificacion.getCalificacion(), resultado.getCalificacion());
        verify(resenaCalificacionRepository, times(1)).save(resenaCalificacion);
    }

    @Test
    void crearResCalConResenaNula() {
        ResenaCalificacion resenaCalificacionSinResena = new ResenaCalificacion();
        resenaCalificacionSinResena.setIdCurso(103);
        resenaCalificacionSinResena.setNombreCurso("Curso de JavaScript");
        resenaCalificacionSinResena.setResena(null);
        resenaCalificacionSinResena.setCalificacion(3);

        when(resenaCalificacionRepository.save(any(ResenaCalificacion.class))).thenReturn(resenaCalificacionSinResena);

        ResenaCalificacion resultado = resenaCalificacionService.crearResCal(resenaCalificacionSinResena);

        assertNotNull(resultado);
        assertEquals(resenaCalificacionSinResena.getIdCurso(), resultado.getIdCurso());
        assertEquals(resenaCalificacionSinResena.getNombreCurso(), resultado.getNombreCurso());
        assertNull(resultado.getResena());
        assertEquals(resenaCalificacionSinResena.getCalificacion(), resultado.getCalificacion());
        verify(resenaCalificacionRepository, times(1)).save(resenaCalificacionSinResena);
    }

    @Test
    void crearResCalConCalificacionCero() {
        ResenaCalificacion resenaCalificacionCalificacionCero = new ResenaCalificacion();
        resenaCalificacionCalificacionCero.setIdCurso(104);
        resenaCalificacionCalificacionCero.setNombreCurso("Curso de C++");
        resenaCalificacionCalificacionCero.setResena("Curso muy difícil");
        resenaCalificacionCalificacionCero.setCalificacion(0);

        when(resenaCalificacionRepository.save(any(ResenaCalificacion.class))).thenReturn(resenaCalificacionCalificacionCero);

        ResenaCalificacion resultado = resenaCalificacionService.crearResCal(resenaCalificacionCalificacionCero);

        assertNotNull(resultado);
        assertEquals(resenaCalificacionCalificacionCero.getIdCurso(), resultado.getIdCurso());
        assertEquals(resenaCalificacionCalificacionCero.getNombreCurso(), resultado.getNombreCurso());
        assertEquals(resenaCalificacionCalificacionCero.getResena(), resultado.getResena());
        assertEquals(0, resultado.getCalificacion());
        verify(resenaCalificacionRepository, times(1)).save(resenaCalificacionCalificacionCero);
    }

    @Test
    void crearResCalNulo() {
        ResenaCalificacion resenaCalificacionNulo = null;

        when(resenaCalificacionRepository.save(any(ResenaCalificacion.class))).thenReturn(resenaCalificacionNulo);

        ResenaCalificacion resultado = resenaCalificacionService.crearResCal(resenaCalificacionNulo);

        assertNull(resultado);
        verify(resenaCalificacionRepository, times(1)).save(resenaCalificacionNulo);
    }
}