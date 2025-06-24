package com.edutech.service;

import com.edutech.dto.ContenidoDTO;
import com.edutech.dto.CursoConContenidoDTO;
import com.edutech.dto.CursoDTO;
import com.edutech.modelo.Inscripcion;
import com.edutech.repository.InscripcionRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.fasterxml.jackson.databind.cfg.CoercionInputShape.Array;

@Service
public class InscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private RestTemplate restTemplate;

    public List<Inscripcion> obtenerInscripcionesPorUsuario(Integer idUsuario) {
        return inscripcionRepository.findByIdUsuario(idUsuario);
    }

    public Optional<Inscripcion> obtenerInscripcion(Integer idUsuario, Integer idCurso) {
        return inscripcionRepository.findByIdUsuarioAndIdCurso(idUsuario, idCurso);
    }

    public Inscripcion inscribirseACurso(Integer idUsuario, Integer idCurso, Integer idPago) {
        Optional<Inscripcion> existente = inscripcionRepository.findByIdUsuarioAndIdCurso(idUsuario, idCurso);
        if (existente.isPresent()) {
            throw new RuntimeException("El usuario ya está inscrito en este curso.");
        }

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setIdUsuario(idUsuario);
        inscripcion.setIdCurso(idCurso);
        inscripcion.setIdPago(idPago);
        inscripcion.setFechaInscripcion(LocalDateTime.now());

        return inscripcionRepository.save(inscripcion);
    }

    public List<CursoConContenidoDTO> obtenerCursosPorUsuario(Integer idUsuario, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
         HttpHeaders headers = new HttpHeaders();
         headers.add("Authorization", "Bearer " + token);
         HttpEntity<Void> entity = new HttpEntity<>(headers); //como es un get no hay body, por eso es void

        List<Inscripcion> inscripciones = obtenerInscripcionesPorUsuario(idUsuario);
        List<CursoConContenidoDTO> misCursos = new ArrayList<>();

        for (Inscripcion inscripcion : inscripciones) {
            Integer idCurso = inscripcion.getIdCurso();

            //↓aqui esta EL CURSO por inscripcion
            ResponseEntity<CursoDTO> curso = restTemplate.exchange(
                    "http://localhost:8080/api/cursos/" + idCurso,
                    HttpMethod.GET,
                    entity,
                    CursoDTO.class);

            //↓aqui estan todos los archivos que representan el contenido de EL CURSO
            ResponseEntity<ContenidoDTO[]> respuestaContenido = restTemplate.exchange(
                    "http://localhost:8080/api/contenido/curso/" + idCurso,
                    HttpMethod.GET,
                    entity,
                    ContenidoDTO[].class//no me deja hacer: List<CursoConContenidoDTO>.class porque se pierde el <CursoConContenidoDTO> → equivalente a List.class
            );
            List<ContenidoDTO> contenido = Arrays.asList(respuestaContenido.getBody());

            CursoConContenidoDTO cursoConContenidoDTO = new CursoConContenidoDTO();
            cursoConContenidoDTO.setCurso(curso.getBody());
            cursoConContenidoDTO.setContenido(contenido);

            misCursos.add(cursoConContenidoDTO);
        }
        return misCursos;
    }

    public Boolean existeUsuarioYCurso(Integer idUsuario, Integer idCurso) {
        return inscripcionRepository.existsByIdUsuarioAndIdCurso(idUsuario, idCurso);
    }
}
