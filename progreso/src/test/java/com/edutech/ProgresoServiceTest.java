package com.edutech.service;

import com.edutech.model.Progreso;
import com.edutech.repository.ProgresoRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import utils.JwtUtil;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ProgresoServiceTest {

    @MockBean
    private ProgresoRepository progresoRepository;

    @MockBean
    private RestTemplate restTemplate;

    @Mock
    private HttpServletRequest request;

    @Autowired
    private ProgresoService progresoService;

    private MockedStatic<JwtUtil> jwtUtilMock;

    @BeforeEach
    void setUp() {
        when(request.getHeader("Authorization")).thenReturn("Bearer abc123");

        jwtUtilMock = Mockito.mockStatic(JwtUtil.class);
        jwtUtilMock.when(() -> JwtUtil.obtenerToken(request)).thenReturn("Bearer abc123");
        jwtUtilMock.when(() -> JwtUtil.obtenerId("Bearer abc123")).thenReturn(1);
    }


    @AfterEach
    void tearDown() {
        jwtUtilMock.close();
    }

    @Test
    void registrarProgreso_creaONoDuplicaProgresoYActualizaCorrectamente() {
        Integer idContenido = 12;
        Integer idCurso = 3;

        Map<String, Object> contenidoMap = new HashMap<>();
        contenidoMap.put("idContenido", idContenido);
        contenidoMap.put("titulo", "Variables en Java");
        contenidoMap.put("idCurso", idCurso);
        contenidoMap.put("tipo", "text/plain");
        //↑simola el json de respuesta

        ResponseEntity<Map> contenidoResponse = new ResponseEntity<>(contenidoMap, HttpStatus.OK);
        when(restTemplate.exchange(
                eq("http://localhost:8080/api/contenido/traer-contenido/" + idContenido), //espera exactamente esa url
                eq(HttpMethod.GET), //espera exactamente el metodo GET
                any(HttpEntity.class), //no importa cual HttpEntity devuelva, cualquiera sirve
                eq(Map.class) //espera respuesta tipo Map
        )).thenReturn(contenidoResponse);

        // Simular respuesta de todos los contenidos del curso
        Map<String, Object> contenido1 = new HashMap<>();
        contenido1.put("idContenido", 12);
        Map<String, Object> contenido2 = new HashMap<>();
        contenido2.put("idContenido", 13);

        List<Map<String, Object>> listaContenidos = Arrays.asList(contenido1, contenido2);
        ResponseEntity<List> listaContenidosResponse = new ResponseEntity(listaContenidos, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://localhost:8080/api/contenido/curso/" + idCurso),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(List.class)
        )).thenReturn(listaContenidosResponse);

        // Simular que no existe progreso previo
        when(progresoRepository.findByIdEstudianteAndIdCurso(1, idCurso)).thenReturn(Optional.empty());

        // Ejecutar
        progresoService.registrarProgreso(idContenido, request);

        // Capturar argumento
        ArgumentCaptor<Progreso> captor = ArgumentCaptor.forClass(Progreso.class);
        verify(progresoRepository, times(1)).save(captor.capture());
        Progreso progresoGuardado = captor.getValue();

        // Validar usando assertAll
        assertAll("Validar progreso guardado",
                () -> assertEquals(1, progresoGuardado.getIdEstudiante()),
                () -> assertEquals(3, progresoGuardado.getIdCurso()),
                () -> assertEquals(2, progresoGuardado.getIdContenidoCompleto().size()),
                () -> assertTrue(progresoGuardado.getIdContenidoVisualizado().contains(12)),
                () -> assertEquals(50.0, progresoGuardado.getPorcentaje())
        );
    }

    @Test
    void registrarProgreso_actualizaProgresoExistenteSinDuplicarContenidoVisualizado() {
        Integer idContenido = 13;
        Integer idCurso = 3;
        Integer idEstudiante = 1;

        // Simular contenido individual (con idCurso)
        Map<String, Object> contenidoMap = new HashMap<>();
        contenidoMap.put("idContenido", idContenido);
        contenidoMap.put("idCurso", idCurso);
        ResponseEntity<Map> contenidoResponse = new ResponseEntity<>(contenidoMap, HttpStatus.OK);
        when(restTemplate.exchange(eq("http://localhost:8080/api/contenido/traer-contenido/" + idContenido),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(contenidoResponse);

        // Simular lista de contenidos del curso (id 12 y 13)
        Map<String, Object> c1 = Map.of("idContenido", 12);
        Map<String, Object> c2 = Map.of("idContenido", 13);
        List<Map<String, Object>> listaContenidos = List.of(c1, c2);
        ResponseEntity<List> listaContenidosResponse = new ResponseEntity<>(listaContenidos, HttpStatus.OK);
        when(restTemplate.exchange(eq("http://localhost:8080/api/contenido/curso/" + idCurso),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(List.class)))
                .thenReturn(listaContenidosResponse);

        // Simular progreso ya existente (visualizó solo el contenido 12)
        Progreso progresoExistente = new Progreso();
        progresoExistente.setIdEstudiante(idEstudiante);
        progresoExistente.setIdCurso(idCurso);
        progresoExistente.setIdContenidoVisualizado(new ArrayList<>(List.of(12)));
        when(progresoRepository.findByIdEstudianteAndIdCurso(idEstudiante, idCurso))
                .thenReturn(Optional.of(progresoExistente));

        // Ejecutar
        progresoService.registrarProgreso(idContenido, request);

        // Verificar
        ArgumentCaptor<Progreso> captor = ArgumentCaptor.forClass(Progreso.class);
        verify(progresoRepository, times(1)).save(captor.capture());

        Progreso guardado = captor.getValue();

        assertAll("Validar actualización de progreso existente",
                () -> assertEquals(idEstudiante, guardado.getIdEstudiante()),
                () -> assertEquals(idCurso, guardado.getIdCurso()),
                () -> assertEquals(List.of(12, 13), guardado.getIdContenidoVisualizado()),
                () -> assertEquals(List.of(12, 13), guardado.getIdContenidoCompleto()),
                () -> assertEquals(100.0, guardado.getPorcentaje(), 0.01)
        );
    }

    @Test
    void registrarProgreso_noAgregaContenidoYaVisualizado_niModificaPorcentaje() {
        // Datos simulados
        Integer idContenido = 12;
        Integer idCurso = 3;
        Integer idEstudiante = 1;

        // Simular contenido con su idCurso
        Map<String, Object> contenidoMap = Map.of(
                "idContenido", idContenido,
                "idCurso", idCurso
        );
        ResponseEntity<Map> contenidoResponse = new ResponseEntity<>(contenidoMap, HttpStatus.OK);
        when(restTemplate.exchange(eq("http://localhost:8080/api/contenido/traer-contenido/" + idContenido),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(contenidoResponse);

        // Simular lista de todos los contenidos del curso
        List<Map<String, Object>> listaContenidos = List.of(
                Map.of("idContenido", 12),
                Map.of("idContenido", 13)
        );
        ResponseEntity<List> listaContenidosResponse = new ResponseEntity<>(listaContenidos, HttpStatus.OK);
        when(restTemplate.exchange(eq("http://localhost:8080/api/contenido/curso/" + idCurso),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(List.class)))
                .thenReturn(listaContenidosResponse);

        // Simular progreso ya existente con el contenido ya visto
        Progreso progresoExistente = new Progreso();
        progresoExistente.setIdEstudiante(idEstudiante);
        progresoExistente.setIdCurso(idCurso);
        progresoExistente.setIdContenidoVisualizado(new ArrayList<>(List.of(12, 13))); // ya los vio todos
        progresoExistente.setPorcentaje(100.0); // ya completó

        when(progresoRepository.findByIdEstudianteAndIdCurso(idEstudiante, idCurso))
                .thenReturn(Optional.of(progresoExistente));

        // Ejecutar
        progresoService.registrarProgreso(idContenido, request);

        // Capturar el progreso guardado
        ArgumentCaptor<Progreso> captor = ArgumentCaptor.forClass(Progreso.class);
        verify(progresoRepository).save(captor.capture());
        Progreso guardado = captor.getValue();

        // Validar con assertAll
        assertAll("Validar que no se duplicó contenido ni cambió porcentaje",
                () -> assertEquals(List.of(12, 13), guardado.getIdContenidoVisualizado(), "No debe duplicar contenido"),
                () -> assertEquals(List.of(12, 13), guardado.getIdContenidoCompleto(), "Contenidos completos deben mantenerse"),
                () -> assertEquals(100.0, guardado.getPorcentaje(), "Porcentaje no debe cambiar"),
                () -> assertEquals(2, guardado.getIdContenidoVisualizado().size(), "Debe haber solo 2 contenidos visualizados")
        );
    }

    @Test
    void registrarProgreso_lanzaExcepcionCuandoFallaObtencionDeContenido() {
        // Datos simulados
        Integer idContenido = 12;

        // Simular que RestTemplate lanza una excepción cuando intenta obtener el contenido
        when(restTemplate.exchange(
                eq("http://localhost:8080/api/contenido/traer-contenido/" + idContenido),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new RuntimeException("Falla en servicio de contenido"));

        // Ejecutar y verificar que lanza una excepción
        Exception ex = assertThrows(RuntimeException.class, () ->
                progresoService.registrarProgreso(idContenido, request)
        );

        // Validar el mensaje de error
        assertEquals("Falla en servicio de contenido", ex.getMessage());
    }
}