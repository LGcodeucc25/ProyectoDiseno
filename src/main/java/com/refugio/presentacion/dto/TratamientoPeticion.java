package com.refugio.presentacion.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** POST /api/animales/{id}/tratamientos (HU-11). La fecha de fin puede completarse después. */
public record TratamientoPeticion(@NotBlank String diagnostico, @NotNull LocalDate fechaInicio,
                                  LocalDate fechaFin, String indicaciones) {
}
