package com.ginomarin.persistencia.dto;

// Vista reducida del duenno, sin perros, para evitar ciclos al serializar Perro -> Duenno -> Perro
public record DuennoResumenDTO(
        Long id,
        String cedula,
        String nombre
) {
}
