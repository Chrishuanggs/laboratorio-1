package com.ginomarin.persistencia.repository;

import com.ginomarin.persistencia.model.Duenno;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DuennoRepository extends JpaRepository<Duenno, Long> {

    // Cargar los perros en la misma consulta para evitar N+1 y LazyInitializationException
    @Override
    @EntityGraph(attributePaths = "perros")
    List<Duenno> findAll();

    @Override
    @EntityGraph(attributePaths = "perros")
    Optional<Duenno> findById(Long id);

    @EntityGraph(attributePaths = "perros")
    Optional<Duenno> findByCedula(String cedula);
    
}
