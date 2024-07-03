package com.aluracursos.LiterAlura.repository;

import com.aluracursos.LiterAlura.model.entity.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {
    List<Autor> findByFechaDeFallecimientoGreaterThan(Integer año);

    List<Autor> findByNombre(String nombre);
}