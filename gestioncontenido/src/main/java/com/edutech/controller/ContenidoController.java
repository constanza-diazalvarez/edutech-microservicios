package com.edutech.controller;

import com.edutech.model.Contenido;
import com.edutech.service.ContenidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
//@RequestMapping("/api/contenido")
@RequestMapping("/contenido")
public class ContenidoController {
    @Autowired
    private final ContenidoService contenidoService;

    public ContenidoController(ContenidoService contenidoService) {
        this.contenidoService = contenidoService;
    }

    @GetMapping
    public List<Contenido> obtenerContenido() {
        return contenidoService.obtenerTodoContenido();
    }

    @PostMapping(value="/subir",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirArchivo(
            @RequestParam("idCurso") Integer idCurso,
            @RequestParam("archivo") MultipartFile archivo) throws IOException {
        Contenido contenido = contenidoService.guardarContenido(idCurso, archivo);
        return ResponseEntity.ok(contenido);
    }

    @PutMapping(value = "/actualizar/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
}
