package com.refugio.datos;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.refugio.modelo.Vacunacion;

/** Colección {@code vacunaciones}: cada aplicación concreta de una vacuna (HU-10). */
public interface VacunacionRepository extends MongoRepository<Vacunacion, String> {

    List<Vacunacion> findByIdAnimalOrderByFechaAplicacionDesc(String idAnimal);
}
