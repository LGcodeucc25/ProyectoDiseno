package com.refugio.presentacion.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** POST /api/animales/{id}/vacunaciones (HU-10). El veterinario sale de la sesión. */
public record VacunacionPeticion(@NotBlank String idTipoVacuna, @NotNull LocalDate fechaAplicacion) {
}
