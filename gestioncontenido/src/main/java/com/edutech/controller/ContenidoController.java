package com.edutech.controller;

import com.edutech.model.Contenido;
import com.edutech.service.ContenidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/contenido")
@Tag(name = "Contenido", description = "Operaciones relacionadas con el contenido correspondiente a cada curso")
public class ContenidoController {
    @Autowired
    private final ContenidoService contenidoService;

    public ContenidoController(ContenidoService contenidoService) {
        this.contenidoService = contenidoService;
    }

    @GetMapping
    @Operation(
            summary = "Obtener todo el contenido sin distincion por curso",
            description = "Retorna una lista de todos los archivos",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de contenido obtenida correctamente"),
                    @ApiResponse(responseCode = "500", description = "Error interno al obtener contenido")
            }
    )
    public List<Contenido> obtenerContenido() {
        return contenidoService.obtenerTodoContenido();
    }

    @PostMapping(value="/subir",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Subir nuevo contenido",
            description = "Permite subir un archivo asociado a un curso, indicando el ID del curso.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contenido subido correctamente"),
                    @ApiResponse(responseCode = "400", description = "Parámetros inválidos o archivo faltante"),
                    @ApiResponse(responseCode = "500", description = "Error al subir el contenido")
            }
    )
    public ResponseEntity<?> subirArchivo(
            @RequestParam("idCurso") Integer idCurso,
            @RequestParam("archivo") MultipartFile archivo) throws IOException {
        Contenido contenido = contenidoService.guardarContenido(idCurso, archivo);
        return ResponseEntity.ok(contenido);
    }

    @PutMapping(value = "/actualizar/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Actualizar contenido",
            description = "Permite actualizar archivo asociado a contenido existente",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contenido actualizado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Contenido no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error al actualizar el contenido")
            }
    )
    public ResponseEntity<?> actualizarContenido(
            @PathVariable Integer id,
            @RequestParam(name="idCurso") Integer idCurso,
            @RequestParam(name = "archivo", required = false) MultipartFile archivo) {
        try {//manejo de errores
            Contenido contenidoActualizado = contenidoService.actualizarContenido(id, idCurso, archivo);
            return ResponseEntity.ok(contenidoActualizado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar el contenido");
        }
    }

    @GetMapping("/visualizar/contenido/{idContenido}")
    @Operation(
            summary = "Visualizar contenido",
            description = "Devuelve el archivo para ser visualizado o descargado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contenido visualizado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Contenido no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error al visualizar contenido")
            }
    )
    public ResponseEntity<byte[]> visualizarContenido(@PathVariable("idContenido") Integer idContenido){
        return contenidoService.visualizarContenido(idContenido);
    }

    @GetMapping("/traer-contenido/{idContenido}")
    @Operation(
            summary = "Obtener contenido por id",
            description = "Devuelve la información de la instancia Contenido",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contenido obtenido correctamente"),
                    @ApiResponse(responseCode = "404", description = "Contenido no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error al traer contenido")
            }
    )
    public ResponseEntity<Contenido> traerContenido(@PathVariable("idContenido") Integer idContenido){
        return contenidoService.traerContenido(idContenido);
    }

    @GetMapping("/curso/{idCurso}")
    @Operation(
            summary = "Obtener contenido de un curso",
            description = "Devuelve todos los contenidos asociados a un curso específico",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contenidos del curso obtenidos correctamente"),
                    @ApiResponse(responseCode = "404", description = "Curso no encontrado o sin contenidos"),
                    @ApiResponse(responseCode = "500", description = "Error al obtener contenido del curso")
            }
    )
    public ResponseEntity<List<Contenido>>  obtenerContenidoPorCurso(@PathVariable("idCurso") Integer idCurso){
        return ResponseEntity.ok(contenidoService.obtenerPorIdCurso(idCurso));
    }

}
