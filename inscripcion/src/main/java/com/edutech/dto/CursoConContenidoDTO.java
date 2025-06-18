package com.edutech.dto;

import lombok.Data;

import java.util.List;

@Data
public class CursoConContenidoDTO {
    private CursoDTO curso;
    private List<ContenidoDTO> contenido;
}
