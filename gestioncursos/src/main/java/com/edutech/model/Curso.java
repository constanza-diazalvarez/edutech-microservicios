package com.edutech.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name="Cursos")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCurso;
    @Column(nullable = false)
    private String nombreCurso;

    @Column(nullable = true)
    private Integer idUsuario;

    @Column(nullable = true)
    private String descripcion;//(opcional)
    @Column(nullable = false)
    private String categoria;
    @Column(nullable = false)
    private String nivel;
    @Column(nullable = false)
    private int duracion;
}
