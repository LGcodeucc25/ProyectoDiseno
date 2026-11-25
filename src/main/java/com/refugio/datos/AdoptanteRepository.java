package com.refugio.datos;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.refugio.modelo.Adoptante;

/** Colección {@code adoptantes}. */
public interface AdoptanteRepository extends MongoRepository<Adoptante, String> {

    boolean existsByIdentificacionIndex(String identificacion);
}
