package com.refugio.negocio;

import java.time.LocalDate;

import com.refugio.modelo.TipoVacuna;
import com.refugio.modelo.Tratamiento;
import com.refugio.modelo.Vacunacion;

/**
 * Fachada pública de Atención Médica (HU-09, HU-10, HU-11).
 * La consume {@code AtencionMedicaController}.
 *
 * <p>{@code idVeterinario} es siempre el de la sesión: el responsable no se elige.
 */
public interface IAtencionMedica {

    TipoVacuna crearTipoVacuna(String nombre, String descripcion);

    /** Nunca se borra: se desactiva y deja de ofrecerse al vacunar (IDesactivable). */
    TipoVacuna desactivarTipoVacuna(String idTipoVacuna);

    Vacunacion registrarVacunacion(String idAnimal, String idTipoVacuna, LocalDate fechaAplicacion,
                                   String idVeterinario);

    Tratamiento registrarTratamiento(String idAnimal, String diagnostico, LocalDate fechaInicio,
                                     LocalDate fechaFin, String indicaciones, String idVeterinario);

    Tratamiento finalizarTratamiento(String idTratamiento, LocalDate fechaFin);
}
