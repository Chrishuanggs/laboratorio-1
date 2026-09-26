package com.ginomarin.persistencia.service;

import com.ginomarin.persistencia.model.Perro;
import com.ginomarin.persistencia.repository.PerroRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PerroService {

    private final PerroRepository perroRepository;

    public PerroService(PerroRepository perroRepository) {
        this.perroRepository = perroRepository;
    }

    public Optional<Perro> buscarUnPerro(){
        return perroRepository.findById(1L);
    }

}
