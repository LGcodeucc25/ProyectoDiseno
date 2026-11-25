package com.refugio.presentacion.dto;

import com.refugio.modelo.Rol;
import com.refugio.modelo.Usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** POST /api/usuarios (HU-01). Todos los campos son obligatorios salvo el teléfono. */
public record RegistroUsuarioPeticion(
        @NotBlank String identificacion,
        @NotBlank String nombre,
        @NotBlank @Email String correo,
        String telefono,
        @NotNull Rol rol,
        @NotBlank String contrasena) {

    public Usuario aUsuario() {
        // La contraseña viaja aparte: el service la convierte en hash antes de guardarla.
        return new Usuario(identificacion.trim(), nombre.trim(), correo, telefono, null, rol);
    }
}
