package com.refugio.presentacion.dto;

import java.time.LocalDate;

import com.refugio.modelo.ResultadoEvaluacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** POST /api/solicitudes/{id}/evaluacion (HU-14). Observaciones obligatorias en ambos resultados. */
public record EvaluacionPeticion(@NotNull ResultadoEvaluacion resultado, @NotNull LocalDate fecha,
                                 @NotBlank String observaciones) {
}
