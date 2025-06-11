package com.edutech.controller;

import com.edutech.model.Comentario;
import com.edutech.model.ResenaCalificacion;
import com.edutech.service.ComentarioService;
import com.edutech.service.ResenaCalificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
//@RequestMapping("/api/interacciones")
@RequestMapping("/interaccion")
public class InteraccionController {
    @Autowired
    private ComentarioService comentarioService;
    @Autowired
    private ResenaCalificacionService resenaCalificacionService;

    @GetMapping
    public List<Object> listarTodo() {
        List<Object> listaTodo = new ArrayList<>();
        listaTodo.addAll(comentarioService.listarComentarios());
        listaTodo.addAll(resenaCalificacionService.listarResCal());
        return listaTodo;
    }

    @GetMapping("/comentarios")
    public List<Comentario> listarComentarios() {
        return comentarioService.listarComentarios();
    }

    @GetMapping("/resena-calificacion")
    public List<ResenaCalificacion> listarResenaCalificacion() {
        return resenaCalificacionService.listarResCal();
    }

    @PostMapping("/comentarios/crear")
    public Comentario crearComentario(@RequestBody Comentario comentario) {
        return comentarioService.crearComentario(comentario);
    }

    @PostMapping
    public ResponseEntity<?> crearResenaCalificacion(@RequestBody ResenaCalificacion rescal) {
        resenaCalificacionService.crearResCal(rescal);
        return ResponseEntity.ok().body("Resena/calificacion enviada correctamente");
    }


}
