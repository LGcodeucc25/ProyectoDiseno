package com.refugio.datos;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.refugio.modelo.Tratamiento;

/** Colección {@code tratamientos} (HU-11). */
public interface TratamientoRepository extends MongoRepository<Tratamiento, String> {

    List<Tratamiento> findByIdAnimalOrderByFechaInicioDesc(String idAnimal);
}
