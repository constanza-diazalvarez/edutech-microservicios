package com.edutech.controller;

import com.edutech.modelo.Inscripcion;
import com.edutech.service.InscripcionService;
import com.edutech.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inscripcion")
public class InscripcionController {

    @Autowired
    private InscripcionService inscripcionService;

    @PostMapping("/{idCurso}")
    public Inscripcion inscribirUsuario(@RequestHeader("Authorization") String authHeader,
                                        @PathVariable("idCurso") Integer idCurso) {

        String token = authHeader.replace("Bearer ", "");
        JwtUtil.validarToken(token, "ESTUDIANTE");

        Integer idUsuario = JwtUtil.obtenerIdUsuario(token);

        return inscripcionService.inscribirseACurso(idUsuario, idCurso);
    }
}


