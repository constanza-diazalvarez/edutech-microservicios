package com.edutech.controller;

import com.edutech.dto.CursoConContenidoDTO;
import com.edutech.modelo.Inscripcion;
import com.edutech.service.InscripcionService;
import com.edutech.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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

    @GetMapping("/mis-cursos")
    public ResponseEntity<List<CursoConContenidoDTO>> obtenerCursosPorUsuario(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        JwtUtil.validarToken(token, "ESTUDIANTE");
        Integer idUsuario = JwtUtil.obtenerIdUsuario(token);

        List<CursoConContenidoDTO> cursos = inscripcionService.obtenerCursosPorUsuario(idUsuario);
        return ResponseEntity.ok(cursos);

    }

    @GetMapping("/mis-cursos/curso/{idCurso}/contenido/{idContenido")
    public ResponseEntity<String> accederContenido(HttpServletRequest request,
                                                 @PathVariable("idCurso") Integer idCurso,
                                                 @PathVariable("idContenido") Integer idContenido){
        String token = request.getHeader("Authorization");
        JwtUtil.validarToken(token, "ESTUDIANTE");
        Integer idUsuario = JwtUtil.obtenerIdUsuario(token);

        if(!inscripcionService.existeUsuarioYCurso(idUsuario, idCurso)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        URI redireccion = URI.create("http://localhost:8080/api/contenido/visualizar/contenido/" + idContenido);
        return ResponseEntity.status(HttpStatus.FOUND).location(redireccion).build(); // 302
    }
}


