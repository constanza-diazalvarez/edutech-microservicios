package com.edutech.controller;

import com.edutech.model.Progreso;
import com.edutech.service.ProgresoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utils.JwtUtil;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/progreso")
@AllArgsConstructor
@Tag(name = "Progreso", description = "Registro y consulta de progreso de los usuarios en los cursos")
public class ProgresoController {

    private final ProgresoService progresoService;

    @PostMapping("/registrar-progreso/contenido/{idContenido}")
    @Operation(
            summary = "Registrar visualización de contenido",
            description = "Registra cuando un usuario ha visto por primera vez un contenido de uno de sus cursos",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Progreso registrado correctamente"),
                    @ApiResponse(responseCode = "500", description = "Error al registrar el progreso")
            }
    )
    public ResponseEntity<?> registrarVisualizacion(
            @PathVariable("idContenido") Integer idContenido,
            HttpServletRequest request
    ) {
        try {
            progresoService.registrarProgreso(idContenido, request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar visualización: " + e.getMessage());
        }
    }

    @GetMapping("/curso/{idCurso}")
    @Operation(
            summary = "Ver progreso de un curso del usuario",
            description = "Devuelve el porcentaje de avance del usuario autenticado en un curso",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Progreso obtenido correctamente"),
                    @ApiResponse(responseCode = "404", description = "No existe progreso registrado para este curso"),
                    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
            }
    )
    public ResponseEntity<?> verProgresoEnCurso(
            @PathVariable("idCurso") Integer idCurso,
            HttpServletRequest request
    ) {
        try {
            Integer idEstudiante = JwtUtil.obtenerId(JwtUtil.obtenerToken(request));
            Progreso progreso = progresoService.obtenerProgresoPorCurso(idEstudiante, idCurso);
            return ResponseEntity.ok(progreso);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay progreso registrado para este curso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado.");
        }
    }

    @GetMapping("/mis-progresos")
    @Operation(
            summary = "Ver progreso de todos los cursos inscritp",
            description = "Devuelve una lista con el porcentaje de progreso del usuario autenticado en todos los cursos",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de progresos obtenida correctamente"),
                    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
            }
    )
    public ResponseEntity<List<Progreso>> verTodosMisProgresos(
            HttpServletRequest request
    ) {

        Integer idEstudiante = JwtUtil.obtenerId(JwtUtil.obtenerToken(request));
        List<Progreso> progresos = progresoService.obtenerTodosLosProgresos(idEstudiante);
        return ResponseEntity.ok(progresos);
    }

}
