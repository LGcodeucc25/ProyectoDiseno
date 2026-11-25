package com.refugio.presentacion.dto;

import java.time.LocalDate;

import com.refugio.modelo.EstadoAnimal;

import jakarta.validation.constraints.NotNull;

/** POST /api/animales/{id}/estados (HU-04). El motivo es obligatorio solo para No disponible. */
public record CambioEstadoPeticion(@NotNull EstadoAnimal estado, @NotNull LocalDate fecha,
                                   String motivo) {
}
