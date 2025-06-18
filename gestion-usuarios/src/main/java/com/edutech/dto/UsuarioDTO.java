package com.edutech.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Integer id;
    private String nombre;
    private String correo;
    private String password;
    private String estado;
}
