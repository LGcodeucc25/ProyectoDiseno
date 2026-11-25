package com.refugio.negocio;

import java.time.LocalDate;
import java.util.List;

import com.refugio.modelo.Tratamiento;

/**
 * Contrato interno de Atención Médica, de solo lectura. Lo requiere Consultas
 * y Reportes para la ficha integral.
 */
public interface IHistorialMedico {

    /** Vacunas aplicadas, con el nombre actual del tipo ya resuelto desde el catálogo. */
    List<VacunaAplicada> vacunasDe(String idAnimal);

    List<Tratamiento> tratamientosDe(String idAnimal);

    boolean tieneTratamientoActivo(String idAnimal);

    /**
     * Una aplicación de vacuna tal como la ve quien no es dueño del catálogo.
     * El nombre es el actual del tipo: si se renombra, la ficha muestra el nuevo.
     */
    record VacunaAplicada(String id, String idTipoVacuna, String nombreVacuna,
                          LocalDate fechaAplicacion, String idVeterinario) {
    }
}
