package com.edutech.controller;

import com.edutech.model.Descuento;
import com.edutech.model.Pago;
import com.edutech.service.DescuentoService;
import com.edutech.service.PagoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pago")
public class PagoController {

    @Autowired
    private DescuentoService service;
    @Autowired
    private PagoService pagoService;

    @PostMapping("/procesar-pago/{idUsuario}")
    public Pago generarPago(
            @PathVariable("idUsuario") Integer idsuario,
            @RequestParam(value = "codigoDescuento", required = false) String codigoDescuento
    ){
        return pagoService.generarPago(idsuario, codigoDescuento);
    }

    /*
    @PostMapping
    public Descuento crearOActualizar(@RequestBody Descuento descuento) {
        return service.guardarDescuento(descuento);
    }

    @GetMapping("/{userId}")
    public Descuento obtenerPorUsuario(@PathVariable("userId") Long userId) {
        return service.obtenerDescuentoPorUsuario(userId).orElse(null);
    }*/
}
