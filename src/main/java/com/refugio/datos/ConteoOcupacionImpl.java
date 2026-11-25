package com.refugio.datos;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Implementación del fragmento {@link ConteoOcupacion}. Spring Data la
 * encuentra por el sufijo {@code Impl} y la combina con el repositorio.
 */
class ConteoOcupacionImpl implements ConteoOcupacion {

    private static final String COLECCION_ANIMALES = "animales";

    private final MongoTemplate mongo;

    ConteoOcupacionImpl(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    @Override
    public long contarAnimalesEn(String idEspacio) {
        return mongo.count(Query.query(Criteria.where("idEspacio").is(idEspacio)),
                COLECCION_ANIMALES);
    }
}
