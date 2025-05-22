package com.edutech.service;

import com.edutech.model.Rol;
import com.edutech.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolServiceImpl implements RolService{
    private final RolRepository rolRepository;

    @Autowired
    public RolServiceImpl(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    public Rol findById(int id) {
        return rolRepository.findById(id).get();
    }

    @Override
    public List<Rol> findAll() {
        return rolRepository.findAll();
    }

    @Override
    public void save(Rol rol) {
        rolRepository.save(rol);
    }

    @Override
    public void delete(Rol rol) {
        rolRepository.delete(rol);
    }

    @Override
    public Optional<Rol> findByRol(String rol) {
        return rolRepository.findByRol(rol);
    }
}
