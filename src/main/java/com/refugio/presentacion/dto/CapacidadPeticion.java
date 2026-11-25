package com.refugio.presentacion.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** PATCH /api/espacios/{id} (HU-06). */
public record CapacidadPeticion(@NotNull @Positive Integer capacidad) {
}
