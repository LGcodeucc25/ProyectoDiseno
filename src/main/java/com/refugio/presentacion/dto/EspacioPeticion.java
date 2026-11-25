package com.refugio.presentacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** POST /api/espacios (HU-06). */
public record EspacioPeticion(@NotBlank String nombre, String ubicacion,
                              @NotNull @Positive Integer capacidad) {
}
