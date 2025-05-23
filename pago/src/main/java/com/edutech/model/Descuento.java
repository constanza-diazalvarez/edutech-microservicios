package com.edutech.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "descuento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Descuento {
    @Id
    private Long userId;
    private Double porcentaje;
}

