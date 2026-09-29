package com.ginomarin.persistencia.service;

import com.ginomarin.persistencia.dto.DuennoDTO;
import com.ginomarin.persistencia.mappers.DuennoMapper;
import com.ginomarin.persistencia.model.Duenno;
import com.ginomarin.persistencia.repository.DuennoRepository;
import com.ginomarin.persistencia.repository.PerroRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DuennoService {

    private final DuennoRepository duennoRepository;
    private final PerroRepository perroRepository;
    private final DuennoMapper duennoMapper;

    public DuennoService(DuennoRepository duennoRepository, PerroRepository perroRepository, DuennoMapper duennoMapper) {
        this.duennoRepository = duennoRepository;
        this.perroRepository = perroRepository;
        this.duennoMapper = duennoMapper;
    }


    public List<Duenno> findAll() {
        return duennoRepository.findAll();
    }


    public Optional<Duenno> findById(

            Long id
    ) {
        log.info("Buscando veterinario por cedula usando parametro: ", id);
        return duennoRepository.findById(id);
    }


    public Optional<Duenno> findDuennoByCedula(String cedula){
        return duennoRepository.findByCedula(cedula);
    }

    @Tool(name = "buscarDuennoPorCedula",
            description = "Busca a un duenno por número de cédula")
    public Optional<DuennoDTO> findDuennoByCedulaDTO(
            @ToolParam(description = "Número de cédula de un duenno")
            String cedula
    ){
        log.info("Buscando duenno por cedula: ", cedula);
        return duennoRepository.findByCedula(cedula).stream().map(duennoMapper::toDto).findFirst();
    }



    public Duenno create(
            Duenno duenno
    ) {
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
