package com.edutech.service;

import com.edutech.modelo.Inscripcion;
import com.edutech.repository.InscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    public List<Inscripcion> obtenerInscripcionesPorUsuario(Integer idUsuario) {
        return inscripcionRepository.findByIdUsuario(idUsuario);
    }

    public Optional<Inscripcion> obtenerInscripcion(Integer idUsuario, Integer idCurso) {
        return inscripcionRepository.findByIdUsuarioAndIdCurso(idUsuario, idCurso);
    }

    public Inscripcion inscribirseACurso(Integer idUsuario, Integer idCurso) {
        Optional<Inscripcion> existente = inscripcionRepository.findByIdUsuarioAndIdCurso(idUsuario, idCurso);
        if (existente.isPresent()) {
            throw new RuntimeException("El usuario ya está inscrito en este curso.");
        }

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setIdUsuario(idUsuario);
        inscripcion.setIdCurso(idCurso);
        inscripcion.setFechaInscripcion(LocalDateTime.now());

        return inscripcionRepository.save(inscripcion);
    }
}
