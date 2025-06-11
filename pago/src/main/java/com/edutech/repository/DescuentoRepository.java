package com.edutech.repository;

import com.edutech.model.Descuento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DescuentoRepository extends JpaRepository<Descuento, Long> {
    public Optional<Descuento> findByCodigo(String codigo);
}

