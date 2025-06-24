package com.edutech.controller;

import com.edutech.dto.UsuarioDTO;
import com.edutech.model.Usuario;
import com.edutech.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Gestión de Usuarios", description = "Operaciones para gestionar usuarios del sistema")
public class GestionUsuariosController {
    private final UsuarioService usuarioService;

    @Autowired
    public GestionUsuariosController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los usuarios",
            description = "Obtiene una lista completa de usuarios registrados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente"),
                    @ApiResponse(responseCode = "403", description = "No tiene los permisos suficientes"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public List<Usuario> listarUsuarios(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        JwtUtil.validarRolToken(token, "ADMIN");
        return usuarioService.findAll();
    }

    @PostMapping("/registrar")
    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Registra un nuevo usuario utilizando un DTO que contiene nombre, correo y contraseña.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public Usuario registrarUsuario(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo usuario",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UsuarioDTO.class),
                            examples = @ExampleObject(value = """
                    {
                      "nombre": "Nombre",
                      "correo": "nombre@correo.com",
                      "password": "clave123"
                    }
                """)
                    )
            )
            @RequestBody UsuarioDTO usuarioDto) {
        return usuarioService.registrarUsuario(usuarioDto);
    }

    @PostMapping
    @Operation(
            summary = "Crear usuario",
            description = "Crea un usuario utilizando directamente un objeto Usuario.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public Usuario crearUsuario(HttpServletRequest request, @RequestBody Usuario usuario) {
        return usuarioService.crearUsuario(request, usuario);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina usuario por id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public void eliminarUsuario(HttpServletRequest request, @PathVariable("id") Integer id) {
        usuarioService.eliminarUsuario(request, id);
    }

    @GetMapping("/perfil/{nombreUsuario}")
    @Operation(
            summary = "Buscar usuario por nombre",
            description = "Busca un usuario por su nombre de usuario.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public Optional<Usuario> buscarUsuarioPorNombre(@PathVariable String nombreUsuario) {
        return usuarioService.findByNombre(nombreUsuario);
    }

    @PatchMapping("/perfil/editar")
    @Operation(
            summary = "Modificar usuario",
            description = "Modifica uno o más campos del perfil del usuario. Puedes enviar nombre, correo y/o password. Trabaja en el microservicio de gestión de usuarios y en auth.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario modificado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public ResponseEntity<?> modificarUsuario(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Campos a modificar. Puedes enviar uno o varios campos.",
                    required = true,
                    content = @Content(
                            schema = @Schema(type = "object"),
                            examples = {
                                    @ExampleObject(value = """
                        {
                          "nombre": "Nombre",
                          "correo": "nombre@correo.com",
                          "password": "clave123"
                        }
                    """)
                            }
                    )
            )
            @RequestBody Map<String, Object> usuarioMap, HttpServletRequest request) {
        return usuarioService.modificarUsuario(usuarioMap, request);
    }
}
