package com.edutech.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="Comentarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idComentario;
    @Column(nullable = false)
    private Integer idCurso;
    @Column(nullable = false)
    private Integer idUsuario;
    @Column(nullable = false)
    private String comentario;

}
