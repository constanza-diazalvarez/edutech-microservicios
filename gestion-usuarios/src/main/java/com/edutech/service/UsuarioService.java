package com.edutech.service;

import com.edutech.dto.UsuarioDTO;
import com.edutech.model.Usuario;
import com.edutech.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import utils.JwtUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UsuarioService {

    @Autowired
    public UsuarioRepository usuarioRepository;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findUsuarioById(Integer id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        return usuario.get();
    }

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void deleteById(Integer id) {
        usuarioRepository.deleteById(id);
    }

    public Optional<Usuario> findByNombre(String nombre) {
        return usuarioRepository.findByNombre(nombre);
    }

    public List<Usuario> obtenerInstructores() {
        ArrayList<Usuario> instructores = new ArrayList<>();
        return instructores;//TODO → es para que no tire error
       //return usuarioRepository.findByRol_Rol("INSTRUCTOR");
    }

    public Usuario registrarUsuario(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setId(dto.getId());
        usuario.setNombre(dto.getNombre());
        usuario.setEstado(dto.getEstado());
        return save(usuario);
    }

    public Usuario crearUsuario(HttpServletRequest request, Usuario usuario) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        if (!JwtUtil.validarRolToken(token, "ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso suficiente"); //ERROR 403
        }
        return save(usuario);
    }

    public void eliminarUsuario(HttpServletRequest request, Integer id) {
        String token =  request.getHeader("Authorization").replace("Bearer ", "");
        if (!JwtUtil.validarRolToken(token, "ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso suficiente");//403
        }
        deleteById(id);
    }

    public ResponseEntity<?> modificarUsuario(Map<String, Object> usuarioMap, HttpServletRequest request) {
        String token = JwtUtil.obtenerToken(request);
        Integer id = JwtUtil.obtenerId(token);

        Usuario usuario = findUsuarioById(id);

        for (String key : usuarioMap.keySet()) {
            if (key != null) {
                switch (key) {
                    case "nombre" -> usuario.setNombre(usuarioMap.get(key).toString());
                    //aqui se pueden agregar campos como dirección, teléfono, etc.
                    default -> {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Campo no permitido: " + key);
                    }
                }
            }
        }

        save(usuario);
        return ResponseEntity.ok().build();
    }
}
