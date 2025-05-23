package com.edutech.service;

import com.edutech.model.Progreso;
import com.edutech.repository.ProgresoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgresoService {
    @Autowired
    private ProgresoRepository repo;

    public List<Progreso> obtenerProgresoPorEstudiante(Integer estudianteId) {
        return repo.findByEstudianteId(estudianteId);
    }
}

