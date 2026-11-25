package com.refugio.modelo;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Solicitud de adopción de un animal por parte de un adoptante (HU-13 a HU-17, HU-20).
 *
 * <p>Raíz del documento: incrusta la {@link Adopcion} y, dentro de ella, las
 * visitas de seguimiento. Evaluación y devolución son atributos y no clases
 * aparte: tienen multiplicidad 0..1 y tres atributos o menos, y no aportaban
 * comportamiento propio.
 *
 * <p>Una solicitud aprobada no vuelve a Pendiente. El refugio no revierte su
 * propia decisión: solo el adoptante puede cancelarla.
 */
@Document(collection = "solicitudes")
public class SolicitudAdopcion {

    @Id
    private String id;

    @Indexed
    private String idAnimal;

    @Indexed
    private String idAdoptante;

    private LocalDate fechaSolicitud;

    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    // --- evaluación (HU-14), absorbida como atributos ---
    private LocalDate fechaEvaluacion;
    private ResultadoEvaluacion resultado;
    private String observaciones;
    /** Coordinador que evaluó. Null mientras la solicitud siga pendiente. */
    private String idEvaluador;

    // --- cancelación (HU-15) ---
    private LocalDate fechaCancelacion;
    private String motivoCancelacion;

    /** Composición 0..1: solo las solicitudes aprobadas y entregadas la tienen. */
    private Adopcion adopcion;

    public SolicitudAdopcion() {
    }

    public SolicitudAdopcion(String idAnimal, String idAdoptante, LocalDate fechaSolicitud) {
        this.idAnimal = idAnimal;
        this.idAdoptante = idAdoptante;
        this.fechaSolicitud = fechaSolicitud;
        this.estado = EstadoSolicitud.PENDIENTE;
    }

    public void evaluar(ResultadoEvaluacion resultado, LocalDate fecha, String observaciones,
                        String idEvaluador) {
        this.resultado = resultado;
        this.fechaEvaluacion = fecha;
        this.observaciones = observaciones;
        this.idEvaluador = idEvaluador;
        this.estado = (resultado == ResultadoEvaluacion.APROBADA)
                ? EstadoSolicitud.APROBADA
                : EstadoSolicitud.RECHAZADA;
    }

    /**
     * Rechazo automático al dejar el animal de estar disponible (HU-04) o al
     * aprobarse otra solicitud sobre el mismo animal (HU-14).
     */
    public void rechazarAutomaticamente(LocalDate fecha, String motivo) {
        this.estado = EstadoSolicitud.RECHAZADA;
        this.resultado = ResultadoEvaluacion.RECHAZADA;
        this.fechaEvaluacion = fecha;
        this.observaciones = motivo;
        this.idEvaluador = null;
    }

    public void cancelar(LocalDate fecha, String motivo) {
        this.estado = EstadoSolicitud.CANCELADA;
        this.fechaCancelacion = fecha;
        this.motivoCancelacion = motivo;
    }

    public void registrarAdopcion(Adopcion adopcion) {
        this.adopcion = adopcion;
    }

    public boolean estaPendiente() {
        return estado == EstadoSolicitud.PENDIENTE;
    }

    public boolean estaAprobada() {
        return estado == EstadoSolicitud.APROBADA;
    }

    public boolean tieneAdopcion() {
        return adopcion != null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdAnimal() {
        return idAnimal;
    }

    public void setIdAnimal(String idAnimal) {
        this.idAnimal = idAnimal;
    }

    public String getIdAdoptante() {
        return idAdoptante;
    }

    public void setIdAdoptante(String idAdoptante) {
        this.idAdoptante = idAdoptante;
    }

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public LocalDate getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public void setFechaEvaluacion(LocalDate fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }

    public ResultadoEvaluacion getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoEvaluacion resultado) {
        this.resultado = resultado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getIdEvaluador() {
        return idEvaluador;
    }

    public void setIdEvaluador(String idEvaluador) {
        this.idEvaluador = idEvaluador;
    }

    public LocalDate getFechaCancelacion() {
        return fechaCancelacion;
    }

    public void setFechaCancelacion(LocalDate fechaCancelacion) {
        this.fechaCancelacion = fechaCancelacion;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }

    public Adopcion getAdopcion() {
        return adopcion;
    }

    public void setAdopcion(Adopcion adopcion) {
        this.adopcion = adopcion;
    }
}
