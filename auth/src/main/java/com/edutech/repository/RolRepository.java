package com.edutech.repository;

import com.edutech.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer>{
    Optional<Rol> findByRol(String rol);

}
