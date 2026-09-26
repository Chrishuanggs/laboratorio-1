package com.ginomarin.persistencia.service;

import com.ginomarin.persistencia.model.Duenno;
import com.ginomarin.persistencia.repository.DuennoRepository;
import com.ginomarin.persistencia.repository.PerroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DuennoService {

    private final DuennoRepository duennoRepository;
    private final PerroRepository perroRepository;

    public DuennoService(DuennoRepository duennoRepository, PerroRepository perroRepository) {
        this.duennoRepository = duennoRepository;
        this.perroRepository = perroRepository;
    }

    public List<Duenno> findAll() {
        return duennoRepository.findAll();
    }

    public Optional<Duenno> findById(Long id) {
        return duennoRepository.findById(id);
    }

    public Optional<Duenno> findDuennoByCedula(String cedula){
        return duennoRepository.findByCedula(cedula);
    }

    public Duenno create(Duenno duenno) {
        // Sin id para que save() haga INSERT y nunca sobrescriba un registro existente
        duenno.setId(null);
        // Los perros se asignan con agregarPerro/quitarPerro, no desde el body
        duenno.getPerros().clear();
        return duennoRepository.save(duenno);
    }

    @Transactional
    public Optional<Duenno> update(Long id, Duenno datos) {
        return duennoRepository.findById(id).map(duenno -> {
            duenno.setCedula(datos.getCedula());
            duenno.setNombre(datos.getNombre());
            duenno.setCorreo(datos.getCorreo());
            return duennoRepository.save(duenno);
        });
    }

    @Transactional
    public boolean delete(Long id) {
        if (!duennoRepository.existsById(id)) {
            return false;
        }
        duennoRepository.deleteById(id);
        return true;
    }

    @Transactional
    public Optional<Duenno> agregarPerro(Long duennoId, Long perroId) {
        return duennoRepository.findById(duennoId).flatMap(duenno ->
                perroRepository.findById(perroId).map(perro -> {
                    duenno.getPerros().add(perro);
                    return duenno;
                }));
    }

    @Transactional
    public Optional<Duenno> quitarPerro(Long duennoId, Long perroId) {
        return duennoRepository.findById(duennoId).flatMap(duenno ->
                perroRepository.findById(perroId).map(perro -> {
                    duenno.getPerros().remove(perro);
                    return duenno;
                }));
    }

}
