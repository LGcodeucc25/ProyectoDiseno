package com.refugio.presentacion.dto;

import com.refugio.modelo.Rol;
import com.refugio.modelo.Usuario;

/** Un usuario tal como sale de la API: sin el hash de la contraseña. */
public record UsuarioRespuesta(String id, String identificacion, String nombre, String correo,
                               String telefono, Rol rol, boolean activo) {

    public static UsuarioRespuesta de(Usuario u) {
        return new UsuarioRespuesta(u.getId(), u.getIdentificacion(), u.getNombre(), u.getCorreo(),
                u.getTelefono(), u.getRol(), u.isActivo());
    }
}
