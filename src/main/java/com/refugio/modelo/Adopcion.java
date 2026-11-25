package com.refugio.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entrega efectiva de un animal a un adoptante (HU-17, HU-20).
 *
 * <p>Va incrustada en la {@link SolicitudAdopcion} que la originó: la
 * composición no cruza la frontera del componente, nadie la lee por separado
 * y se borra con la solicitud.
 *
 * <p>Una devolución no borra la adopción: le pone fecha y motivo. El historial
 * de lo que pasó se conserva.
 */
public class Adopcion {

    private LocalDate fechaEntrega;

    private LocalDate fechaDevolucion;

    private String motivoDevolucion;

    /** Composición: las visitas no existen sin la adopción que las origina. */
    private List<VisitaSeguimiento> visitas = new ArrayList<>();

    public Adopcion() {
    }

    public Adopcion(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public void registrarDevolucion(LocalDate fecha, String motivo) {
        this.fechaDevolucion = fecha;
        this.motivoDevolucion = motivo;
        cancelarVisitasProgramadas("Animal devuelto al refugio");
    }

    public boolean fueDevuelta() {
        return fechaDevolucion != null;
    }

    public void programarVisita(VisitaSeguimiento visita) {
        if (visitas == null) {
            visitas = new ArrayList<>();
        }
        visitas.add(visita);
    }

    public VisitaSeguimiento buscarVisita(String idVisita) {
        if (visitas == null) {
            return null;
        }
        return visitas.stream()
                .filter(v -> v.getId().equals(idVisita))
                .findFirst()
                .orElse(null);
    }

    /** Al devolver el animal, las visitas que seguían pendientes quedan sin efecto. */
    public void cancelarVisitasProgramadas(String motivo) {
        if (visitas == null) {
            return;
        }
        visitas.stream().filter(VisitaSeguimiento::estaProgramada)
                .forEach(v -> v.cancelar(motivo));
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public String getMotivoDevolucion() {
        return motivoDevolucion;
    }

    public void setMotivoDevolucion(String motivoDevolucion) {
        this.motivoDevolucion = motivoDevolucion;
    }

    public List<VisitaSeguimiento> getVisitas() {
        return visitas;
    }

    public void setVisitas(List<VisitaSeguimiento> visitas) {
        this.visitas = visitas;
    }
}
