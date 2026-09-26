package com.ginomarin.persistencia.repository;

import com.ginomarin.persistencia.model.Perro;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerroRepository extends JpaRepository<Perro, Long> {

    @Override
    @EntityGraph(attributePaths = "duennos")
    Optional<Perro> findById(Long id);
}
