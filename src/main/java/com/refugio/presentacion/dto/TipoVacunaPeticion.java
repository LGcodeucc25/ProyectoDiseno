package com.refugio.presentacion.dto;

import jakarta.validation.constraints.NotBlank;

/** POST /api/tipos-vacuna y PUT /api/tipos-vacuna/{id} (HU-09). La descripción es opcional. */
public record TipoVacunaPeticion(@NotBlank String nombre, String descripcion) {
}
