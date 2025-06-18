package com.edutech.controller;

import com.edutech.dto.LoginRequest;
import com.edutech.dto.LoginResponse;
import com.edutech.dto.UsuarioDTO;
import com.edutech.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final RestTemplate restTemplate;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody UsuarioDTO usuarioDto) {
        return authService.registrar(usuarioDto);
    }

    @PatchMapping("/perfil/editar")
    public ResponseEntity<?> modificarUsuario(
            @RequestBody Map<String, Object> usuarioMap,
            HttpServletRequest request) {
        return authService.modificarUsuario(usuarioMap, request);
    }
}
