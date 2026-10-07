package com.ginomarin.persistencia.service;

import com.ginomarin.persistencia.dto.ProductoDTO;
import com.ginomarin.persistencia.mappers.ProductoMapper;
import com.ginomarin.persistencia.model.Categoria;
import com.ginomarin.persistencia.model.Producto;
import com.ginomarin.persistencia.repository.CategoriaRepository;
import com.ginomarin.persistencia.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;

    public List<ProductoDTO> listar() {
        return productoRepository.findAll()
                .stream()
                .map(productoMapper::toDto)
                .toList();
    }

    public ProductoDTO crear(ProductoDTO dto) {
        Categoria categoria = buscarCategoria(dto.categoriaId());
        Producto producto = productoMapper.toEntity(dto);
        producto.setCategoria(categoria);
        Producto guardado = productoRepository.save(producto);
        return productoMapper.toDto(guardado);
    }

    public Optional<ProductoDTO> buscarPorId(Long id) {
        return productoRepository.findById(id).map(productoMapper::toDto);
    }

    public boolean eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            return false;
        }
        productoRepository.deleteById(id);
        return true;
    }

    public Optional<ProductoDTO> actualizar(Long id, ProductoDTO dto) {
        Categoria categoria = buscarCategoria(dto.categoriaId());
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setNombre(dto.nombre());
                    producto.setDescripcion(dto.descripcion());
                    producto.setPrecio(dto.precio());
                    producto.setCantidadStock(dto.cantidadStock());
                    producto.setCategoria(categoria);
                    Producto guardado = productoRepository.save(producto);
                    return productoMapper.toDto(guardado);
                });
    }
    private Categoria buscarCategoria(Long categoriaId) {
        if (categoriaId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El categoriaId es obligatorio");
        }
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "No existe la categoria con id " + categoriaId));
    }
}