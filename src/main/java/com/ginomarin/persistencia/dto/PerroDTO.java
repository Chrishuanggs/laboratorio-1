package com.ginomarin.persistencia.dto;

import java.util.Set;

public record PerroDTO(
        Long id,
        String nombre,
        String raza,
        Set<DuennoResumenDTO> duennos
) {
}
