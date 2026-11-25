package com.refugio.datos;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.refugio.modelo.TipoVacuna;

/** Colección {@code tiposVacuna}: el catálogo (HU-09). */
public interface TipoVacunaRepository extends MongoRepository<TipoVacuna, String> {

    boolean existsByNombreIgnoreCase(String nombre);

    /** Al renombrar: ¿otro tipo, distinto de este, ya usa ese nombre? */
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, String id);
}
