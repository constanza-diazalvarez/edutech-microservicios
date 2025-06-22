package com.edutech.controller;

import com.edutech.model.Curso;
import com.edutech.service.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @GetMapping
    public List<Curso> obtenerCursos(){
        return cursoService.listarCursos();
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearCurso(@RequestBody Curso curso) {
        Curso nuevoCurso = cursoService.crearCurso(curso);
        return ResponseEntity.ok().body("Curso creado con ID: " + nuevoCurso.getIdCurso());
    }

    @DeleteMapping("/eliminar/{idCurso}")
    public ResponseEntity<String> eliminarCurso(@PathVariable Integer idCurso) {
        cursoService.eliminarCursoPorId(idCurso);
        return ResponseEntity.ok().body("Curso eliminado");
    }

    @PostMapping("/vincular-instructor-curso/{usuarioId}{cursoId}")
    public ResponseEntity<String> vincularCurso(@PathVariable Integer usuarioId, @PathVariable Integer cursoId) {
        cursoService.vincularCursoConInstructor(usuarioId, cursoId);
        return ResponseEntity.ok().body("Instructor vinculado con curso");
    }

    @PutMapping("/actualizar/{idCurso}")
    public ResponseEntity<?> actualizarCurso(
            @PathVariable("idCurso") Integer idCurso,
            @RequestBody Curso cursoActualizado) {

        try {
            Curso curso = cursoService.actualizarCurso(idCurso, cursoActualizado);
            return ResponseEntity.ok(curso);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/buscar/categoria/{categoria}")
    public List<Curso> buscarCategoria(@PathVariable("categoria") String categoria) {
        return cursoService.obtenerCursosPorCategoria(categoria);
    }

    @GetMapping("/buscar/instructor/{idInstructor}")
    public List<Curso> buscarInstructor(@PathVariable("idInstructor") Integer idInstructor) {
        return cursoService.obtenerCursosPorUsuario(idInstructor);
    }

    @GetMapping("/buscar/nivel/{nivel}")
    public List<Curso> buscarNivel(@PathVariable("nivel") String nivel) {
        return cursoService.obtenerCursosPorNivel(nivel);
    }

    @GetMapping("/buscar/duracion/{duracion}")
    public List<Curso> buscarDuracion(@PathVariable("duracion") int duracion) {
        return cursoService.obtenerCursosPorDuracion(duracion);
    }

    @GetMapping("/buscar/palabra-clave/{palabra}")
    public List<Curso> buscarPalabraClave(@PathVariable("palabra") String palabra) {
        return cursoService.obtenerCursosPorPalabrasClave(palabra);
    }

    @GetMapping("/{idCurso}")
    public ResponseEntity<Curso> obtenerCursoPorId(@PathVariable("idCurso") Integer idCurso) {
        return ResponseEntity.ok(cursoService.obtenerCursoPorId(idCurso));
    }

}
