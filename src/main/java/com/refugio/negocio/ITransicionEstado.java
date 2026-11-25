package com.refugio.negocio;

import java.time.LocalDate;

/**
 * Contrato interno de Gestión de Animales para los cambios de estado
 * automáticos. Su único consumidor es Gestión de Adopciones.
 *
 * <p>Cada operación verifica contra {@code EstadoAnimal.permiteAutomatica} que
 * el cambio exista; si no, lanza {@code TransicionNoPermitidaException}.
 */
public interface ITransicionEstado {

    /** Disponible → En proceso de adopción, al aprobar una solicitud (HU-14). */
    void marcarEnProcesoAdopcion(String idAnimal, LocalDate fecha, String motivo);

    /** En proceso → Adoptado, al registrar la adopción (HU-17). Libera el espacio. */
    void marcarAdoptado(String idAnimal, LocalDate fecha, String motivo);

    /** Adoptado → En valoración, al registrar una devolución (HU-20). Queda sin espacio. */
    void marcarEnValoracion(String idAnimal, LocalDate fecha, String motivo);

    /** En proceso → Disponible, solo si el adoptante cancela la solicitud aprobada (HU-15). */
    void marcarDisponible(String idAnimal, LocalDate fecha, String motivo);
}
