package com.edutech.controller;

import com.edutech.model.Descuento;
import com.edutech.service.DescuentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/descuentos")
public class DescuentoController {

    @Autowired
    private DescuentoService service;

    @PostMapping
    public Descuento crearOActualizar(@RequestBody Descuento descuento) {
        return service.guardarDescuento(descuento);
    }

    @GetMapping("/{userId}")
    public Descuento obtenerPorUsuario(@PathVariable("userId") Long userId) {
        return service.obtenerDescuentoPorUsuario(userId).orElse(null);
    }
}
