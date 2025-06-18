package com.edutech.dto;

import lombok.Data;

@Data
public class CursoDTO {
    private Integer idCurso;
    private String nombreCurso;
    private String descripcion;
    private String categoria;
    private String nivel;
    private int duracion;
}
