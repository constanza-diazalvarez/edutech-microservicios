package com.edutech.repository;

import com.edutech.model.ResenaCalificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResenaCalificacionRepository extends JpaRepository<ResenaCalificacion, Integer> {


}
