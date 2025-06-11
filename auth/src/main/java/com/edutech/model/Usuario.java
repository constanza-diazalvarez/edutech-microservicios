package com.edutech.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data //genera: getters, setters, toString(), equals(), hashCode()
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //quitar por duplicidad con gestion-usuarios
    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String correo;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER) //significa que al cargar un usuario se carga altiro su rol@JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;
}
