package com.refugio.config;

/**
 * El cambio de estado no existe en la tabla de transiciones de
 * {@link com.refugio.modelo.EstadoAnimal}: ni manual ni automático (HU-04,
 * criterio 3). Se distingue de {@link TransicionReservadaException} porque el
 * usuario necesita saber si el cambio es imposible o si debe hacerlo desde
 * otro flujo.
 */
public class TransicionNoPermitidaException extends ReglaDeNegocioException {

    public TransicionNoPermitidaException() {
        super("TRANSICION_NO_PERMITIDA", "Transición de estado no permitida");
    }
}
