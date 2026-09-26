package com.ginomarin.persistencia.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Perro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    String nombre;

    @Column
    String raza;

    @ManyToMany(mappedBy = "perros")
    Set<Duenno> duennos = new HashSet<>();

}
