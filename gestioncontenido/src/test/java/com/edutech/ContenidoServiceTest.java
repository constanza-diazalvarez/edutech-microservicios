package com.edutech.service;

import com.edutech.model.Contenido;
import com.edutech.repository.ContenidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ContenidoServiceTest {

    // mock del repository
    @MockBean
    private ContenidoRepository contenidoRepository;

    // servicio a testear
    @Autowired
    private ContenidoService contenidoService;

    // datos
    private MultipartFile archivoValido;
    private Contenido contenidoExistente;
    private final Integer ID_CURSO_VALIDO = 1;
    private final Integer ID_CONTENIDO_VALIDO = 1;

    @BeforeEach//configura el entorno antes de cada prueba
    void setUp() throws IOException {
        //crear un archivo mock para las pruebas
        archivoValido = new MockMultipartFile(
                "archivo",
                "documento.pdf",
                "application/pdf",
                "contenido de prueba".getBytes()
        );

        //crear un contenido existente para pruebas de actualización
        contenidoExistente = new Contenido();
        contenidoExistente.setIdContenido(ID_CONTENIDO_VALIDO);
        contenidoExistente.setNombre("antiguo.pdf");
        contenidoExistente.setTipoContenido("application/pdf");
        contenidoExistente.setDatosContenido("contenido antiguo".getBytes());
        contenidoExistente.setIdCurso(ID_CURSO_VALIDO);
    }

    @Test
    void guardarContenido_ConDatosValidos_DeberiaGuardarCorrectamente() throws IOException {
        //mock del repositorio
        when(contenidoRepository.save(any(Contenido.class)))
                .thenAnswer(invocation -> {
                    // qué hace invocation.getArgument(0) = el Contenido que pasa al servicio
                    Contenido c = invocation.getArgument(0); // Obtiene el objeto real
                    c.setIdContenido(1); // lo modifica como una BD real
                    return c; // lo devuelve modificado
                });

        // ejecuta el metodo
        Contenido resultado = contenidoService.guardarContenido(ID_CURSO_VALIDO, archivoValido);

        // Verificaciones
        //no nulo
        assertNotNull(resultado);
        //id no coincide
        assertEquals(ID_CONTENIDO_VALIDO, resultado.getIdContenido());
        //nombre de archivo no coincide
        assertEquals(archivoValido.getOriginalFilename(), resultado.getNombre());
        //tipo de contenido no coincide
        assertEquals(archivoValido.getContentType(), resultado.getTipoContenido());
        //datos no coinciden
        assertArrayEquals(archivoValido.getBytes(), resultado.getDatosContenido());
        //id curso no couincide
        assertEquals(ID_CURSO_VALIDO, resultado.getIdCurso());

        // Verificar interacción con el repositorio
        verify(contenidoRepository, times(1)).save(any(Contenido.class));
    }

    @Test
    void actualizarContenido_ConNuevoArchivo_DeberiaActualizarTodosLosCampos() throws IOException {
        // mocks
        when(contenidoRepository.findById(ID_CONTENIDO_VALIDO)).thenReturn(Optional.of(contenidoExistente)); // Simula encontrar el contenido
        when(contenidoRepository.save(any(Contenido.class))).thenAnswer(i -> i.getArgument(0)); // Devuelve el mismo objeto que recibe

        //nuevo id para actualización
        Integer nuevoCursoId = 2;

        //metodo
        Contenido resultado = contenidoService.actualizarContenido(
                ID_CONTENIDO_VALIDO,
                nuevoCursoId,
                archivoValido
        );

        // verificaciones
        assertNotNull(resultado);
        assertEquals(ID_CONTENIDO_VALIDO, resultado.getIdContenido());
        assertEquals(archivoValido.getOriginalFilename(), resultado.getNombre());
        assertEquals(archivoValido.getContentType(), resultado.getTipoContenido());
        assertArrayEquals(archivoValido.getBytes(), resultado.getDatosContenido());
        assertEquals(nuevoCursoId, resultado.getIdCurso());

        //interacciones
        verify(contenidoRepository, times(1)).findById(ID_CONTENIDO_VALIDO);
        verify(contenidoRepository, times(1)).save(any(Contenido.class));
    }

    @Test
    void actualizarContenido_SinArchivo_DeberiaActualizarSoloCurso() throws IOException {
        //mocks
        when(contenidoRepository.findById(ID_CONTENIDO_VALIDO)).thenReturn(Optional.of(contenidoExistente));
        when(contenidoRepository.save(any(Contenido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //probar actualización
        Integer nuevoCursoId = 2;

        // ejecutar con archivo null
        Contenido resultado = contenidoService.actualizarContenido(
                ID_CONTENIDO_VALIDO,
                nuevoCursoId,
                null
        );

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(ID_CONTENIDO_VALIDO, resultado.getIdContenido());
        assertEquals(contenidoExistente.getNombre(), resultado.getNombre());
        assertEquals(contenidoExistente.getTipoContenido(), resultado.getTipoContenido());
        assertArrayEquals(contenidoExistente.getDatosContenido(), resultado.getDatosContenido());
        assertEquals(nuevoCursoId, resultado.getIdCurso());

        // verificar interacciones
        verify(contenidoRepository, times(1)).findById(ID_CONTENIDO_VALIDO);
        verify(contenidoRepository, times(1)).save(any(Contenido.class));
    }

    @Test
    void actualizarContenido_ConIdInexistente_DeberiaLanzarExcepcion() {
        //mock para simular contenido no encontrado
        when(contenidoRepository.findById(ID_CONTENIDO_VALIDO)).thenReturn(Optional.empty());

        // verificar excepcion
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            contenidoService.actualizarContenido(ID_CONTENIDO_VALIDO, ID_CURSO_VALIDO, null);
        });

        // verificar mensaje de error
        assertEquals("Contenido no encontrado con id: " + ID_CONTENIDO_VALIDO, exception.getMessage());

        // verificar que no se llamó a save
        verify(contenidoRepository, never()).save(any(Contenido.class));
    }

    @Test
    void obtenerTodoContenido_DeberiaRetornarTodosLosContenidos() {
        //mock para devolver una lista de contenidos
        List<Contenido> contenidosEsperados = Arrays.asList(
                new Contenido(),
                new Contenido(),
                new Contenido()
        );
        when(contenidoRepository.findAll()).thenReturn(contenidosEsperados);

        // ejecutar el metodo
        List<Contenido> resultado = contenidoService.obtenerTodoContenido();

        // verificaciones
        assertNotNull(resultado, "La lista no debería ser nula");
        assertEquals(3, resultado.size(), "Debería retornar 3 contenidos");

        // verificar interacción
        verify(contenidoRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorIdCurso_DeberiaRetornarSoloContenidoDelCurso() {
        // mock para devolver contenidos específicos
        List<Contenido> contenidosEsperados = Arrays.asList(
                new Contenido(),
                new Contenido()
        );
        when(contenidoRepository.findByIdCurso(ID_CURSO_VALIDO)).thenReturn(contenidosEsperados);

        // ejecutar el metodo
        List<Contenido> resultado = contenidoService.obtenerPorIdCurso(ID_CURSO_VALIDO);

        // verificaciones
        assertNotNull(resultado);
        //retornar la cantidad correcta de la lista
        assertEquals(2, resultado.size());

        // Verificar interacción
        verify(contenidoRepository, times(1)).findByIdCurso(ID_CURSO_VALIDO);
    }
}