package com.edutech.service;

import com.edutech.model.Contenido;
import com.edutech.repository.ContenidoRepository;
import jakarta.transaction.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ContenidoService {

    private final ContenidoRepository contenidoRepository;

    // Inyección por constructor (recomendado)
    public ContenidoService(ContenidoRepository contenidoRepository) {
        this.contenidoRepository = contenidoRepository;
    }

    @Transactional
    public Contenido guardarContenido(Integer cursoId, MultipartFile contenido) throws IOException {
        Contenido nuevoContenido = new Contenido();
        nuevoContenido.setIdCurso(cursoId);
        nuevoContenido.setNombre(contenido.getOriginalFilename());
        nuevoContenido.setTipoContenido(contenido.getContentType());
        nuevoContenido.setDatosContenido(contenido.getBytes());

        return contenidoRepository.save(nuevoContenido);
    }

    @Transactional
    public Contenido actualizarContenido(Integer id, Integer idCurso, MultipartFile archivo) throws IOException {
        // Buscar el contenido existente
        Contenido contenidoExistente = contenidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado con id: " + id));

        // Actualizar campos
        contenidoExistente.setIdCurso(idCurso);

        if (archivo != null && !archivo.isEmpty()) {
            contenidoExistente.setNombre(archivo.getOriginalFilename());
            contenidoExistente.setTipoContenido(archivo.getContentType());
            contenidoExistente.setDatosContenido(archivo.getBytes());
        }

        return contenidoRepository.save(contenidoExistente);
    }

    public List<Contenido> obtenerTodoContenido() {
        return contenidoRepository.findAll();
    }

    public List<Contenido> obtenerPorIdCurso(Integer idCurso) {
        return contenidoRepository.findByIdCurso(idCurso);
    }
}