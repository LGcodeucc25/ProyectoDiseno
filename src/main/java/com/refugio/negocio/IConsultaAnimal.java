package com.refugio.negocio;

import java.util.List;

import com.refugio.modelo.Animal;
import com.refugio.modelo.Especie;
import com.refugio.modelo.EstadoAnimal;
import com.refugio.modelo.Sexo;

/**
 * Contrato interno de Gestión de Animales, de solo lectura.
 *
 * <p>Lo requieren Atención Médica (no vacuna a un animal No disponible),
 * Gestión de Adopciones (no acepta solicitudes sobre uno que no esté
 * Disponible) y Consultas y Reportes (arma la ficha y la búsqueda). Está
 * separado de {@link ITransicionEstado} para que ninguno de esos clientes,
 * salvo Adopciones, pueda cambiar el estado.
 */
public interface IConsultaAnimal {

    Animal obtenerAnimal(String idAnimal);

    EstadoAnimal estadoActual(String idAnimal);

    boolean estaDisponible(String idAnimal);

    List<Animal> listar(Filtro filtro);

    /**
     * Dónde está el animal, ya resuelto: "Caniles A — Perros medianos", o null
     * si no tiene alojamiento. Gestión de Animales es responsable de la
     * ubicación y la resuelve con {@link IEspacios}; así Consultas no necesita
     * una conexión propia con Gestión de Alojamiento.
     */
    String ubicacionDe(String idAnimal);

    /**
     * Criterios de búsqueda (HU-07). Todos opcionales, combinados con Y.
     * El estado es el actual, no cualquiera del historial.
     */
    record Filtro(Especie especie, String raza, Sexo sexo, String edad, EstadoAnimal estado,
                  String idVoluntario) {
    }
}
