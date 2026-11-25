package com.refugio.datos;

import java.util.Collection;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.refugio.modelo.EstadoSolicitud;
import com.refugio.modelo.SolicitudAdopcion;

/**
 * Colección {@code solicitudes}, con la adopción y sus visitas incrustadas.
 * Las consultas se ordenan de la más reciente a la más antigua (HU-16).
 */
public interface SolicitudAdopcionRepository extends MongoRepository<SolicitudAdopcion, String> {

    List<SolicitudAdopcion> findByIdAnimalAndEstado(String idAnimal, EstadoSolicitud estado);

    List<SolicitudAdopcion> findByIdAnimalOrderByFechaSolicitudDesc(String idAnimal);

    List<SolicitudAdopcion> findByIdAdoptanteOrderByFechaSolicitudDesc(String idAdoptante);

    List<SolicitudAdopcion> findAllByOrderByFechaSolicitudDesc();

    boolean existsByIdAnimalAndIdAdoptanteAndEstadoIn(String idAnimal, String idAdoptante,
                                                      Collection<EstadoSolicitud> estados);

    /** Solicitudes con alguna visita asignada al voluntario (HU-19). */
    @Query("{ 'adopcion.visitas.idVoluntario': ?0 }")
    List<SolicitudAdopcion> buscarConVisitasDe(String idVoluntario);
}
