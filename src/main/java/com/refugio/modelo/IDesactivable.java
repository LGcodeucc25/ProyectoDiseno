package com.refugio.modelo;

/**
 * Contrato de las entidades que se desactivan en lugar de eliminarse.
 *
 * <p>Lo implementan {@link EspacioAlojamiento} y {@link TipoVacuna}: no tienen
 * relación entre sí, pero siguen la misma regla de negocio — se desactivan para
 * no perder la información histórica que las referencia (HU-06, HU-09).
 */
public interface IDesactivable {

    void desactivar();

    void reactivar();

    boolean estaActivo();
}
