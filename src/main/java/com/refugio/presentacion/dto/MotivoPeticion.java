package com.refugio.presentacion.dto;

import jakarta.validation.constraints.NotBlank;

/** POST /api/solicitudes/{id}/visitas/{idVisita}/cancelacion (HU-18). El motivo es obligatorio. */
public record MotivoPeticion(@NotBlank String motivo) {
}
