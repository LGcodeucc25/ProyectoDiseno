package com.refugio.negocio;

import com.refugio.modelo.EspacioAlojamiento;

/**
 * Contrato interno de Gestión de Alojamiento. Lo requiere Gestión de Animales.
 *
 * <p>Solo lectura. "Ocupar" un espacio es fijar {@code Animal.idEspacio}, y ese
 * dato pertenece a Gestión de Animales: este contrato le permite comprobar el
 * cupo antes de alojar y resolver el espacio para la ficha, pero la asignación
 * la hace el propio Gestión de Animales. No hay {@code ocupar} ni {@code liberar}
 * porque un método que valida y no guarda nada prometería algo que no cumple.
 */
public interface IEspacios {

    /** Verdadero si el espacio está activo y su ocupación no llegó a la capacidad. */
    boolean tieneCupo(String idEspacio);

    long ocupacionActual(String idEspacio);

    EspacioAlojamiento obtenerEspacio(String idEspacio);
}
