package com.edutech;

import com.edutech.dto.ContenidoDTO;
import com.edutech.dto.CursoConContenidoDTO;
import com.edutech.dto.CursoDTO;
import com.edutech.modelo.Inscripcion;
import com.edutech.repository.InscripcionRepository;
import com.edutech.service.InscripcionService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@SpringBootTest
class InscripcionServiceTest {

    @Autowired
    private InscripcionService inscripcionService;

    @MockBean
    private InscripcionRepository inscripcionRepository;

    @MockBean
    private RestTemplate restTemplate;

    @Mock
    private HttpServletRequest request;

    @Test
    void obtenerInscripcionesPorUsuario_devuelveListaCorrecta() {
        Integer idUsuario = 1;
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setId(1);
        inscripcion.setIdUsuario(idUsuario);
        inscripcion.setIdCurso(2);
        inscripcion.setIdPago(3);
        inscripcion.setFechaInscripcion(LocalDateTime.now());

        List<Inscripcion> inscripcionesFalsas = List.of(inscripcion);

        when(inscripcionRepository.findByIdUsuario(idUsuario)).thenReturn(inscripcionesFalsas);

        List<Inscripcion> resultado = inscripcionService.obtenerInscripcionesPorUsuario(idUsuario);

        assertEquals(1, resultado.size());
        assertEquals(idUsuario, resultado.get(0).getIdUsuario());
    }

    @Test
    void obtenerInscripcionesPorUsuario_sinResultadosDevuelveListaVacia() {
        Integer idUsuario = 999;

        when(inscripcionRepository.findByIdUsuario(idUsuario)).thenReturn(Collections.emptyList());

        List<Inscripcion> resultado = inscripcionService.obtenerInscripcionesPorUsuario(idUsuario);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void inscribirseACurso_CuandoNoExisteInscripcion_DeberiaGuardarYRetornar() {
        Integer idUsuario = 1;
        Integer idCurso = 2;
        Integer idPago = 3;

        when(inscripcionRepository.findByIdUsuarioAndIdCurso(idUsuario, idCurso))
                .thenReturn(Optional.empty());

        Inscripcion inscripcionGuardada = new Inscripcion();
        inscripcionGuardada.setId(100);
        inscripcionGuardada.setIdUsuario(idUsuario);
        inscripcionGuardada.setIdCurso(idCurso);
        inscripcionGuardada.setIdPago(idPago);
        inscripcionGuardada.setFechaInscripcion(LocalDateTime.now());

        when(inscripcionRepository.save(any(Inscripcion.class)))
                .thenReturn(inscripcionGuardada);

        Inscripcion resultado = inscripcionService.inscribirseACurso(idUsuario, idCurso, idPago);

        assertNotNull(resultado);
        assertEquals(idUsuario, resultado.getIdUsuario());
        assertEquals(idCurso, resultado.getIdCurso());
        assertEquals(idPago, resultado.getIdPago());
    }

    @Test
    void inscribirseACurso_CuandoYaExisteInscripcion_DeberiaLanzarExcepcion() {
        Integer idUsuario = 1;
        Integer idCurso = 2;
        Integer idPago = 3;

        Inscripcion existente = new Inscripcion();
        existente.setIdUsuario(idUsuario);
        existente.setIdCurso(idCurso);

        when(inscripcionRepository.findByIdUsuarioAndIdCurso(idUsuario, idCurso))
                .thenReturn(Optional.of(existente));

        assertThrows(RuntimeException.class, () ->
                inscripcionService.inscribirseACurso(idUsuario, idCurso, idPago));
    }

    @Test
    void obtenerCursosPorUsuario_CuandoHayInscripciones_DeberiaRetornarCursosConContenido() {
        Integer idUsuario = 1;
        String authHeader = "Bearer abc123";

        when(request.getHeader("Authorization")).thenReturn(authHeader);

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setIdCurso(100);
        when(inscripcionRepository.findByIdUsuario(idUsuario))
                .thenReturn(List.of(inscripcion));

        CursoDTO cursoDTO = new CursoDTO();
        cursoDTO.setIdCurso(100);
        cursoDTO.setNombreCurso("Curso de prueba");
        when(restTemplate.exchange(
                eq("http://localhost:8080/api/cursos/100"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(CursoDTO.class)
        )).thenReturn(ResponseEntity.ok(cursoDTO));

        ContenidoDTO contenido1 = new ContenidoDTO();
        contenido1.setIdContenido(1);
        ContenidoDTO contenido2 = new ContenidoDTO();
        contenido2.setIdContenido(2);
        ContenidoDTO[] contenidos = new ContenidoDTO[]{contenido1, contenido2};

        when(restTemplate.exchange(
                eq("http://localhost:8080/api/contenido/curso/100"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(ContenidoDTO[].class)
        )).thenReturn(ResponseEntity.ok(contenidos));

        List<CursoConContenidoDTO> resultado = inscripcionService.obtenerCursosPorUsuario(idUsuario, request);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(100, resultado.get(0).getCurso().getIdCurso());
        assertEquals(2, resultado.get(0).getContenido().size());
    }

    @Test
    void testObtenerCursosPorUsuario_SinInscripciones_DeberiaRetornarListaVacia() {
        Integer idUsuario = 2;

        when(inscripcionRepository.findByIdUsuario(idUsuario)).thenReturn(Collections.emptyList());

        List<CursoConContenidoDTO> resultado = inscripcionService.obtenerCursosPorUsuario(idUsuario, request);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testExisteUsuarioYCurso_CuandoExiste_DeberiaRetornarTrue() {
        Integer idUsuario = 1;
        Integer idCurso = 10;

        when(inscripcionRepository.existsByIdUsuarioAndIdCurso(idUsuario, idCurso))
                .thenReturn(true);

        Boolean resultado = inscripcionService.existeUsuarioYCurso(idUsuario, idCurso);

        assertTrue(resultado);
    }

    @Test
    void testExisteUsuarioYCurso_CuandoNoExiste_DeberiaRetornarFalse() {
        Integer idUsuario = 2;
        Integer idCurso = 20;

        when(inscripcionRepository.existsByIdUsuarioAndIdCurso(idUsuario, idCurso))
                .thenReturn(false);

        Boolean resultado = inscripcionService.existeUsuarioYCurso(idUsuario, idCurso);

        assertFalse(resultado);
    }
}


