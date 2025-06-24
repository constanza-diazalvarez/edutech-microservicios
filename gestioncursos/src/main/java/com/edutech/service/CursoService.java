package com.edutech.service;

import com.edutech.model.Curso;
import com.edutech.repository.CursoRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CursoService {
    private CursoRepository cursoRepository;
    private final RestTemplate restTemplate;

    public List<Curso> listarCursos() {
        return cursoRepository.findAll();
    }

    public Curso crearCurso(Curso curso) {
        return cursoRepository.save(curso); // ← Aquí se crea el curso
    }

    public void eliminarCursoPorId(Integer idCurso) {
        if (!cursoRepository.existsById(idCurso)) {
            throw new RuntimeException("Curso con ID " + idCurso + " no existe");
        }
        cursoRepository.deleteById(idCurso);
    }

    @Transactional
    public Curso actualizarCurso(Integer idCurso, Curso cursoActualizado) {
        return cursoRepository.findById(idCurso)
                .map(cursoExistente -> {
                    // Actualizar solo los campos no nulos
                    if (cursoActualizado.getNombreCurso() != null) {
                        cursoExistente.setNombreCurso(cursoActualizado.getNombreCurso());
                    }
                    if (cursoActualizado.getDescripcion() != null) {
                        cursoExistente.setDescripcion(cursoActualizado.getDescripcion());
                    }
                    if (cursoActualizado.getCategoria() != null) {
                        cursoExistente.setCategoria(cursoActualizado.getCategoria());
                    }
                    if (cursoActualizado.getNivel() != null) {
                        cursoExistente.setNivel(cursoActualizado.getNivel());
                    }
                    if (cursoActualizado.getDuracion() != 0) {
                        cursoExistente.setDuracion(cursoActualizado.getDuracion());
                    }
                    // No actualizamos idUsuario aquí (usar el endpoint de vinculación)
                    return cursoRepository.save(cursoExistente);
                })
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + idCurso));
    }


    public Curso vincularCursoConInstructor(Integer instructorId, Integer cursoId) {
        Boolean instructorExiste = restTemplate.getForObject(
                "http://localhost:8080/api/auth/" + instructorId,
                Boolean.class
        );
        if (!instructorExiste) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor no encontrado");
        }
        Optional<Curso> c = cursoRepository.findById(cursoId);
        if (c.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado");
        }
        Curso curso = c.get();
        curso.setIdInstructor(instructorId);
        return cursoRepository.save(curso);
    }

    public List<Curso> obtenerCursosPorInstructor(Integer idInstructor) {
        return cursoRepository.findByIdInstructor(idInstructor);
    }

    public List<Curso> obtenerCursosPorCategoria(String categoria) {
        return cursoRepository.findByCategoria(categoria);
    }

    public List<Curso> obtenerCursosPorNivel(String nivel) {
        return cursoRepository.findByNivel(nivel);
    }

    public List<Curso> obtenerCursosPorDuracion(Integer duracion) {
        return cursoRepository.findByDuracion(duracion);
    }

    public List<Curso> obtenerCursosPorPalabrasClave(String palabraClave) {
        return cursoRepository.findByNombreCursoContainingIgnoreCase(palabraClave);
    }

    public Curso obtenerCursoPorId(Integer idCurso) {
        Optional<Curso> curso = cursoRepository.findById(idCurso);
        if (curso.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado");
        }
        return curso.get();
    }
}

