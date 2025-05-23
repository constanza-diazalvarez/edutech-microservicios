package com.edutech.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="Contenido")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Contenido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idContenido;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private Integer idCurso;
    @Column(nullable = false)
    private String tipoContenido;
    @Lob // Para datos binarios grandes
    @Column(nullable = false)
    private byte[] datosContenido;
}
