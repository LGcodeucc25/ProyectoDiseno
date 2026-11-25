package com.refugio.datos;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.refugio.modelo.EspacioAlojamiento;

/**
 * Colección {@code espacios}.
 *
 * <p>Hereda de {@link ConteoOcupacion} el conteo de animales alojados, que
 * Spring Data enlaza con {@link ConteoOcupacionImpl}.
 */
public interface EspacioAlojamientoRepository
        extends MongoRepository<EspacioAlojamiento, String>, ConteoOcupacion {

    boolean existsByNombreIgnoreCase(String nombre);
}
