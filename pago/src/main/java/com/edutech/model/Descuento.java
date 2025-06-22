package com.edutech.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "descuentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Descuento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDescuento;

    private String codigo;
    private Double porcentaje;
}

