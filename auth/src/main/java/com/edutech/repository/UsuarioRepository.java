package com.edutech.repository;

import com.edutech.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    /*Optional<Usuario> findByNombre(String nombre);*/
    Optional<Usuario> findByCorreo(String correo);
}
