package com.edutech.repository;

import com.edutech.model.Contenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContenidoRepository extends JpaRepository<Contenido, Integer> {

    List<Contenido> findByIdCurso(Integer idCurso);

}
