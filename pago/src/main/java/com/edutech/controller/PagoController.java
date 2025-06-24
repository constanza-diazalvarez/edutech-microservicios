package com.edutech.controller;

import com.edutech.model.Descuento;
import com.edutech.model.Pago;
import com.edutech.service.DescuentoService;
import com.edutech.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.annotations.processing.SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utils.JwtUtil;

import java.util.List;

@RestController
@RequestMapping("/pago")
@Tag(name = "Pagos", description = "Procesamiento y visualización de pagos de los cursos")
public class PagoController {

    @Autowired
    private DescuentoService service;
    @Autowired
    private PagoService pagoService;

    @PostMapping("/procesar-pago")
    @Operation(
            summary = "Procesar pago de curso",
            description = "Genera un nuevo pago para el usuario autenticado. Se puede aplicar un código de descuento opcional.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pago procesado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
            }
    )
    public Pago generarPago(
            HttpServletRequest request,
            @RequestParam(value = "codigoDescuento", required = false) String codigoDescuento
    ){
        return pagoService.generarPago(request, codigoDescuento);
    }

    @GetMapping("/mis-pagos")
    @Operation(
            summary = "Obtener pagos del usuario",
            description = "Devuelve una lista de todos los pagos realizados por el usuario que tiene iniciada la sesión",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pagos obtenidos correctamente"),
                    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
            }
    )
    public List<Pago> misPagos(HttpServletRequest request){
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Integer idUsuario = (Integer) JwtUtil.obtenerId(token);
        return pagoService.findByIdUsuario(idUsuario);
    }

}
