package com.edutech.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="ResenaCalificacion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResenaCalificacion {
    @Id
    @Column(nullable = false)
    private Integer idCurso;
    @Column(nullable = false)
    private String nombreCurso;

    @Column(nullable = true)
    private String resena;
    @Column(nullable = true)
    private int calificacion;

}
