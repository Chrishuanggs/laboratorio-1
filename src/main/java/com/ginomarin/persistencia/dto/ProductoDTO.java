package com.ginomarin.persistencia.dto;

import java.math.BigDecimal;

public record ProductoDTO (
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Integer cantidadStock,
        Long categoriaId
) {
}
