package com.edutech.service;

import com.edutech.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService{
    List<Usuario> findAll();
    Optional<Usuario> findById(Integer id);
    Usuario save(Usuario usuario);
    void deleteById(Integer id);

    Optional<Usuario> findByNombre(String nombre);
    Optional<Usuario> findByCorreo(String correo);
}
