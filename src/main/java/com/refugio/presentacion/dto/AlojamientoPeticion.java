package com.refugio.presentacion.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** POST /api/animales/{id}/alojamiento (HU-05). */
public record AlojamientoPeticion(@NotBlank String idEspacio, @NotNull LocalDate fecha) {
}
