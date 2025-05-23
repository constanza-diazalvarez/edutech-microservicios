package com.edutech.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UsuarioDTO {
    private Integer idUsuario;
    @ManyToOne(fetch = FetchType.EAGER) //significa que al cargar un usuario se carga altiro su rol
    @JoinColumn(name = "ROL", nullable = false)
    private final String rol="INSTRUCTOR";
    private String nombre;
    private String apellido;
    private String email;
}
