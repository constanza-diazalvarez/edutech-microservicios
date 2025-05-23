package com.edutech.service;

import com.edutech.model.Rol;

import java.util.List;
import java.util.Optional;

public interface RolService {
    public Rol findById(int id);
    public List<Rol> findAll();
    public void save(Rol rol);
    public void delete(Rol rol);

    Optional<Rol> findByRol(String rol);
}
