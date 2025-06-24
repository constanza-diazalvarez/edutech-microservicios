package com.edutech.service;

import com.edutech.model.Curso;
import com.edutech.repository.CursoRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


//Estructura de un test
//Arrange (Preparar): Configuración inicial y datos de prueba
//Act (Actuar): Ejecución del metodo a probar
//Assert (Verificar): Comprobación de resultados


@SpringBootTest
public class CursoServiceTest {

    @MockBean
    private CursoRepository cursoRepository;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private CursoService cursoService;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker(new Locale("es"));
    }

    @Test
    void listarCursos_ShouldReturnAllCourses() {
        // Arrange
        List<Curso> cursos = Arrays.asList(
                crearCursoFake(),
                crearCursoFake(),
                crearCursoFake()
        );

        when(cursoRepository.findAll()).thenReturn(cursos);

        // Act
        List<Curso> resultado = cursoService.listarCursos();

        // Assert
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        verify(cursoRepository, times(1)).findAll();
    }

    @Test
    void crearCurso_ShouldSaveNewCourse() {
        // Arrange
        Curso cursoNuevo = crearCursoFake();
        cursoNuevo.setIdCurso(null);

        when(cursoRepository.save(any(Curso.class))).thenReturn(cursoNuevo);

        // Act
        Curso resultado = cursoService.crearCurso(cursoNuevo);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getNombreCurso());
        assertNotNull(resultado.getCategoria());
        assertNotNull(resultado.getNivel());
        assertTrue(resultado.getDuracion() > 0);
        verify(cursoRepository, times(1)).save(any(Curso.class));
    }

    @Test
    void eliminarCursoPorId_ShouldDeleteExistingCourse() {
        // Arrange
        Integer idCurso = faker.number().numberBetween(1, 100);
        when(cursoRepository.existsById(idCurso)).thenReturn(true);

        // Act
        cursoService.eliminarCursoPorId(idCurso);

        // Assert
        verify(cursoRepository, times(1)).existsById(idCurso);
        verify(cursoRepository, times(1)).deleteById(idCurso);
    }

    @Test
    void eliminarCursoPorId_ShouldThrowExceptionWhenCourseNotExists() {
        // Arrange
        Integer idCurso = faker.number().numberBetween(1, 100);
        when(cursoRepository.existsById(idCurso)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> cursoService.eliminarCursoPorId(idCurso));
        verify(cursoRepository, times(1)).existsById(idCurso);
        verify(cursoRepository, never()).deleteById(anyInt());
    }

    @Test
    void actualizarCurso_ShouldUpdateExistingCourse() {
        // Arrange
        Integer idCurso = faker.number().numberBetween(1, 100);
        Curso cursoExistente = crearCursoFake();
        cursoExistente.setIdCurso(idCurso);

        Curso cursoActualizado = new Curso();
        cursoActualizado.setNombreCurso("Nuevo nombre del curso");
        cursoActualizado.setDescripcion("Nueva descripción");
        cursoActualizado.setCategoria("Nueva categoría");
        cursoActualizado.setNivel("Avanzado");
        cursoActualizado.setDuracion(60);

        when(cursoRepository.findById(idCurso)).thenReturn(Optional.of(cursoExistente));
        when(cursoRepository.save(any(Curso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Curso resultado = cursoService.actualizarCurso(idCurso, cursoActualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals(idCurso, resultado.getIdCurso());
        assertEquals("Nuevo nombre del curso", resultado.getNombreCurso());
        assertEquals("Nueva descripción", resultado.getDescripcion());
        assertEquals("Nueva categoría", resultado.getCategoria());
        assertEquals("Avanzado", resultado.getNivel());
        assertEquals(60, resultado.getDuracion());

        verify(cursoRepository, times(1)).findById(idCurso);
        verify(cursoRepository, times(1)).save(any(Curso.class));
    }

    @Test
    void actualizarCurso_ShouldThrowExceptionWhenCourseNotExists() {
        // Arrange
        Integer idCurso = faker.number().numberBetween(1, 100);
        when(cursoRepository.findById(idCurso)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                cursoService.actualizarCurso(idCurso, new Curso()));
        verify(cursoRepository, times(1)).findById(idCurso);
        verify(cursoRepository, never()).save(any(Curso.class));
    }

    @Test
    void vincularCursoConInstructor_ShouldLinkCourseWithInstructor() {
        // Arrange
        Integer instructorId = faker.number().numberBetween(1, 100);
        Integer cursoId = faker.number().numberBetween(1, 100);




        Curso curso = crearCursoFake();
        curso.setIdCurso(cursoId);
        curso.setIdInstructor(instructorId);

        when(restTemplate.getForObject(anyString(), eq(Integer.class), anyInt()))
                .thenReturn(instructorId);

        when(cursoRepository.findCursoByIdCurso(cursoId)).thenReturn(curso);
        when(cursoRepository.save(any(Curso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Curso resultado = cursoService.vincularCursoConInstructor(instructorId, cursoId);

        // Assert
        assertNotNull(resultado);
        assertEquals(instructorId, resultado.getIdInstructor());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(Integer.class), eq(instructorId));
        verify(cursoRepository, times(1)).findCursoByIdCurso(cursoId);
        verify(cursoRepository, times(1)).save(any(Curso.class));
    }

    @Test
    void vincularCursoConInstructor_ShouldThrowExceptionWhenInstructorNotFound() {
        // Arrange
        Integer instructorId = faker.number().numberBetween(1, 100);
        Integer cursoId = faker.number().numberBetween(1, 100);

        when(restTemplate.getForObject(anyString(), eq(Integer.class), anyInt()))
                .thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                cursoService.vincularCursoConInstructor(instructorId, cursoId));
        verify(restTemplate, times(1)).getForObject(anyString(), eq(Integer.class), eq(instructorId));
        verify(cursoRepository, never()).findCursoByIdCurso(anyInt());
        verify(cursoRepository, never()).save(any(Curso.class));
    }

    @Test
    void obtenerCursosPorUsuario_ShouldReturnUserCourses() {
        // Arrange
        Integer instrunctorId = faker.number().numberBetween(1, 100);
        List<Curso> cursos = Arrays.asList(
                crearCursoFake(instrunctorId),
                crearCursoFake(instrunctorId)
        );

        when(cursoRepository.findByIdInstructor(instrunctorId)).thenReturn(cursos);

        // Act
        List<Curso> resultado = cursoService.obtenerCursosPorInstructor(instrunctorId);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        resultado.forEach(c -> assertEquals(instrunctorId, c.getIdInstructor()));
        verify(cursoRepository, times(1)).findByIdInstructor(instrunctorId);
    }

    @Test
    void obtenerCursosPorCategoria_ShouldReturnCategoryCourses() {
        // Arrange
        String categoria = "Programación";
        List<Curso> cursos = Arrays.asList(
                crearCursoFakeConCategoria(categoria),
                crearCursoFakeConCategoria(categoria)
        );

        when(cursoRepository.findByCategoria(categoria)).thenReturn(cursos);

        // Act
        List<Curso> resultado = cursoService.obtenerCursosPorCategoria(categoria);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        resultado.forEach(c -> assertEquals(categoria, c.getCategoria()));
        verify(cursoRepository, times(1)).findByCategoria(categoria);
    }

    // Métodos auxiliares
    private Curso crearCursoFake() {
        return crearCursoFake(faker.bool().bool() ? faker.number().numberBetween(1, 100) : null);
    }

    private Curso crearCursoFake(Integer idInstructor) {
        Curso curso = new Curso();
        curso.setIdCurso(faker.number().numberBetween(1, 100));
        curso.setNombreCurso(faker.educator().course());
        curso.setIdInstructor(idInstructor);
        curso.setDescripcion(faker.lorem().sentence());
        curso.setCategoria(faker.options().option("Programación", "Diseño", "Marketing", "Negocios"));
        curso.setNivel(faker.options().option("Principiante", "Intermedio", "Avanzado"));
        curso.setDuracion(faker.number().numberBetween(8, 120));
        return curso;
    }

    private Curso crearCursoFakeConCategoria(String categoria) {
        Curso curso = crearCursoFake();
        curso.setCategoria(categoria);
        return curso;
    }
}