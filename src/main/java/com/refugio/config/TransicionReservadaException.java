package com.refugio.config;

/**
 * El cambio de estado existe, pero solo lo puede disparar el proceso de
 * adopción (HU-04, criterio 2): aprobar, entregar, cancelar o devolver.
 */
public class TransicionReservadaException extends ReglaDeNegocioException {

    public TransicionReservadaException() {
        super("TRANSICION_RESERVADA_AL_FLUJO_DE_ADOPCION",
                "Este cambio de estado se realiza desde el proceso de adopción");
    }
}
