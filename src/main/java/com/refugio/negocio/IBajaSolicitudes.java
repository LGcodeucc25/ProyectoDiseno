package com.refugio.negocio;

import java.time.LocalDate;

/**
 * Contrato interno de Gestión de Adopciones. Su único consumidor es Gestión
 * de Animales.
 *
 * <p>Cuando un animal deja de estar Disponible por un cambio manual (HU-04),
 * sus solicitudes pendientes se rechazan en ese mismo momento.
 */
public interface IBajaSolicitudes {

    /** Rechaza las pendientes con motivo "Animal no disponible". Devuelve cuántas rechazó. */
    int rechazarPendientesDe(String idAnimal, LocalDate fecha);
}
