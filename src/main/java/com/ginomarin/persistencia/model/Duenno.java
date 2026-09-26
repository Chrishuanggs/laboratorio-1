package com.ginomarin.persistencia.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Duenno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 9)
    String cedula;

    @Column
    List<String> correo;

    @Column
    String nombre;

    @ManyToMany
    @JoinTable(
            name = "duenno_perro",
            joinColumns = @JoinColumn(name = "duenno_id"),
            inverseJoinColumns = @JoinColumn(name = "perro_id")
    )
    Set<Perro> perros = new HashSet<>();

}
