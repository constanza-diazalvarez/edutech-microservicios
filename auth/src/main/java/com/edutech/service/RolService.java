package com.edutech.service;

import com.edutech.model.Rol;

import java.util.List;
import java.util.Optional;

public interface RolService {
    Rol findById(int id);
    List<Rol> findAll();
    void save(Rol rol);
    void delete(Rol rol);

    Optional<Rol> findByRol(String rol);
}
