package com.edutech.controller;

import com.edutech.dto.LoginRequest;
import com.edutech.dto.LoginResponse;
import com.edutech.dto.UsuarioDTO;
import com.edutech.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Autenticación", description = "Operaciones de autenticación y generación de tokens JWT")
public class AuthController {
    private final RestTemplate restTemplate;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesión",
            description = "Valida credenciales y genera token JWT si estas son correctas",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso"),
                    @ApiResponse(responseCode = "401", description = "Credenciales incorrectas"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/registrar")
    @Operation(
            summary = "Registrar nuevo usuario estudiante",
            description = "Registra un nuevo estudiante. El correo y contraseña se almacenan en el sistema de autenticación, mientras que el nombre se registra en el sistema de gestión de usuarios.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public ResponseEntity<?> registrarUsuario(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo usuario",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UsuarioDTO.class),
                            examples = @ExampleObject(value = """
                        {
                          "nombre": "Nombre Ejemplo",
                          "email": "nombre.ejemplo@correo.com",
                          "password": "clave123"
                        }
                    """)
                    )
            )
            @RequestBody UsuarioDTO usuarioDto) {
        return authService.registrar(usuarioDto);
    }

    @PatchMapping("/perfil/editar")
    @Operation(
            summary = "Modificar datos del usuario",
            description = "Modifica los atributos del usuario. No es necesario editar todos los campos, el usuario ingresa solo los que quiere cambiar",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
                    @ApiResponse(responseCode = "401", description = "No autorizado"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public ResponseEntity<?> modificarUsuario(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Mapa de datos a modificar (correo, password). Puedes enviar uno, varios o todos los campos.",
                    required = true,
                    content = @Content(
                            schema = @Schema(type = "object"),
                            examples = {
                                    @ExampleObject(value = """
                    {
                      "correo": "nuevo@correo.com",
                      "password": "clave123"
                    }
                """)
                            }
                    )
            )
            @RequestBody Map<String, Object> usuarioMap,
            HttpServletRequest request) {
        return authService.modificarUsuario(usuarioMap, request);
    }

    @GetMapping("/{idUsuario}")
    @Operation(summary = "Busca usuario", description = "Verifica si un usuario existe según su id")
    public boolean existeUsuario(@PathVariable("idUsuario") Integer idUsuario) {
        return authService.existeUsuario(idUsuario);
    }
}