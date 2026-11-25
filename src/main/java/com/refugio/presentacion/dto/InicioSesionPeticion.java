package com.refugio.presentacion.dto;

import jakarta.validation.constraints.NotBlank;

/** POST /api/sesiones (HU-02). */
public record InicioSesionPeticion(@NotBlank String correo, @NotBlank String contrasena) {
}
