package com.edutech.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Progreso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProgreso;

    private Integer idEstudiante;
    private Integer idCurso;

    @ElementCollection
    private List<Integer> idContenidoCompleto;

    @ElementCollection
    private List<Integer> idContenidoVisualizado;
    private double porcentaje;
}

