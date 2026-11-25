package com.refugio.datos;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.refugio.modelo.Animal;

/** Colección {@code animales}, con el historial de estados incrustado. */
public interface AnimalRepository extends MongoRepository<Animal, String>, BusquedaAnimales {
}
