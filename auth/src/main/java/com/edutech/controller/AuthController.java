package com.edutech.controller;

import com.edutech.dto.LoginRequest;
import com.edutech.dto.LoginResponse;
import com.edutech.model.Usuario;
import com.edutech.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.JwtUtil;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        Optional<Usuario> usuario = usuarioService.findByNombre(loginRequest.getNombre());
        if (usuario.isPresent()) {
            Usuario usuarioActual = usuario.get();

            if (usuarioActual.getPassword().equals(loginRequest.getPassword())) {
                String rol = usuarioActual.getRol().getRol();
                Integer id = usuarioActual.getId();
                String token = JwtUtil.generarToken(usuarioActual.getNombre(), rol, usuarioActual.getId());
                return new LoginResponse(token, rol, id);
            }
        }
        throw new RuntimeException("Credenciales inválidas");
    }

    @PostMapping("/registrar")
    public Usuario registrarUsuario(@RequestBody Usuario usuario) {
        return usuarioService.save(usuario);
    }
}
