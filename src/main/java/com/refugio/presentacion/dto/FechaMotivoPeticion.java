package com.refugio.presentacion.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo de las acciones que solo piden fecha y motivo obligatorio:
 * cancelación de una solicitud (HU-15) y devolución (HU-20).
 */
public record FechaMotivoPeticion(@NotNull LocalDate fecha, @NotBlank String motivo) {
}
