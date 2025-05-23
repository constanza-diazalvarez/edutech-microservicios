package com.edutech.controller;

import com.edutech.model.Usuario;
import com.edutech.service.UsuarioService;
import com.edutech.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class GestionUsuariosController {

    private final UsuarioService usuarioService;

    @Autowired
    public GestionUsuariosController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    /*
    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioService.findAll();
    }*/
    @GetMapping
    public List<Usuario> listarUsuarios(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        JwtUtil.validarRolToken(token, "ADMIN");
        return usuarioService.findAll();
    }


    @PostMapping
    public Usuario crearUsuario(@RequestHeader("Authorization") String authHeader, @RequestBody Usuario usuario) {
        String token = authHeader.replace("Bearer ", "");
        JwtUtil.validarRolToken(token, "ADMIN");
        return usuarioService.save(usuario);
    }


    @PutMapping("/{id}")
    public void eliminarUsuario(@RequestHeader("Authorization") String authHeader,
                                @PathVariable Integer id) {
        String token = authHeader.replace("Bearer ", "");
        JwtUtil.validarRolToken(token, "ADMIN");
        usuarioService.deleteById(id);
    }


    @GetMapping("/perfil/{nombreUsuario}")
    public Optional<Usuario> buscarUsuarioPorNombre(@PathVariable String nombreUsuario) {
        return usuarioService.findByNombre(nombreUsuario);
    }

    @PutMapping("/perfil/{nombreUsuario}")
    public Optional<Usuario> modificarUsuario(@RequestHeader("Authorization") String authHeader,
                                              @PathVariable("nombreUsuario") String nombreUsuario,
                                              @RequestBody Usuario datosNuevos) {
        String token = authHeader.replace("Bearer ", "");
        System.out.println(token);
        JwtUtil.validarRolToken(token, "ESTUDIANTE");

        Optional<Usuario> usuarioExistente = usuarioService.findByNombre(nombreUsuario);

        if (usuarioExistente.isPresent()) {
            Usuario u = usuarioExistente.get();

            u.setNombre(datosNuevos.getNombre());
            u.setCorreo(datosNuevos.getCorreo());
            u.setPassword(datosNuevos.getPassword());

            return Optional.of(usuarioService.save(u));
        }
        return Optional.empty();
    }


}
