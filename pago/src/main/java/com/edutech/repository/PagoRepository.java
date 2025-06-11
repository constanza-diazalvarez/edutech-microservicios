package com.edutech.repository;

import com.edutech.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago,Integer> {
    Optional<Pago> findByIdCliente(Integer idCliente);
}
