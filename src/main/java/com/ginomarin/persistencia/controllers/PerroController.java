package com.ginomarin.persistencia.controllers;

import com.ginomarin.persistencia.dto.PerroDTO;
import com.ginomarin.persistencia.mappers.PerroMapper;
import com.ginomarin.persistencia.service.PerroService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/perro")
public class PerroController {

    private final PerroService perroService;
    private final PerroMapper perroMapper;

    public PerroController(PerroService perroService, PerroMapper perroMapper) {
        this.perroService = perroService;
        this.perroMapper = perroMapper;
    }

    @PreAuthorize("hasAuthority('perro:leer')")
    @GetMapping("/")
    public ResponseEntity<Optional<PerroDTO>> buscarPerro(){
        return ResponseEntity.ok(perroService.buscarUnPerro().map(perroMapper::toDto));
    }
}
