package com.ginomarin.persistencia.controllers;

import com.ginomarin.persistencia.dto.DuennoDTO;
import com.ginomarin.persistencia.mappers.DuennoMapper;
import com.ginomarin.persistencia.model.Duenno;
import com.ginomarin.persistencia.service.DuennoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/duenno")
@Slf4j
public class DuennoController {

    private final DuennoService duennoService;
    private final DuennoMapper duennoMapper;

    public DuennoController(DuennoService duennoService, DuennoMapper duennoMapper) {
        this.duennoService = duennoService;
        this.duennoMapper = duennoMapper;
    }

    @PreAuthorize("hasAuthority('duenno:leer')")
    @GetMapping
    public List<DuennoDTO> findAll() {
        log.info("Listando todos los duennos");
        return duennoMapper.toDtoList(duennoService.findAll());
    }

    @PreAuthorize("hasAuthority('duenno:leer')")
    @GetMapping("/")
    public ResponseEntity<DuennoDTO> findDuennoByCedula(@RequestParam String cedula){
        log.info("Buscando duenno por cedula");
        return duennoService.findDuennoByCedula(cedula)
                .map(duennoMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('duenno:leer')")
    @GetMapping("/{id}")
    public ResponseEntity<DuennoDTO> findById(@PathVariable Long id) {
        log.info("Buscando duenno con id {}", id);
        return ResponseEntity.of(duennoService.findById(id).map(duennoMapper::toDto));
    }

    @PreAuthorize("hasAuthority('duenno:escribir')")
    @PostMapping("/")
    public ResponseEntity<DuennoDTO> create(@RequestBody DuennoDTO duennoDTO) {
        log.info("Creando duenno");
        Duenno creado = duennoService.create(duennoMapper.toEntity(duennoDTO));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getId())
                .toUri();
        return ResponseEntity.created(location).body(duennoMapper.toDto(creado));
    }

    @PreAuthorize("hasAuthority('duenno:escribir')")
    @PutMapping("/{id}")
    public ResponseEntity<DuennoDTO> update(@PathVariable Long id, @RequestBody DuennoDTO duennoDTO) {
        log.info("Actualizando duenno con id {}", id);
        return ResponseEntity.of(duennoService.update(id, duennoMapper.toEntity(duennoDTO)).map(duennoMapper::toDto));
    }

    @PreAuthorize("hasAuthority('duenno:eliminar')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Eliminando duenno con id {}", id);
        return duennoService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('duenno:escribir')")
    @PutMapping("/{id}/perros/{perroId}")
    public ResponseEntity<DuennoDTO> agregarPerro(@PathVariable Long id, @PathVariable Long perroId) {
        log.info("Asignando perro {} al duenno {}", perroId, id);
        return ResponseEntity.of(duennoService.agregarPerro(id, perroId).map(duennoMapper::toDto));
    }

    @PreAuthorize("hasAuthority('duenno:escribir')")
    @DeleteMapping("/{id}/perros/{perroId}")
    public ResponseEntity<DuennoDTO> quitarPerro(@PathVariable Long id, @PathVariable Long perroId) {
        log.info("Quitando perro {} del duenno {}", perroId, id);
        return ResponseEntity.of(duennoService.quitarPerro(id, perroId).map(duennoMapper::toDto));
    }
}
