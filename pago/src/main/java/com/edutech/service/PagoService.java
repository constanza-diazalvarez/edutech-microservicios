package com.edutech.service;

import com.edutech.model.Descuento;
import com.edutech.model.Pago;
import com.edutech.repository.DescuentoRepository;
import com.edutech.repository.PagoRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import utils.JwtUtil;

import java.util.List;
import java.util.Optional;

@Service
public class PagoService {
    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    DescuentoRepository descuentoRepository;

    public Pago generarPago(HttpServletRequest request, String codigo){
        String token = JwtUtil.obtenerToken(request);
        Integer id = JwtUtil.obtenerId(token);
        Optional<Descuento> descuento = descuentoRepository.findByCodigo(codigo);

        Pago pago = new Pago();
        pago.setIdUsuario(id);
        if(descuento.isPresent()){
            Descuento desctEncontrado = descuento.get();
            pago.setDescuento(desctEncontrado);
        } else {
            Descuento desctCero = descuentoRepository.findByCodigo("NO_APLICA").get();
            pago.setDescuento(desctCero);
        }
        return pagoRepository.save(pago);
    }

    public List<Pago> findAll() {
        return pagoRepository.findAll();
    }

    public Pago save(Pago pago) {
        return pagoRepository.save(pago);
    }

    public List<Pago> findByIdUsuario(Integer idUsuario) {
        return pagoRepository.findByIdUsuario(idUsuario);
    }

}
