package com.refugio.datos;

import java.util.List;

import com.refugio.modelo.Animal;
import com.refugio.modelo.Especie;
import com.refugio.modelo.EstadoAnimal;
import com.refugio.modelo.Sexo;

/**
 * Fragmento de {@link AnimalRepository} para la búsqueda con filtros (HU-07).
 *
 * <p>Todos los parámetros son opcionales (null = sin filtro) y se combinan con Y.
 */
public interface BusquedaAnimales {

    List<Animal> buscar(Especie especie, String raza, Sexo sexo, String edad,
                        EstadoAnimal estado, String idVoluntario);
}
