package com.refugio.presentacion.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** POST /api/animales/{id}/responsable (HU-21). */
public record ResponsablePeticion(@NotBlank String idVoluntario, @NotNull LocalDate fecha) {
}
