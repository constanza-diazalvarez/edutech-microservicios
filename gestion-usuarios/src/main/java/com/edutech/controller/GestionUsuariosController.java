package com.edutech.controller;

import com.edutech.dto.UsuarioDTO;
import com.edutech.model.Usuario;
import com.edutech.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import utils.JwtUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class GestionUsuariosController {
    private final UsuarioService usuarioService;

    @Autowired
    public GestionUsuariosController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<Usuario> listarUsuarios(@RequestHeader("Authorization") String authHeader) {
        /*String token = authHeader.replace("Bearer ", "");
        JwtUtil.validarRolToken(token, "ADMIN");*/
        return usuarioService.findAll();
    }

    @PostMapping("/registrar")
    public Usuario registrarUsuario(@RequestBody UsuarioDTO usuarioDto) {
        return usuarioService.registrarUsuario(usuarioDto);
    }

    @PostMapping
    public Usuario crearUsuario(@RequestHeader("Authorization") String authHeader, @RequestBody Usuario usuario) {
        return usuarioService.crearUsuario(authHeader, usuario);
    }

    @PutMapping("/{id}")
    public void eliminarUsuario(@RequestHeader("Authorization") String authHeader, @PathVariable("id") Integer id) {
        usuarioService.eliminarUsuario(authHeader, id);
    }

    @GetMapping("/perfil/{nombreUsuario}")
    public Optional<Usuario> buscarUsuarioPorNombre(@PathVariable String nombreUsuario) {
        return usuarioService.findByNombre(nombreUsuario);
    }

    @PatchMapping("/perfil/editar")
    public ResponseEntity<?> modificarUsuario(@RequestBody Map<String, Object> usuarioMap, HttpServletRequest request) {
        return usuarioService.modificarUsuario(usuarioMap, request);
    }
//    @PostMapping("/registrar")
//    public Usuario registrarUsuario(@RequestBody UsuarioDTO usuarioDto) {
//        Usuario usuario = new Usuario();
//        usuario.setId(usuarioDto.getId());
//        usuario.setNombre(usuarioDto.getNombre());
//        usuario.setEstado(usuarioDto.getEstado());
//        return usuarioService.save(usuario);
//    }
//
//    @PostMapping
//    public Usuario crearUsuario(@RequestHeader("Authorization") String authHeader, @RequestBody Usuario usuario) {
//        String token = authHeader.replace("Bearer ", "");
//        if (!JwtUtil.validarRolToken(token, "ADMIN")){
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
//        }
//        return usuarioService.save(usuario);
//    }
//
//    @PutMapping("/{id}")
//    public void eliminarUsuario(@RequestHeader("Authorization") String authHeader,
//                                @PathVariable("id") Integer id) {
//        String token = authHeader.replace("Bearer ", "");
//        if (!JwtUtil.validarRolToken(token, "ADMIN")){
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
//        }
//        usuarioService.deleteById(id);
//    }
//
//
//    @GetMapping("/perfil/{nombreUsuario}")
//    public Optional<Usuario> buscarUsuarioPorNombre(@PathVariable String nombreUsuario) {
//        return usuarioService.findByNombre(nombreUsuario);
//    }
//
//    @PatchMapping("/perfil/editar")
//    public ResponseEntity<?> modificarUsuario(
//            @RequestBody Map<String, Object> usuarioMap,
//            HttpServletRequest request
//            ){
//        String token = JwtUtil.obtenerToken(request);
//        Integer id = JwtUtil.obtenerId(token);
//
//        Usuario usuario = usuarioService.findUsuarioById(id);
//
//        for (String key : usuarioMap.keySet()) {
//            if (key != null) {
//                switch (key) {
//                    case "nombre" -> usuario.setNombre(usuarioMap.get(key).toString());
//                    //agregar estado?
//                    //posibles atributos para agregar mas adelante: telegono, direccion, foto de perfil, etc
//                }
//            }
//        }
//        usuarioService.save(usuario);
//        return ResponseEntity.ok().build();
//    }
}
