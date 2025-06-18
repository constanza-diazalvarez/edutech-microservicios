package com.edutech.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    /*LO PASO DESDE AUTH, YA NO NECESITO QUE SE GENERE AUTOMATICAMENTE
    @GeneratedValue(strategy = GenerationType.IDENTITY)*/
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    /*
    @Column(nullable = false)
    private String correo;*/

    /*
    @Column(nullable = false)
    private String password;*/

    /*
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;*/

    @Column(nullable = false)
    private String estado;

}
