package com.ginomarin.persistencia.mappers;

import com.ginomarin.persistencia.dto.CategoriaDTO;
import com.ginomarin.persistencia.model.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoriaMapper {

    CategoriaDTO toDto(Categoria categoria);

    @Mapping(target = "id", ignore = true)
    Categoria toEntity(CategoriaDTO categoriaDTO);
}
