package com.cursojava.curso.models;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@ToString
@EqualsAndHashCode
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Getter
    @Setter
    private Long id;

    @Column(name = "nombre", nullable = false)
    @Getter
    @Setter
    private String nombre;

    @Column(name = "apellido", nullable = false)
    @Getter
    @Setter
    private String apellido;

    @Column(name = "email", nullable = false, unique = true)
    @Getter
    @Setter
    private String email;

    @Column(name = "telefono")
    @Getter
    @Setter
    private String telefono;

    @Column(name = "password", nullable = false)
    @Getter
    @Setter
    private String password;
}
