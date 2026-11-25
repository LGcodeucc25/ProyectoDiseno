package com.refugio.presentacion.dto;

import com.refugio.modelo.Rol;
import com.refugio.negocio.IAccesoUsuarios.Sesion;

/** Respuesta del inicio de sesión: el token y el nombre y rol de quien entra (HU-02, criterio 1). */
public record SesionRespuesta(String token, String tipo, String idUsuario, String nombre, Rol rol) {

    public static SesionRespuesta de(Sesion sesion) {
        return new SesionRespuesta(sesion.token(), "Bearer", sesion.usuario().getId(),
                sesion.usuario().getNombre(), sesion.usuario().getRol());
    }
}
