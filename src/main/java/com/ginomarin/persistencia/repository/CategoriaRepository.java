package com.ginomarin.persistencia.repository;

import com.ginomarin.persistencia.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> id(Long id);

}

