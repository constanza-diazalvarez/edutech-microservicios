package com.edutech.repository;

import com.edutech.model.Progreso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgresoRepository extends JpaRepository<Progreso, Integer> {
    List<Progreso> findByEstudianteId(Integer estudianteId);
}

