package com.edutech.repository;

import com.edutech.modelo.Inscripcion;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {
    List<Inscripcion> findByIdUsuario(Integer idUsuario);
    Optional<Inscripcion> findByIdUsuarioAndIdCurso(Integer idUsuario, Integer idCurso);
    Boolean existsByIdUsuarioAndIdCurso(Integer idUsuario, Integer idCurso);
}
