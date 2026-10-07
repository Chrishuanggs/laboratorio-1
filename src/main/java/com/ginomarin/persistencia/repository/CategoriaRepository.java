package com.ginomarin.persistencia.repository;

import com.ginomarin.persistencia.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}