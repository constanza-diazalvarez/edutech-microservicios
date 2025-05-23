package com.edutech.service;

import com.edutech.model.Curso;
import com.edutech.model.UsuarioDTO;
import com.edutech.repository.CursoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CursoService {
    @Autowired //

    private CursoRepository cursoRepository;
    private RestTemplate restTemplate;

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

    public Curso vincularCursoConInstructor(Integer usuarioId, Integer cursoId) {
        // Llamada REST al microservicio X para obtener instructor
        String url = "http://microservicio-X/api/usuarios/{usuarioId}";
        UsuarioDTO instructor = restTemplate.getForObject(url, UsuarioDTO.class, usuarioId);
        Curso curso = cursoRepository.findCursoByIdCurso(cursoId);
        if (instructor == null) {
            throw new RuntimeException("Instructor no encontrado");
        }
        // Copiar el idInstructor en el curso
        curso.setIdUsuario(instructor.getIdUsuario());
        // Guardar curso con el idInstructor copiado
        return cursoRepository.save(curso);
    }

    public List<Curso> obtenerCursosPorUsuario(Integer idUsuario) {
        return cursoRepository.findByIdUsuario(idUsuario);
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
}

