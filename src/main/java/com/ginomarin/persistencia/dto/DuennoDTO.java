package com.ginomarin.persistencia.dto;

import java.util.List;
import java.util.Set;

public record DuennoDTO(
        Long id,
        String cedula,
        List<String> correo,
        String nombre,
        Set<PerroResumenDTO> perros
) {
}
