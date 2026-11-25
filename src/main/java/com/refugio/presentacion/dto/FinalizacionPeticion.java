package com.refugio.presentacion.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

/** POST /api/tratamientos/{id}/finalizacion (HU-11). */
public record FinalizacionPeticion(@NotNull LocalDate fechaFin) {
}
