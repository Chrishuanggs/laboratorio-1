package com.ginomarin.persistencia.mappers;

import com.ginomarin.persistencia.dto.ProductoDTO;
import com.ginomarin.persistencia.model.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductoMapper {

    @Mapping(source = "categoria.id", target = "categoriaId")
    ProductoDTO toDto(Producto productoDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    Producto toEntity(ProductoDTO productoDTO);
}

