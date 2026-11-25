package com.refugio.presentacion.dto;

import com.refugio.modelo.Adoptante;
import com.refugio.modelo.TipoVivienda;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * POST /api/adoptantes (HU-12). El correo es opcional, pero si viene debe ser
 * válido. {@code notasEntrevista} recoge la descripción de la experiencia previa.
 */
public record AdoptantePeticion(
        @NotBlank String identificacion,
        @NotBlank String nombre,
        @Email String correo,
        @NotBlank String telefono,
        @NotBlank String direccion,
        @NotNull TipoVivienda tipoVivienda,
        @NotNull Boolean experienciaPrevia,
        String notasEntrevista) {

    public Adoptante aAdoptante() {
        Adoptante a = new Adoptante(identificacion.trim(), nombre.trim(),
                correo == null || correo.isBlank() ? null : correo.trim(), telefono.trim(),
                direccion.trim(), tipoVivienda, experienciaPrevia);
        a.setNotasEntrevista(notasEntrevista);
        return a;
    }
}
