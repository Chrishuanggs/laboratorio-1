package com.ginomarin.persistencia.dto;

// Vista reducida del perro, sin duennos, para evitar ciclos al serializar Duenno -> Perro -> Duenno
public record PerroResumenDTO(
        Long id,
        String nombre,
        String raza
) {
}
