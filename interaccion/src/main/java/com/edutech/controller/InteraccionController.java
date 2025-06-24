package com.edutech.controller;

import com.edutech.model.Comentario;
import com.edutech.model.ResenaCalificacion;
import com.edutech.service.ComentarioService;
import com.edutech.service.ResenaCalificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/interaccion")
@Tag(name = "Interacciones", description = "Comentarios, reseñas y calificaciones de los cursos")
public class InteraccionController {
    @Autowired
    private ComentarioService comentarioService;
    @Autowired
    private ResenaCalificacionService resenaCalificacionService;

    @GetMapping
    @Operation(
            summary = "Listar todos los comentarios y reseñas",
            description = "Devuelve una lista combinada de todos los comentarios y todas las reseñas/calificaciones",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista combinada obtenida correctamente")
            }
    )
    public List<Object> listarTodo() {
        List<Object> listaTodo = new ArrayList<>();
        listaTodo.addAll(comentarioService.listarComentarios());
        listaTodo.addAll(resenaCalificacionService.listarResCal());
        return listaTodo;
    }

    @GetMapping("/comentarios")
    @Operation(
            summary = "Listar comentarios",
            description = "Devuelve una lista de todos los comentarios",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Comentarios obtenidos correctamente")
            }
    )
    public List<Comentario> listarComentarios() {
        return comentarioService.listarComentarios();
    }

    @GetMapping("/resena-calificacion")
    @Operation(
            summary = "Listar reseñas y calificaciones",
            description = "Devuelve una lista de todas las reseñas y calificaciones realizadas por los usuarios",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reseñas y calificaciones obtenidas correctamente")
            }
    )
    public List<ResenaCalificacion> listarResenaCalificacion() {
        return resenaCalificacionService.listarResCal();
    }

    @PostMapping("/comentarios/crear")
    @Operation(
            summary = "Crear comentario",
            description = "Permite a un usuario dejar un comentario sobre un curso",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Comentario creado correctamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos")
            }
    )
    public Comentario crearComentario(@RequestBody Comentario comentario) {
        return comentarioService.crearComentario(comentario);
    }

    @PostMapping
    @Operation(
            summary = "Crear reseña y calificación",
            description = "Permite a un usuario dejar una reseña escrita y una calificación numérica sobre un curso",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reseña y calificación registradas correctamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos")
            }
    )
    public ResponseEntity<?> crearResenaCalificacion(@RequestBody ResenaCalificacion rescal) {
        resenaCalificacionService.crearResCal(rescal);
        return ResponseEntity.ok().body("Resena/calificacion enviada correctamente");
    }


}
