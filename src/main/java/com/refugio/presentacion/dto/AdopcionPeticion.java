package com.refugio.presentacion.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

/** POST /api/solicitudes/{id}/adopcion (HU-17). */
public record AdopcionPeticion(@NotNull LocalDate fechaEntrega) {
}
