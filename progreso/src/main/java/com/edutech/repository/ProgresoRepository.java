package com.edutech.repository;

import com.edutech.model.Progreso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgresoRepository extends JpaRepository<Progreso, Integer> {
    List<Progreso> findByIdEstudiante(Integer idEstudiante);
    Optional<Progreso> findByIdEstudianteAndIdCurso(Integer idEstudiante, Integer idCurso);
}

