package com.refugio.modelo;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Tratamiento médico de un animal (HU-11).
 *
 * <p>Un tratamiento sin fecha de finalización está activo. No existe un estado
 * «En tratamiento» del animal: tenerlo crearía dos fuentes de verdad que pueden
 * contradecirse, así que se deduce de aquí.
 */
@Document(collection = "tratamientos")
public class Tratamiento {

    @Id
    private String id;

    @Indexed
    private String idAnimal;

    private String idVeterinario;

    private String diagnostico;

    private LocalDate fechaInicio;

    /** Null mientras el tratamiento siga en curso. */
    private LocalDate fechaFin;

    private String indicaciones;

    public Tratamiento() {
    }

    public Tratamiento(String idAnimal, String idVeterinario, String diagnostico,
                       LocalDate fechaInicio, String indicaciones) {
        this.idAnimal = idAnimal;
        this.idVeterinario = idVeterinario;
        this.diagnostico = diagnostico;
        this.fechaInicio = fechaInicio;
        this.indicaciones = indicaciones;
    }

    public void finalizar(LocalDate fecha) {
        this.fechaFin = fecha;
    }

    public boolean estaActivo() {
        return fechaFin == null;
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

    public String getIdVeterinario() {
        return idVeterinario;
    }

    public void setIdVeterinario(String idVeterinario) {
        this.idVeterinario = idVeterinario;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }
}
