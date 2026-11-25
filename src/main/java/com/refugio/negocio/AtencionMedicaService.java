package com.refugio.negocio;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.refugio.datos.TipoVacunaRepository;
import com.refugio.datos.TratamientoRepository;
import com.refugio.datos.VacunacionRepository;
import com.refugio.modelo.Animal;
import com.refugio.modelo.EstadoAnimal;
import com.refugio.modelo.TipoVacuna;
import com.refugio.modelo.Tratamiento;
import com.refugio.modelo.Vacunacion;

/**
 * Atención Médica. Proporciona la fachada {@link IAtencionMedica} y el
 * contrato interno {@link IHistorialMedico}. Requiere {@link IConsultaAnimal}
 * para verificar que el animal existe y no está No disponible.
 */
@Service
public class AtencionMedicaService implements IAtencionMedica, IHistorialMedico {

    private final TipoVacunaRepository tipos;
    private final VacunacionRepository vacunaciones;
    private final TratamientoRepository tratamientos;
    private final IConsultaAnimal consultaAnimal;

    public AtencionMedicaService(TipoVacunaRepository tipos, VacunacionRepository vacunaciones,
                                 TratamientoRepository tratamientos, IConsultaAnimal consultaAnimal) {
        this.tipos = tipos;
        this.vacunaciones = vacunaciones;
        this.tratamientos = tratamientos;
        this.consultaAnimal = consultaAnimal;
    }

    // ------------------------------------------------------ IAtencionMedica

    @Override
    public TipoVacuna crearTipoVacuna(String nombre, String descripcion) {
        if (tipos.existsByNombreIgnoreCase(nombre.trim())) {
            throw new ReglaDeNegocioException("Ya existe un tipo de vacuna con ese nombre");
        }
        return tipos.save(new TipoVacuna(nombre.trim(), descripcion));
    }

    @Override
    public TipoVacuna actualizarTipoVacuna(String idTipoVacuna, String nombre, String descripcion) {
        TipoVacuna tipo = obtenerTipo(idTipoVacuna);
        if (tipos.existsByNombreIgnoreCaseAndIdNot(nombre.trim(), idTipoVacuna)) {
            throw new ReglaDeNegocioException("Ya existe un tipo de vacuna con ese nombre");
        }
        tipo.setNombre(nombre.trim());
        tipo.setDescripcion(descripcion);
        return tipos.save(tipo);
    }

    @Override
    public TipoVacuna desactivarTipoVacuna(String idTipoVacuna) {
        TipoVacuna tipo = obtenerTipo(idTipoVacuna);
        tipo.desactivar();
        return tipos.save(tipo);
    }

    @Override
    public TipoVacuna reactivarTipoVacuna(String idTipoVacuna) {
        TipoVacuna tipo = obtenerTipo(idTipoVacuna);
        tipo.reactivar();
        return tipos.save(tipo);
    }

    @Override
    public Vacunacion registrarVacunacion(String idAnimal, String idTipoVacuna,
                                          LocalDate fechaAplicacion, String idVeterinario) {
        Validaciones.noFutura(fechaAplicacion, "fechaAplicacion",
                "La fecha de aplicación no puede ser futura");
        Animal animal = consultaAnimal.obtenerAnimal(idAnimal);
        if (animal.estadoActual() == EstadoAnimal.NO_DISPONIBLE) {
            throw new ReglaDeNegocioException(
                    "No se pueden registrar vacunas a un animal no disponible");
        }
        TipoVacuna tipo = obtenerTipo(idTipoVacuna);
        if (!tipo.estaActivo()) {
            throw new ReglaDeNegocioException("El tipo de vacuna está inactivo");
        }
        return vacunaciones.save(
                new Vacunacion(idAnimal, idTipoVacuna, idVeterinario, fechaAplicacion));
    }

    @Override
    public Tratamiento registrarTratamiento(String idAnimal, String diagnostico,
                                           LocalDate fechaInicio, LocalDate fechaFin,
                                           String indicaciones, String idVeterinario) {
        Validaciones.noFutura(fechaInicio, "fechaInicio", "La fecha de inicio no puede ser futura");
        Validaciones.noAnterior(fechaFin, fechaInicio, "fechaFin",
                "La fecha de finalización no puede ser anterior a la fecha de inicio");
        consultaAnimal.obtenerAnimal(idAnimal);
        Tratamiento tratamiento = new Tratamiento(idAnimal, idVeterinario, diagnostico,
                fechaInicio, indicaciones);
        if (fechaFin != null) {
            tratamiento.finalizar(fechaFin);
        }
        return tratamientos.save(tratamiento);
    }

    @Override
    public Tratamiento finalizarTratamiento(String idTratamiento, LocalDate fechaFin) {
        Tratamiento tratamiento = tratamientos.findById(idTratamiento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tratamiento no encontrado"));
        if (!tratamiento.estaActivo()) {
            throw new ReglaDeNegocioException("El tratamiento ya está finalizado");
        }
        Validaciones.noAnterior(fechaFin, tratamiento.getFechaInicio(), "fechaFin",
                "La fecha de finalización no puede ser anterior a la fecha de inicio");
        tratamiento.finalizar(fechaFin);
        return tratamientos.save(tratamiento);
    }

    // ----------------------------------------------------- IHistorialMedico

    @Override
    public List<VacunaAplicada> vacunasDe(String idAnimal) {
        List<Vacunacion> aplicadas = vacunaciones.findByIdAnimalOrderByFechaAplicacionDesc(idAnimal);
        Map<String, TipoVacuna> catalogo = tipos.findAllById(
                        aplicadas.stream().map(Vacunacion::getIdTipoVacuna).distinct().toList())
                .stream()
                .collect(Collectors.toMap(TipoVacuna::getId, Function.identity()));
        return aplicadas.stream()
                .map(v -> new VacunaAplicada(v.getId(), v.getIdTipoVacuna(),
                        catalogo.containsKey(v.getIdTipoVacuna())
                                ? catalogo.get(v.getIdTipoVacuna()).getNombre()
                                : null,
                        v.getFechaAplicacion(), v.getIdVeterinario()))
                .toList();
    }

    @Override
    public List<Tratamiento> tratamientosDe(String idAnimal) {
        return tratamientos.findByIdAnimalOrderByFechaInicioDesc(idAnimal);
    }

    @Override
    public boolean tieneTratamientoActivo(String idAnimal) {
        return tratamientosDe(idAnimal).stream().anyMatch(Tratamiento::estaActivo);
    }

    private TipoVacuna obtenerTipo(String idTipoVacuna) {
        return tipos.findById(idTipoVacuna)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de vacuna no encontrado"));
    }
}
