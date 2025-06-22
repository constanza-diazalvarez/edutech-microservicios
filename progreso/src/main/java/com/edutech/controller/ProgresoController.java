package com.edutech.controller;

import com.edutech.model.Progreso;
import com.edutech.service.ProgresoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/progreso")
@AllArgsConstructor
public class ProgresoController {

    private final ProgresoService progresoService;

    @PostMapping("/registrar-progreso/contenido/{idContenido}")
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
}
