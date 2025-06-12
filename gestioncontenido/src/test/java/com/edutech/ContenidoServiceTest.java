package com.edutech.service;

import com.edutech.model.Contenido;
import com.edutech.repository.ContenidoRepository;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContenidoServiceTest {

    @Mock
    private ContenidoRepository contenidoRepository;

    @InjectMocks
    private ContenidoService contenidoService;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker(new Locale("es"));
    }

    @Test
    void guardarContenido_ShouldSaveContent() throws IOException {
        // Datos de prueba generados con Faker
        Integer cursoId = faker.number().numberBetween(1, 100);
        String fileName = faker.file().fileName();
        String contentType = "application/pdf";
        byte[] fileContent = faker.lorem().paragraph().getBytes();

        // Mock del archivo
        MultipartFile mockFile = new MockMultipartFile(
                "file",
                fileName,
                contentType,
                fileContent
        );

        // Mock del contenido guardado
        Contenido contenidoGuardado = crearContenidoFake();
        contenidoGuardado.setNombre(fileName);
        contenidoGuardado.setTipoContenido(contentType);
        contenidoGuardado.setDatosContenido(fileContent);
        contenidoGuardado.setIdCurso(cursoId);

        when(contenidoRepository.save(any(Contenido.class))).thenReturn(contenidoGuardado);

        // Ejecutar el método
        Contenido resultado = contenidoService.guardarContenido(cursoId, mockFile);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(fileName, resultado.getNombre());
        assertEquals(contentType, resultado.getTipoContenido());
        assertEquals(cursoId, resultado.getIdCurso());
        assertArrayEquals(fileContent, resultado.getDatosContenido());

        verify(contenidoRepository, times(1)).save(any(Contenido.class));
    }

    @Test
    void actualizarContenido_ShouldUpdateContent() throws IOException {
        // Datos de prueba generados con Faker
        Integer id = faker.number().numberBetween(1, 100);
        Integer nuevoCursoId = faker.number().numberBetween(1, 100);
        String nuevoFileName = faker.file().fileName();
        String nuevoContentType = "application/pdf";
        byte[] nuevoFileContent = faker.lorem().paragraph().getBytes();

        // Mock del archivo
        MultipartFile mockFile = new MockMultipartFile(
                "file",
                nuevoFileName,
                nuevoContentType,
                nuevoFileContent
        );

        // Contenido existente
        Contenido contenidoExistente = crearContenidoFake();
        contenidoExistente.setIdContenido(id);

        when(contenidoRepository.findById(id)).thenReturn(Optional.of(contenidoExistente));
        when(contenidoRepository.save(any(Contenido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Ejecutar el método
        Contenido resultado = contenidoService.actualizarContenido(id, nuevoCursoId, mockFile);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(id, resultado.getIdContenido());
        assertEquals(nuevoFileName, resultado.getNombre());
        assertEquals(nuevoContentType, resultado.getTipoContenido());
        assertEquals(nuevoCursoId, resultado.getIdCurso());
        assertArrayEquals(nuevoFileContent, resultado.getDatosContenido());

        verify(contenidoRepository, times(1)).findById(id);
        verify(contenidoRepository, times(1)).save(any(Contenido.class));
    }

    @Test
    void actualizarContenido_SinArchivo_ShouldUpdateOnlyCourseId() throws IOException {
        // Datos de prueba
        Integer id = faker.number().numberBetween(1, 100);
        Integer nuevoCursoId = faker.number().numberBetween(1, 100);

        // Contenido existente
        Contenido contenidoExistente = crearContenidoFake();
        contenidoExistente.setIdContenido(id);
        String nombreOriginal = contenidoExistente.getNombre();
        String tipoOriginal = contenidoExistente.getTipoContenido();
        byte[] datosOriginales = contenidoExistente.getDatosContenido();

        when(contenidoRepository.findById(id)).thenReturn(Optional.of(contenidoExistente));
        when(contenidoRepository.save(any(Contenido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Ejecutar el método sin archivo
        Contenido resultado = contenidoService.actualizarContenido(id, nuevoCursoId, null);

        // Verificaciones - solo debe cambiar el curso ID
        assertNotNull(resultado);
        assertEquals(id, resultado.getIdContenido());
        assertEquals(nombreOriginal, resultado.getNombre());
        assertEquals(tipoOriginal, resultado.getTipoContenido());
        assertEquals(nuevoCursoId, resultado.getIdCurso());
        assertArrayEquals(datosOriginales, resultado.getDatosContenido());

        verify(contenidoRepository, times(1)).findById(id);
        verify(contenidoRepository, times(1)).save(any(Contenido.class));
    }

    @Test
    void obtenerTodoContenido_ShouldReturnAllContent() {
        // Generar datos de prueba con Faker
        List<Contenido> contenidos = List.of(
                crearContenidoFake(),
                crearContenidoFake(),
                crearContenidoFake()
        );

        when(contenidoRepository.findAll()).thenReturn(contenidos);

        // Ejecutar el método
        List<Contenido> resultado = contenidoService.obtenerTodoContenido();

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(3, resultado.size());

        verify(contenidoRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorIdCurso_ShouldReturnCourseContent() {
        // Generar datos de prueba con Faker
        Integer cursoId = faker.number().numberBetween(1, 100);
        List<Contenido> contenidos = List.of(
                crearContenidoFake(cursoId),
                crearContenidoFake(cursoId)
        );

        when(contenidoRepository.findByIdCurso(cursoId)).thenReturn(contenidos);

        // Ejecutar el método
        List<Contenido> resultado = contenidoService.obtenerPorIdCurso(cursoId);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        resultado.forEach(c -> assertEquals(cursoId, c.getIdCurso()));

        verify(contenidoRepository, times(1)).findByIdCurso(cursoId);
    }

    @Test
    void actualizarContenido_ContenidoNoExiste_ShouldThrowException() {
        // Datos de prueba
        Integer id = faker.number().numberBetween(1, 100);
        Integer cursoId = faker.number().numberBetween(1, 100);

        when(contenidoRepository.findById(id)).thenReturn(Optional.empty());

        // Verificar que lanza excepción
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            contenidoService.actualizarContenido(id, cursoId, null);
        });

        assertEquals("Contenido no encontrado con id: " + id, exception.getMessage());
        verify(contenidoRepository, times(1)).findById(id);
        verify(contenidoRepository, never()).save(any(Contenido.class));
    }

    private Contenido crearContenidoFake() {
        return crearContenidoFake(faker.number().numberBetween(1, 100));
    }

    private Contenido crearContenidoFake(Integer cursoId) {
        Contenido contenido = new Contenido();
        contenido.setIdContenido(faker.number().numberBetween(1, 100));

        // Generar nombres de archivo más realistas
        String extension = faker.options().option("pdf", "docx", "pptx", "mp4", "jpg");
        String fileName = faker.book().title().replaceAll(" ", "_").toLowerCase() + "." + extension;
        contenido.setNombre(fileName);

        // Tipo de contenido realista
        String mimeType = switch (extension) {
            case "pdf" -> "application/pdf";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "mp4" -> "video/mp4";
            case "jpg" -> "image/jpeg";
            default -> "application/octet-stream";
        };
        contenido.setTipoContenido(mimeType);

        contenido.setDatosContenido(faker.lorem().paragraph().getBytes());
        contenido.setIdCurso(cursoId);
        return contenido;
    }
}