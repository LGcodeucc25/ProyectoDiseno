package com.refugio.presentacion.dto;

import com.refugio.modelo.Adoptante;
import com.refugio.modelo.TipoVivienda;

/** Un adoptante sin la copia indexada de la identificación, que es un detalle de persistencia. */
public record AdoptanteRespuesta(String id, String identificacion, String nombre, String correo,
                                 String telefono, String direccion, TipoVivienda tipoVivienda,
                                 boolean experienciaPrevia, String notasEntrevista) {

    public static AdoptanteRespuesta de(Adoptante a) {
        return new AdoptanteRespuesta(a.getId(), a.getIdentificacion(), a.getNombre(),
                a.getCorreo(), a.getTelefono(), a.getDireccion(), a.getTipoVivienda(),
                a.isExperienciaPrevia(), a.getNotasEntrevista());
    }
}
