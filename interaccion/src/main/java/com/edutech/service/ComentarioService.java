package com.edutech.service;

import com.edutech.model.Comentario;
import com.edutech.repository.ComentarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComentarioService {
    @Autowired
    private ComentarioRepository comentarioRepository;

    public List<Comentario>listarComentarios(){
        return comentarioRepository.findAll();
    }

    public Comentario crearComentario(Comentario comentario){
        return comentarioRepository.save(comentario);
    }


}


