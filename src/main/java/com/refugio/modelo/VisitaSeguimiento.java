package com.refugio.modelo;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Visita de seguimiento posterior a una adopción (HU-18, HU-19).
 *
 * <p>Va incrustada dentro de la {@link Adopcion}, que a su vez va dentro de la
 * {@link SolicitudAdopcion}: composición dentro del mismo componente. Por eso
 * lleva un identificador propio generado aquí — MongoDB no asigna uno a los
 * documentos anidados, y las rutas de la API necesitan referenciarla.
 */
public class VisitaSeguimiento {

    private String id = UUID.randomUUID().toString();

    private LocalDate fechaPrevista;

    private LocalDate fechaRealizacion;

    private EstadoVisita estado = EstadoVisita.PROGRAMADA;

    private Calificacion calificacion;

    private String observaciones;

    /** Voluntario asignado. Solo él puede registrar el resultado. */
    private String idVoluntario;

    private String indicaciones;

    private String motivoCancelacion;

    public VisitaSeguimiento() {
    }

    public VisitaSeguimiento(LocalDate fechaPrevista, String idVoluntario, String indicaciones) {
        this.fechaPrevista = fechaPrevista;
        this.idVoluntario = idVoluntario;
        this.indicaciones = indicaciones;
        this.estado = EstadoVisita.PROGRAMADA;
    }

    public void reprogramar(LocalDate nuevaFecha) {
        this.fechaPrevista = nuevaFecha;
    }

    public void cancelar(String motivo) {
        this.estado = EstadoVisita.CANCELADA;
        this.motivoCancelacion = motivo;
    }

    public void registrarResultado(LocalDate fechaRealizacion, Calificacion calificacion,
                                   String observaciones) {
        this.fechaRealizacion = fechaRealizacion;
        this.calificacion = calificacion;
        this.observaciones = observaciones;
        this.estado = EstadoVisita.REALIZADA;
    }

    public boolean estaProgramada() {
        return estado == EstadoVisita.PROGRAMADA;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getFechaPrevista() {
        return fechaPrevista;
    }

    public void setFechaPrevista(LocalDate fechaPrevista) {
        this.fechaPrevista = fechaPrevista;
    }

    public LocalDate getFechaRealizacion() {
        return fechaRealizacion;
    }

    public void setFechaRealizacion(LocalDate fechaRealizacion) {
        this.fechaRealizacion = fechaRealizacion;
    }

    public EstadoVisita getEstado() {
        return estado;
    }

    public void setEstado(EstadoVisita estado) {
        this.estado = estado;
    }

    public Calificacion getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Calificacion calificacion) {
        this.calificacion = calificacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getIdVoluntario() {
        return idVoluntario;
    }

    public void setIdVoluntario(String idVoluntario) {
        this.idVoluntario = idVoluntario;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }
}
