package com.refugio.datos;

/**
 * Fragmento de {@link EspacioAlojamientoRepository}: cuántos animales hay hoy
 * en un espacio.
 *
 * <p>La única fuente de verdad de dónde está un animal es {@code Animal.idEspacio}.
 * La ocupación se cuenta sobre ese dato en lugar de guardarse en el espacio,
 * para no tener dos fuentes que puedan desincronizarse. El precio asumido es
 * que la capa de datos de Gestión de Alojamiento lee la colección
 * {@code animales}: un acoplamiento de solo lectura, en esta capa y en ninguna otra.
 */
public interface ConteoOcupacion {

    long contarAnimalesEn(String idEspacio);
}
