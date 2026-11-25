package com.refugio.presentacion.dto;

import java.time.LocalDate;

import com.refugio.modelo.Calificacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** POST /api/solicitudes/{id}/visitas/{idVisita}/resultado (HU-19). */
public record ResultadoVisitaPeticion(@NotNull LocalDate fechaRealizacion,
                                      @NotNull Calificacion calificacion,
                                      @NotBlank String observaciones) {
}
