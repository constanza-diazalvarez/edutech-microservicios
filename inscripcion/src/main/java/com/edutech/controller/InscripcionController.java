package com.edutech.controller;

import com.edutech.dto.CursoConContenidoDTO;
import com.edutech.modelo.Inscripcion;
import com.edutech.service.InscripcionService;
import com.edutech.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inscripcion")
@Tag(name = "Inscripción", description = "Operaciones relacionadas con la inscripción de estudiantes en cursos y el acceso a contenido de cursos inscritos")
public class InscripcionController {

    @Autowired
    private InscripcionService inscripcionService;

    @PostMapping("/{idCurso}")
    @Operation(
            summary = "Inscribir usuario en un curso",
            description = "Permite que un usuario se inscriba en un curso mediante su id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inscripción realizada correctamente"),
                    @ApiResponse(responseCode = "401", description = "Token inválido o no autorizado"),
                    @ApiResponse(responseCode = "404", description = "Curso no encontrado")
            }
    )
    public Inscripcion inscribirUsuario(
            @PathVariable("idCurso") Integer idCurso,
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization").replace("Bearer ", "");
        JwtUtil.validarToken(token, "ESTUDIANTE");

        Integer idUsuario = JwtUtil.obtenerIdUsuario(token);
        Integer idPago = (Integer) body.get("idPago");

        return inscripcionService.inscribirseACurso(idUsuario, idCurso, idPago);
    }

    @GetMapping("/mis-cursos")
    @Operation(
            summary = "Obtener cursos inscritos",
            description = "Devuelve lista de los cursos en los que el usuario está inscrito y el contenido asociado",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de cursos obtenida correctamente"),
                    @ApiResponse(responseCode = "401", description = "Token inválido o no autorizado")
            }
    )
    public ResponseEntity<List<CursoConContenidoDTO>> obtenerCursosPorUsuario(HttpServletRequest request){
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        JwtUtil.validarToken(token, "ESTUDIANTE");
        Integer idUsuario = JwtUtil.obtenerIdUsuario(token);

        List<CursoConContenidoDTO> cursos = inscripcionService.obtenerCursosPorUsuario(idUsuario, request);
        return ResponseEntity.ok(cursos);

    }

    @GetMapping("/mis-cursos/curso/{idCurso}/contenido/{idContenido}")
    @Operation(
            summary = "Acceder al contenido de un curso inscrito",
            description = "Permite a un usuario acceder al contenido de un curso en el que está inscrito. Si la relación usuario-curso no existe, se deniega el acceso.",
            responses = {
                    @ApiResponse(responseCode = "403", description = "El usuario no está inscrito en este curso"),
                    @ApiResponse(responseCode = "401", description = "Token inválido o no autorizado")
            }
    )
    public ResponseEntity<String> accederContenido(HttpServletRequest request,
                                                 @PathVariable("idCurso") Integer idCurso,
                                                 @PathVariable("idContenido") Integer idContenido){
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        JwtUtil.validarToken(token, "ESTUDIANTE");
        Integer idUsuario = JwtUtil.obtenerIdUsuario(token);

        if(!inscripcionService.existeUsuarioYCurso(idUsuario, idCurso)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        URI redireccion = URI.create("http://localhost:8080/api/contenido/visualizar/contenido/" + idContenido);
        return ResponseEntity.status(HttpStatus.FOUND).location(redireccion).build(); // 302
    }
}


