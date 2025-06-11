package com.edutech.service;

import com.edutech.model.Descuento;
import com.edutech.model.Pago;
import com.edutech.repository.DescuentoRepository;
import com.edutech.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PagoService {
    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    DescuentoRepository descuentoRepository;

    public Pago generarPago(Integer idUsuario, String codigo){
        Optional<Descuento> descuento = descuentoRepository.findByCodigo(codigo);

        Pago pago = new Pago();
        pago.setIdCliente(idUsuario);
        if(descuento.isPresent()){
            Descuento descEncontrado = descuento.get();
            pago.setDescuento(descEncontrado);
        }
        return pagoRepository.save(pago);
    }

    public List<Pago> findAll() {
        return pagoRepository.findAll();
    }

    public Pago save(Pago pago) {
        return pagoRepository.save(pago);
    }

}
