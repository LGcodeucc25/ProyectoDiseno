package com.refugio.negocio;

import java.time.LocalDate;

import com.refugio.modelo.Animal;
import com.refugio.modelo.EstadoAnimal;

/**
 * Fachada pública de Gestión de Animales (HU-03, HU-04, HU-05, HU-21).
 * La consume {@code AnimalController}.
 *
 * <p>{@code idUsuario} es siempre el de la sesión: queda en el historial como
 * responsable del cambio.
 */
public interface IGestionAnimales {

    /** Registra el ingreso. Todo animal entra En valoración. */
    Animal registrarAnimal(Animal datos, String idUsuario);

    /** Corrige los datos del ingreso. No toca el estado, ni el espacio, ni el responsable. */
    Animal actualizarAnimal(String idAnimal, Animal datos);

    /** Cambio de estado manual (HU-04). Los automáticos van por {@link ITransicionEstado}. */
    Animal cambiarEstado(String idAnimal, EstadoAnimal nuevo, LocalDate fecha, String motivo,
                         String idUsuario);

    Animal asignarAlojamiento(String idAnimal, String idEspacio, LocalDate fecha);

    Animal asignarVoluntario(String idAnimal, String idVoluntario, LocalDate fecha);
}
