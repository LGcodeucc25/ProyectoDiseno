package com.refugio.datos;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;

import com.refugio.modelo.Animal;
import com.refugio.modelo.Especie;
import com.refugio.modelo.EstadoAnimal;
import com.refugio.modelo.Sexo;

/**
 * Búsqueda de animales con filtros opcionales.
 *
 * <p>El filtro por estado se aplica sobre el estado <em>actual</em>, que es el
 * del último elemento de {@code historialEstados}: {@code $arrayElemAt} con
 * índice -1. Filtrar sobre el historial completo devolvería animales que
 * estuvieron disponibles alguna vez aunque hoy no lo estén.
 *
 * <p>Raza y edad son texto libre: coincidencia parcial sin distinguir
 * mayúsculas. Especie, sexo y estado: valor exacto del enum.
 */
class BusquedaAnimalesImpl implements BusquedaAnimales {

    private final MongoTemplate mongo;

    BusquedaAnimalesImpl(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    @Override
    public List<Animal> buscar(Especie especie, String raza, Sexo sexo, String edad,
                               EstadoAnimal estado, String idVoluntario) {
        List<Document> condiciones = new ArrayList<>();
        if (especie != null) {
            condiciones.add(new Document("especie", especie.name()));
        }
        if (sexo != null) {
            condiciones.add(new Document("sexo", sexo.name()));
        }
        if (tieneTexto(raza)) {
            condiciones.add(new Document("raza", contiene(raza)));
        }
        if (tieneTexto(edad)) {
            condiciones.add(new Document("edadAproximada", contiene(edad)));
        }
        if (tieneTexto(idVoluntario)) {
            condiciones.add(new Document("idVoluntario", idVoluntario));
        }
        if (estado != null) {
            Document ultimoEstado = new Document("$arrayElemAt",
                    List.of("$historialEstados.estado", -1));
            condiciones.add(new Document("$expr",
                    new Document("$eq", List.of(ultimoEstado, estado.name()))));
        }
        Document filtro = condiciones.isEmpty() ? new Document() : new Document("$and", condiciones);
        BasicQuery consulta = new BasicQuery(filtro);
        consulta.with(Sort.by("nombre"));
        return mongo.find(consulta, Animal.class);
    }

    private static boolean tieneTexto(String valor) {
        return valor != null && !valor.isBlank();
    }

    private static Pattern contiene(String texto) {
        return Pattern.compile(Pattern.quote(texto.trim()), Pattern.CASE_INSENSITIVE);
    }
}
