package com.refugio.negocio;

import java.util.List;
import java.util.Optional;

import com.refugio.modelo.Adopcion;
import com.refugio.modelo.EstadoSolicitud;
import com.refugio.modelo.SolicitudAdopcion;
import com.refugio.modelo.VisitaSeguimiento;

/**
 * Contrato interno de Gestión de Adopciones, de solo lectura. Lo requiere
 * Consultas y Reportes: la ficha integral muestra solicitudes, adopción y
 * visitas, y el voluntario consulta las suyas.
 */
public interface IConsultaAdopciones {

    /**
     * Solicitudes filtradas por animal, por adoptante y por estado (todos
     * opcionales), de la más reciente a la más antigua (HU-16).
     */
    List<SolicitudAdopcion> solicitudesDe(String idAnimal, String idAdoptante,
                                          EstadoSolicitud estado);

    /** La adopción más reciente del animal, devuelta o no. */
    Optional<Adopcion> adopcionDe(String idAnimal);

    /** Todas las visitas de todas las adopciones del animal. */
    List<VisitaSeguimiento> visitasDe(String idAnimal);

    /** Visitas programadas del voluntario, con lo necesario para registrar su resultado. */
    List<VisitaAsignada> visitasAsignadasA(String idVoluntario);

    /**
     * Una visita con su contexto. {@code idSolicitud} hace falta para la ruta
     * de escritura del resultado, que cuelga de la solicitud.
     */
    record VisitaAsignada(String idSolicitud, String idAnimal, String idAdoptante,
                          VisitaSeguimiento visita) {
    }
}
