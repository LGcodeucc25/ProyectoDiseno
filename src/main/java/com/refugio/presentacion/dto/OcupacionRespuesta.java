package com.refugio.presentacion.dto;

import com.refugio.negocio.IAdministracionEspacios.Ocupacion;

/** GET /api/espacios/{id}/ocupacion (HU-06). {@code sinCupo} es el indicador del criterio 10. */
public record OcupacionRespuesta(String id, String nombre, String ubicacion, int capacidad,
                                 long ocupacion, long cuposLibres, boolean activo, boolean sinCupo) {

    public static OcupacionRespuesta de(Ocupacion o) {
        return new OcupacionRespuesta(o.espacio().getId(), o.espacio().getNombre(),
                o.espacio().getUbicacion(), o.espacio().getCapacidad(), o.ocupados(), o.libres(),
                o.espacio().estaActivo(), o.libres() == 0);
    }
}
