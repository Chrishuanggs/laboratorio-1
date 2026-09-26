package com.ginomarin.persistencia.mappers;

import com.ginomarin.persistencia.dto.DuennoResumenDTO;
import com.ginomarin.persistencia.dto.PerroDTO;
import com.ginomarin.persistencia.dto.PerroResumenDTO;
import com.ginomarin.persistencia.model.Duenno;
import com.ginomarin.persistencia.model.Perro;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PerroMapper {

    PerroDTO toDto(Perro perro);

    PerroResumenDTO toResumenDto(Perro perro);

    // Los duennos se gestionan desde el lado dueño de la relación (Duenno.perros)
    @Mapping(target = "duennos", ignore = true)
    Perro toEntity(PerroDTO perroDTO);

    // Usado por toDto para mapear Perro.duennos sin depender de DuennoMapper (evita dependencia circular)
    DuennoResumenDTO toDuennoResumenDto(Duenno duenno);
}
