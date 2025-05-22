package com.edutech.service;

import com.edutech.model.Usuario;
import com.edutech.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> findById(Integer id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public void deleteById(Integer id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public Optional<Usuario> findByNombre(String nombre) {
        return usuarioRepository.findByNombre(nombre);
    }

    @Override
    public Optional<Usuario> findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Override
    public List<Usuario> findByRol_Rol(String rol) {
        return usuarioRepository.findByRol_Rol(rol);
    }

    @Override
    public List<Usuario> obtenerInstructores() {
        return usuarioRepository.findByRol_Rol("INSTRUCTOR");
    }
}
