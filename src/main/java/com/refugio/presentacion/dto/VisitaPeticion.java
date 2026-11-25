package com.refugio.presentacion.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** POST /api/solicitudes/{id}/visitas (HU-18). */
public record VisitaPeticion(@NotNull LocalDate fechaPrevista, @NotBlank String idVoluntario,
                             String indicaciones) {
}
