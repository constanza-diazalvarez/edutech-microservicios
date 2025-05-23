package com.edutech.service;

import com.edutech.model.*;
import com.edutech.repository.ResenaCalificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResenaCalificacionService {
    @Autowired
    private ResenaCalificacionRepository resenaCalificacionRepository;

    public List<ResenaCalificacion> listarResCal(){
        return resenaCalificacionRepository.findAll();
    }

    public ResenaCalificacion crearResCal(ResenaCalificacion rescal) {
        return resenaCalificacionRepository.save(rescal);
    }




}
