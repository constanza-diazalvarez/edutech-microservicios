package com.edutech.controller;

import com.edutech.model.Curso;
import com.edutech.service.CursoService;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("/cursos")
@Tag(name = "Cursos", description = "Operaciones para gestionar cursos y su búsqueda")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @GetMapping
    @Operation(
            summary = "Obtener todos los cursos",
            description = "Devuelve una lista con todos los cursos disponibles.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
            }
    )
    public List<Curso> obtenerCursos(){
        return cursoService.listarCursos();
    }

    @PostMapping("/crear")
    @Operation(
            summary = "Crear un nuevo curso",
            description = "Crea un nuevo curso con la información proporcionada en el cuerpo de la solicitud.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Curso creado correctamente"),
                    @ApiResponse(responseCode = "403", description = "No tiene los permisos suficientes"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos")
            }
    )
    public ResponseEntity<?> crearCurso(@RequestBody Curso curso, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        if(!JwtUtil.validarRolToken(token, "GERENTE_CURSOS")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No está autorizado para crear un curso");
        }
        Curso nuevoCurso = cursoService.crearCurso(curso);
        return ResponseEntity.ok().body("Curso creado con ID: " + nuevoCurso.getIdCurso());
    }

    @DeleteMapping("/eliminar/{idCurso}")
    @Operation(
            summary = "Eliminar un curso",
            description = "Elimina el curso correspondiente al id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Curso eliminado correctamente"),
                    @ApiResponse(responseCode = "403", description = "No tiene los permisos suficientes"),
                    @ApiResponse(responseCode = "404", description = "Curso no encontrado")
            }
    )
    public ResponseEntity<String> eliminarCurso(@PathVariable Integer idCurso, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        JwtUtil.validarRolToken(token, "GERENTE_CURSOS");
        cursoService.eliminarCursoPorId(idCurso);
        return ResponseEntity.ok().body("Curso eliminado");
    }

    @PostMapping("/vincular-instructor-curso/{instructorId}/{cursoId}")
    @Operation(
            summary = "Vincular instructor a curso",
            description = "Asocia un usuario instructor a un curso por sus respectivos id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Instructor vinculado al curso"),
                    @ApiResponse(responseCode = "403", description = "No tiene los permisos suficientes"),
                    @ApiResponse(responseCode = "404", description = "Instructor o curso no encontrado")
            }
    )
    public ResponseEntity<String> vincularCurso(@PathVariable("instructorId") Integer instructorId, @PathVariable("cursoId") Integer cursoId, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        JwtUtil.validarRolToken(token, "GERENTE_CURSOS");
        cursoService.vincularCursoConInstructor(instructorId, cursoId);
        return ResponseEntity.ok().body("Instructor vinculado con curso");
    }

    @PutMapping("/actualizar/{idCurso}")
    @Operation(
            summary = "Actualizar curso",
            description = "Actualiza los datos de un curso existente usando su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Curso actualizado correctamente"),
                    @ApiResponse(responseCode = "403", description = "No tiene los permisos suficientes"),
                    @ApiResponse(responseCode = "404", description = "Curso no encontrado"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos")
            }
    )
    public ResponseEntity<?> actualizarCurso(
            @PathVariable("idCurso") Integer idCurso,
            @RequestBody Curso cursoActualizado, HttpServletRequest request) {

        try {
            String token = request.getHeader("Authorization").replace("Bearer ", "");
            JwtUtil.validarRolToken(token, "GERENTE_CURSOS");
            Curso curso = cursoService.actualizarCurso(idCurso, cursoActualizado);
            return ResponseEntity.ok(curso);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/buscar/categoria/{categoria}")
    @Operation(
            summary = "Buscar cursos por categoría",
            description = "Devuelve cursos que pertenecen a una categoría específica.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cursos obtenidos correctamente")
            }
    )
    public List<Curso> buscarCategoria(@PathVariable("categoria") String categoria) {
        return cursoService.obtenerCursosPorCategoria(categoria);
    }

    @GetMapping("/buscar/instructor/{idInstructor}")
    @Operation(
            summary = "Buscar cursos por instructor",
            description = "Devuelve todos los cursos asociados a un instructor.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cursos del instructor obtenidos correctamente")
            }
    )
    public List<Curso> buscarInstructor(@PathVariable("idInstructor") Integer idInstructor) {
        return cursoService.obtenerCursosPorInstructor(idInstructor);
    }

    @GetMapping("/buscar/nivel/{nivel}")
    @Operation(
            summary = "Buscar cursos por nivel",
            description = "Devuelve todos los cursos filtrados por su nivel",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cursos btenidos correctamente")
            }
    )
    public List<Curso> buscarNivel(@PathVariable("nivel") String nivel) {
        return cursoService.obtenerCursosPorNivel(nivel);
    }

    @GetMapping("/buscar/duracion/{duracion}")
    @Operation(
            summary = "Buscar cursos por duración",
            description = "Devuelve cursos cuya duración coincide con la solicitada.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cursos obtenidos correctamente")
            }
    )
    public List<Curso> buscarDuracion(@PathVariable("duracion") int duracion) {
        return cursoService.obtenerCursosPorDuracion(duracion);
    }

    @GetMapping("/buscar/palabra-clave/{palabra}")
    @Operation(
            summary = "Buscar cursos por palabra clave",
            description = "Devuelve los cursos que contengan cierta palabra",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cursos obtenidos correctamente")
            }
    )
    public List<Curso> buscarPalabraClave(@PathVariable("palabra") String palabra) {
        return cursoService.obtenerCursosPorPalabrasClave(palabra);
    }

    @GetMapping("/{idCurso}")
    @Operation(
            summary = "Obtener curso por id",
            description = "Devuelve el curso correspondiente al id indicado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Curso obtenido correctamente"),
                    @ApiResponse(responseCode = "404", description = "Curso no encontrado")
            }
    )
    public ResponseEntity<Curso> obtenerCursoPorId(@PathVariable("idCurso") Integer idCurso) {
        return ResponseEntity.ok(cursoService.obtenerCursoPorId(idCurso));
    }

}
