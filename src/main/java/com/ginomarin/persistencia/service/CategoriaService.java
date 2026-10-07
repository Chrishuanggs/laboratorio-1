package com.ginomarin.persistencia.service;

import com.ginomarin.persistencia.dto.CategoriaDTO;
import com.ginomarin.persistencia.mappers.CategoriaMapper;
import com.ginomarin.persistencia.model.Categoria;
import com.ginomarin.persistencia.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public List<CategoriaDTO> listar() {
        return categoriaRepository.findAll()
                .stream()
                .map(categoriaMapper::toDto)
                .toList();
    }

    public CategoriaDTO crear(CategoriaDTO dto) {
        Categoria categoria = categoriaMapper.toEntity(dto);
        Categoria guardada = categoriaRepository.save(categoria);
        return categoriaMapper.toDto(guardada);
    }

    public Optional<CategoriaDTO> buscarPorId(Long id) {
        return categoriaRepository.findById(id).map(categoriaMapper::toDto);
    }

    public boolean eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            return false;
        }
        categoriaRepository.deleteById(id);
        return true;
    }

    public Optional<CategoriaDTO> actualizar(Long id, CategoriaDTO dto) {
        return categoriaRepository.findById(id)
                .map(categoria -> {
                    categoria.setDescripcion(dto.descripcion());
                    categoria.setNombre(dto.nombre());
                    Categoria guardada = categoriaRepository.save(categoria);
                    return categoriaMapper.toDto(guardada);
                });
    }
}