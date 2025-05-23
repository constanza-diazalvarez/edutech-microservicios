package com.edutech.controller;

import com.edutech.model.Progreso;
import com.edutech.service.ProgresoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/progreso")
public class ProgresoController {

    @Autowired
    private ProgresoService service;

    @GetMapping("/{estudianteId}")
    public List<Progreso> getProgreso(@PathVariable("estudianteId") Integer estudianteId) {
        return service.obtenerProgresoPorEstudiante(estudianteId);
    }
}

