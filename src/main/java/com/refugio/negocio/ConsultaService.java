package com.refugio.negocio;

import java.util.List;

import org.springframework.stereotype.Service;

import com.refugio.modelo.Animal;
import com.refugio.modelo.EstadoSolicitud;
import com.refugio.modelo.SolicitudAdopcion;
import com.refugio.presentacion.dto.FichaAnimal;

/**
 * Consultas y Reportes. Proporciona la fachada {@link IConsultas}.
 *
 * <p>No tiene repositorio propio: pide cada parte a su dueño por los tres
 * contratos de la vista funcional (conexiones 1, 2 y 3). La ubicación llega ya
 * resuelta por Gestión de Animales, así que no hace falta una cuarta conexión
 * con Gestión de Alojamiento.
 */
@Service
public class ConsultaService implements IConsultas {

    private final IConsultaAnimal consultaAnimal;
    private final IHistorialMedico historialMedico;
    private final IConsultaAdopciones consultaAdopciones;

    public ConsultaService(IConsultaAnimal consultaAnimal, IHistorialMedico historialMedico,
                           IConsultaAdopciones consultaAdopciones) {
        this.consultaAnimal = consultaAnimal;
        this.historialMedico = historialMedico;
        this.consultaAdopciones = consultaAdopciones;
    }

    @Override
    public List<Animal> buscarAnimales(IConsultaAnimal.Filtro filtro) {
        return consultaAnimal.listar(filtro);
    }

    @Override
    public FichaAnimal fichaDe(String idAnimal) {
        Animal animal = consultaAnimal.obtenerAnimal(idAnimal);
        return new FichaAnimal(
                animal.getId(),
                animal.getNombre(),
                animal.getEspecie(),
                animal.getRaza(),
                animal.getSexo(),
                animal.getEdadAproximada(),
                animal.getFechaIngreso(),
                animal.getOrigenIngreso(),
                animal.getDescripcion(),
                animal.estadoActual(),
                animal.getHistorialEstados(),
                animal.getIdEspacio(),
                consultaAnimal.ubicacionDe(idAnimal),
                animal.getIdVoluntario(),
                historialMedico.vacunasDe(idAnimal),
                historialMedico.tratamientosDe(idAnimal),
                historialMedico.tieneTratamientoActivo(idAnimal),
                consultaAdopciones.solicitudesDe(idAnimal, null, null),
                consultaAdopciones.adopcionDe(idAnimal).orElse(null),
                consultaAdopciones.visitasDe(idAnimal));
    }

    @Override
    public List<SolicitudAdopcion> consultarSolicitudes(String idAnimal, String idAdoptante,
                                                        EstadoSolicitud estado) {
        return consultaAdopciones.solicitudesDe(idAnimal, idAdoptante, estado);
    }

    @Override
    public List<IConsultaAdopciones.VisitaAsignada> visitasAsignadasA(String idVoluntario) {
        return consultaAdopciones.visitasAsignadasA(idVoluntario);
    }
}
