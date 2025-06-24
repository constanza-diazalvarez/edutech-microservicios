package com.edutech.repository;

import com.edutech.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Integer> {

    //Buscar por instructor
    List<Curso> findByIdInstructor(Integer idInstructor);

    // Buscar por categoría
    List<Curso> findByCategoria(String categoria);

    // Buscar por nivel
    List<Curso> findByNivel(String nivel);

    // Buscar por duracion
    List<Curso> findByDuracion(int duracion);

    // Buscar cursos que contengan una palabra clave
    List<Curso> findByNombreCursoContainingIgnoreCase(String nombre);

    Curso findCursoByIdCurso(Integer idCurso);
}

