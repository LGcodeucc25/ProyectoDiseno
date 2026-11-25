package com.refugio.negocio;

import java.time.LocalDate;

import com.refugio.modelo.Adoptante;
import com.refugio.modelo.Calificacion;
import com.refugio.modelo.ResultadoEvaluacion;
import com.refugio.modelo.SolicitudAdopcion;
import com.refugio.modelo.VisitaSeguimiento;

/**
 * Fachada pública de Gestión de Adopciones (HU-12 a HU-20).
 * La consume {@code AdopcionController}.
 */
public interface IGestionAdopciones {

    Adoptante registrarAdoptante(Adoptante datos);

    /** Edita los datos del adoptante; no afecta las evaluaciones ya registradas (HU-12). */
    Adoptante actualizarAdoptante(String idAdoptante, Adoptante datos);

    /** Solo sobre animales Disponibles. Queda Pendiente. */
    SolicitudAdopcion registrarSolicitud(String idAnimal, String idAdoptante, LocalDate fecha);

    /**
     * Aprobar pone el animal En proceso de adopción y rechaza las demás
     * pendientes del mismo animal (HU-14).
     */
    SolicitudAdopcion evaluarSolicitud(String idSolicitud, ResultadoEvaluacion resultado,
                                       LocalDate fecha, String observaciones, String idEvaluador);

    /** Cancelar una aprobada devuelve el animal a Disponible, sin reabrir las rechazadas (HU-15). */
    SolicitudAdopcion cancelarSolicitud(String idSolicitud, LocalDate fecha, String motivo);

    /** El animal pasa a Adoptado y libera su espacio (HU-17). */
    SolicitudAdopcion registrarAdopcion(String idSolicitud, LocalDate fechaEntrega);

    /**
     * El animal vuelve En valoración y sin espacio, y se cancelan las visitas
     * programadas. La adopción no se borra: recibe fecha y motivo (HU-20).
     */
    SolicitudAdopcion registrarDevolucion(String idSolicitud, LocalDate fecha, String motivo);

    VisitaSeguimiento programarVisita(String idSolicitud, LocalDate fechaPrevista,
                                      String idVoluntario, String indicaciones);

    /**
     * Cambia la fecha prevista y, si se indica, el voluntario. Solo sobre
     * visitas programadas; no deja historial de la fecha anterior (HU-18).
     */
    VisitaSeguimiento reprogramarVisita(String idSolicitud, String idVisita,
                                        LocalDate fechaPrevista, String idVoluntario);

    /** Cancelación manual con motivo obligatorio. Solo sobre visitas programadas (HU-18). */
    VisitaSeguimiento cancelarVisita(String idSolicitud, String idVisita, String motivo);

    /** Solo el voluntario asignado puede registrar el resultado (HU-19). */
    VisitaSeguimiento registrarResultadoVisita(String idSolicitud, String idVisita,
                                               LocalDate fechaRealizacion, Calificacion calificacion,
                                               String observaciones, String idVoluntarioSesion);
}
