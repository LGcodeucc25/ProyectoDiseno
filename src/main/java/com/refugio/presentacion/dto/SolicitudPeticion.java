package com.refugio.presentacion.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** POST /api/solicitudes (HU-13). */
public record SolicitudPeticion(@NotBlank String idAnimal, @NotBlank String idAdoptante,
                                @NotNull LocalDate fechaSolicitud) {
}
