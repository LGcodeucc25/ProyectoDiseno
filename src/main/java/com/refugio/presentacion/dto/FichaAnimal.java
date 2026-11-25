package com.refugio.presentacion.dto;

import java.time.LocalDate;
import java.util.List;

import com.refugio.modelo.Adopcion;
import com.refugio.modelo.Especie;
import com.refugio.modelo.EstadoAnimal;
import com.refugio.modelo.OrigenIngreso;
import com.refugio.modelo.RegistroEstado;
import com.refugio.modelo.Sexo;
import com.refugio.modelo.SolicitudAdopcion;
import com.refugio.modelo.Tratamiento;
import com.refugio.modelo.VisitaSeguimiento;
import com.refugio.negocio.IHistorialMedico.VacunaAplicada;

/**
 * Ficha integral de un animal (HU-08).
 *
 * <p>No es una entidad: no tiene identidad ni ciclo de vida propios. La arma
 * {@code ConsultaService} con lo que le dan tres componentes —Gestión de
 * Animales (datos, estado y ubicación ya resuelta), Atención Médica e
 * historial, y Gestión de Adopciones—, cada uno por su contrato.
 */
public record FichaAnimal(
        String id,
        String nombre,
        Especie especie,
        String raza,
        Sexo sexo,
        String edadAproximada,
        LocalDate fechaIngreso,
        OrigenIngreso origenIngreso,
        String descripcion,
        EstadoAnimal estadoActual,
        List<RegistroEstado> historialEstados,
        String idEspacio,
        String alojamiento,
        String idVoluntario,
        List<VacunaAplicada> vacunas,
        List<Tratamiento> tratamientos,
        boolean tieneTratamientoActivo,
        List<SolicitudAdopcion> solicitudes,
        Adopcion adopcionActual,
        List<VisitaSeguimiento> visitas) {
}
