package com.refugio.negocio;

import com.refugio.modelo.EspacioAlojamiento;

/**
 * Fachada pública de Gestión de Alojamiento (HU-06).
 * La consume {@code EspacioAlojamientoController}.
 */
public interface IAdministracionEspacios {

    EspacioAlojamiento crearEspacio(String nombre, String ubicacion, int capacidad);

    /** La nueva capacidad no puede quedar por debajo de la ocupación actual. */
    EspacioAlojamiento actualizarCapacidad(String idEspacio, int capacidad);

    /** Solo se desactiva un espacio vacío. Nunca se borra (IDesactivable). */
    EspacioAlojamiento desactivarEspacio(String idEspacio);

    EspacioAlojamiento reactivarEspacio(String idEspacio);

    Ocupacion consultarOcupacion(String idEspacio);

    /** El espacio con su ocupación de este momento. */
    record Ocupacion(EspacioAlojamiento espacio, long ocupados, long libres) {
    }
}
