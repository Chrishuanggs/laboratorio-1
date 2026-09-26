package com.ginomarin.persistencia.mappers;

import com.ginomarin.persistencia.dto.DuennoDTO;
import com.ginomarin.persistencia.model.Duenno;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = PerroMapper.class)
public interface DuennoMapper {

    DuennoDTO toDto(Duenno duenno);

    List<DuennoDTO> toDtoList(List<Duenno> duennos);

    // Los perros se asignan con agregarPerro/quitarPerro, no desde el body
    @Mapping(target = "perros", ignore = true)
    Duenno toEntity(DuennoDTO duennoDTO);
}
