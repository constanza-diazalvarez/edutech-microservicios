package com.edutech.service;

import com.edutech.model.Descuento;
import com.edutech.repository.DescuentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DescuentoService {

    private final DescuentoRepository repository;

    public Descuento guardarDescuento(Descuento descuento) {
        return repository.save(descuento);
    }

}

