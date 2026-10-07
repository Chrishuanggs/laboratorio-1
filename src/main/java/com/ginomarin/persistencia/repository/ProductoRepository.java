package com.ginomarin.persistencia.repository;

import com.ginomarin.persistencia.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}