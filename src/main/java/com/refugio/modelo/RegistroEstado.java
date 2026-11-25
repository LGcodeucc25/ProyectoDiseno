package com.refugio.modelo;

import java.time.LocalDate;

/**
 * Un cambio de estado de un animal, con su fecha, su motivo y su responsable.
 *
 * <p>Va incrustado dentro del documento del animal: pertenece al mismo
 * componente, nadie lo lee por separado y se borra con él. Es la pieza que
 * convierte el estado en un historial en lugar de un atributo (HU-04).
 */
public class RegistroEstado {

    private EstadoAnimal estado;
    private LocalDate fecha;
    private String motivo;
    /** Usuario que registró el cambio. Nulo cuando lo disparó el propio sistema. */
    private String idUsuario;
    private boolean automatico;

    public RegistroEstado() {
    }

    public RegistroEstado(EstadoAnimal estado, LocalDate fecha, String motivo,
                          String idUsuario, boolean automatico) {
        this.estado = estado;
        this.fecha = fecha;
        this.motivo = motivo;
        this.idUsuario = idUsuario;
        this.automatico = automatico;
    }

    public EstadoAnimal getEstado() {
        return estado;
    }

    public void setEstado(EstadoAnimal estado) {
        this.estado = estado;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public boolean isAutomatico() {
        return automatico;
    }

    public void setAutomatico(boolean automatico) {
        this.automatico = automatico;
    }
}
