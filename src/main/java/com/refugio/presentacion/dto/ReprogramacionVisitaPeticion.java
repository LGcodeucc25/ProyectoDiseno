package com.refugio.presentacion.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

/**
 * POST /api/solicitudes/{id}/visitas/{idVisita}/reprogramacion (HU-18).
 * El voluntario es opcional: si no viene, se conserva el asignado.
 */
public record ReprogramacionVisitaPeticion(@NotNull LocalDate fechaPrevista, String idVoluntario) {
}
