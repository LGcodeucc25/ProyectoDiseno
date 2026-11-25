package com.refugio.negocio;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.refugio.datos.AdoptanteRepository;
import com.refugio.datos.SolicitudAdopcionRepository;
import com.refugio.modelo.Adopcion;
import com.refugio.modelo.Adoptante;
import com.refugio.modelo.Calificacion;
import com.refugio.modelo.EstadoSolicitud;
import com.refugio.modelo.ResultadoEvaluacion;
import com.refugio.modelo.Rol;
import com.refugio.modelo.SolicitudAdopcion;
import com.refugio.modelo.VisitaSeguimiento;

/**
 * Gestión de Adopciones. Proporciona la fachada {@link IGestionAdopciones} y
 * los contratos internos {@link IBajaSolicitudes} e {@link IConsultaAdopciones}.
 *
 * <p>Requiere {@link IConsultaAnimal} e {@link ITransicionEstado} de Gestión de
 * Animales, e {@link IConsultaUsuario} para validar al voluntario de una visita.
 * La dependencia con Gestión de Animales es mutua (ver el constructor de
 * {@link AnimalService}); aquí la inyección es normal y el {@code @Lazy} está
 * del otro lado.
 */
@Service
public class AdopcionService implements IGestionAdopciones, IBajaSolicitudes, IConsultaAdopciones {

    static final String MOTIVO_ANIMAL_NO_DISPONIBLE = "Animal no disponible";
    static final String MOTIVO_ASIGNADO_A_OTRO = "El animal fue asignado a otro adoptante";

    private static final EnumSet<EstadoSolicitud> ACTIVAS =
            EnumSet.of(EstadoSolicitud.PENDIENTE, EstadoSolicitud.APROBADA);

    private final AdoptanteRepository adoptantes;
    private final SolicitudAdopcionRepository solicitudes;
    private final IConsultaAnimal consultaAnimal;
    private final ITransicionEstado transicion;
    private final IConsultaUsuario usuarios;

    public AdopcionService(AdoptanteRepository adoptantes, SolicitudAdopcionRepository solicitudes,
                           IConsultaAnimal consultaAnimal, ITransicionEstado transicion,
                           IConsultaUsuario usuarios) {
        this.adoptantes = adoptantes;
        this.solicitudes = solicitudes;
        this.consultaAnimal = consultaAnimal;
        this.transicion = transicion;
        this.usuarios = usuarios;
    }

    // --------------------------------------------------- IGestionAdopciones

    @Override
    public Adoptante registrarAdoptante(Adoptante datos) {
        if (adoptantes.existsByIdentificacionIndex(datos.getIdentificacion())) {
            throw new ReglaDeNegocioException("Ya existe un adoptante con esa identificación");
        }
        datos.setId(null);
        datos.sincronizarIndices();
        return adoptantes.save(datos);
    }

    @Override
    public Adoptante actualizarAdoptante(String idAdoptante, Adoptante datos) {
        Adoptante adoptante = adoptantes.findById(idAdoptante)
                .orElseThrow(() -> new RecursoNoEncontradoException("Adoptante no encontrado"));
        boolean cambiaIdentificacion = !adoptante.getIdentificacion().equals(datos.getIdentificacion());
        if (cambiaIdentificacion && adoptantes.existsByIdentificacionIndex(datos.getIdentificacion())) {
            throw new ReglaDeNegocioException("Ya existe un adoptante con esa identificación");
        }
        adoptante.setIdentificacion(datos.getIdentificacion());
        adoptante.setNombre(datos.getNombre());
        adoptante.setCorreo(datos.getCorreo());
        adoptante.setTelefono(datos.getTelefono());
        adoptante.setDireccion(datos.getDireccion());
        adoptante.setTipoVivienda(datos.getTipoVivienda());
        adoptante.setExperienciaPrevia(datos.isExperienciaPrevia());
        adoptante.setNotasEntrevista(datos.getNotasEntrevista());
        adoptante.sincronizarIndices();
        return adoptantes.save(adoptante);
    }

    @Override
    public SolicitudAdopcion registrarSolicitud(String idAnimal, String idAdoptante,
                                                LocalDate fecha) {
        Validaciones.noFutura(fecha, "fechaSolicitud",
                "La fecha de la solicitud no puede ser futura");
        if (!adoptantes.existsById(idAdoptante)) {
            throw new RecursoNoEncontradoException("Adoptante no encontrado");
        }
        if (!consultaAnimal.estaDisponible(idAnimal)) {
            throw new ReglaDeNegocioException(
                    "Solo se pueden registrar solicitudes sobre animales disponibles");
        }
        if (solicitudes.existsByIdAnimalAndIdAdoptanteAndEstadoIn(idAnimal, idAdoptante, ACTIVAS)) {
            throw new ReglaDeNegocioException(
                    "El adoptante ya tiene una solicitud activa sobre este animal");
        }
        return solicitudes.save(new SolicitudAdopcion(idAnimal, idAdoptante, fecha));
    }

    @Override
    public SolicitudAdopcion evaluarSolicitud(String idSolicitud, ResultadoEvaluacion resultado,
                                              LocalDate fecha, String observaciones,
                                              String idEvaluador) {
        Validaciones.obligatorio(observaciones, "observaciones", "Las observaciones son obligatorias");
        Validaciones.noFutura(fecha, "fecha", "La fecha de evaluación no puede ser futura");
        SolicitudAdopcion solicitud = obtenerSolicitud(idSolicitud);
        if (!solicitud.estaPendiente()) {
            throw new ReglaDeNegocioException("Solo se pueden evaluar solicitudes pendientes");
        }
        Validaciones.noAnterior(fecha, solicitud.getFechaSolicitud(), "fecha",
                "La fecha de evaluación no puede ser anterior a la fecha de la solicitud");

        if (resultado == ResultadoEvaluacion.APROBADA) {
            // Primero el animal: si la transición no es posible, nada ha cambiado todavía.
            transicion.marcarEnProcesoAdopcion(solicitud.getIdAnimal(), fecha,
                    "Solicitud de adopción aprobada");
        }
        solicitud.evaluar(resultado, fecha, observaciones, idEvaluador);
        SolicitudAdopcion guardada = solicitudes.save(solicitud);

        if (resultado == ResultadoEvaluacion.APROBADA) {
            // HU-14: el animal ya tiene adoptante; las demás pendientes se rechazan.
            rechazarPendientes(solicitud.getIdAnimal(), fecha, MOTIVO_ASIGNADO_A_OTRO);
        }
        return guardada;
    }

    @Override
    public SolicitudAdopcion cancelarSolicitud(String idSolicitud, LocalDate fecha, String motivo) {
        Validaciones.obligatorio(motivo, "motivo", "El motivo es obligatorio");
        Validaciones.noFutura(fecha, "fecha", "La fecha de cancelación no puede ser futura");
        SolicitudAdopcion solicitud = obtenerSolicitud(idSolicitud);
        if (!ACTIVAS.contains(solicitud.getEstado())) {
            throw new ReglaDeNegocioException(
                    "Solo se pueden cancelar solicitudes pendientes o aprobadas");
        }
        if (solicitud.tieneAdopcion()) {
            throw new ReglaDeNegocioException(
                    "No se puede cancelar una solicitud cuya adopción ya se registró");
        }
        Validaciones.noAnterior(fecha, solicitud.getFechaSolicitud(), "fecha",
                "La fecha de cancelación no puede ser anterior a la fecha de la solicitud");

        boolean estabaAprobada = solicitud.estaAprobada();
        if (estabaAprobada) {
            // HU-15: el animal vuelve a Disponible. Las rechazadas siguen rechazadas.
            transicion.marcarDisponible(solicitud.getIdAnimal(), fecha,
                    "El adoptante canceló la solicitud aprobada");
        }
        solicitud.cancelar(fecha, motivo);
        return solicitudes.save(solicitud);
    }

    @Override
    public SolicitudAdopcion registrarAdopcion(String idSolicitud, LocalDate fechaEntrega) {
        Validaciones.noFutura(fechaEntrega, "fechaEntrega", "La fecha de entrega no puede ser futura");
        SolicitudAdopcion solicitud = obtenerSolicitud(idSolicitud);
        if (!solicitud.estaAprobada()) {
            throw new ReglaDeNegocioException(
                    "Solo se puede registrar la adopción de una solicitud aprobada");
        }
        if (solicitud.tieneAdopcion()) {
            throw new ReglaDeNegocioException("Esta solicitud ya tiene una adopción registrada");
        }
        Validaciones.noAnterior(fechaEntrega, solicitud.getFechaEvaluacion(), "fechaEntrega",
                "La fecha de entrega no puede ser anterior a la aprobación de la solicitud");

        // HU-17: Adoptado, y Gestión de Animales libera su espacio.
        transicion.marcarAdoptado(solicitud.getIdAnimal(), fechaEntrega, "Adopción registrada");
        solicitud.registrarAdopcion(new Adopcion(fechaEntrega));
        return solicitudes.save(solicitud);
    }

    @Override
    public SolicitudAdopcion registrarDevolucion(String idSolicitud, LocalDate fecha, String motivo) {
        Validaciones.obligatorio(motivo, "motivo", "El motivo es obligatorio");
        Validaciones.noFutura(fecha, "fecha", "La fecha de devolución no puede ser futura");
        SolicitudAdopcion solicitud = obtenerSolicitud(idSolicitud);
        Adopcion adopcion = exigirAdopcion(solicitud);
        if (adopcion.fueDevuelta()) {
            throw new ReglaDeNegocioException("Esta adopción ya tiene una devolución registrada");
        }
        Validaciones.noAnterior(fecha, adopcion.getFechaEntrega(), "fecha",
                "La fecha de devolución no puede ser anterior a la fecha de entrega");

        // HU-20: En valoración y sin espacio. La adopción se conserva con fecha y motivo,
        // y el propio modelo cancela las visitas que seguían programadas.
        transicion.marcarEnValoracion(solicitud.getIdAnimal(), fecha, "Devolución: " + motivo);
        adopcion.registrarDevolucion(fecha, motivo);
        return solicitudes.save(solicitud);
    }

    @Override
    public VisitaSeguimiento programarVisita(String idSolicitud, LocalDate fechaPrevista,
                                             String idVoluntario, String indicaciones) {
        SolicitudAdopcion solicitud = obtenerSolicitud(idSolicitud);
        Adopcion adopcion = exigirAdopcion(solicitud);
        if (adopcion.fueDevuelta()) {
            throw new ReglaDeNegocioException(
                    "No se pueden programar visitas de una adopción con devolución");
        }
        Validaciones.noAnterior(fechaPrevista, adopcion.getFechaEntrega(), "fechaPrevista",
                "La fecha prevista no puede ser anterior a la fecha de entrega");
        // Conexión 10: la misma regla que el responsable de un animal (HU-21).
        if (!usuarios.existeConRol(idVoluntario, Rol.VOLUNTARIO)) {
            throw new ReglaDeNegocioException(
                    "El usuario asignado debe existir y tener el rol Voluntario");
        }
        VisitaSeguimiento visita = new VisitaSeguimiento(fechaPrevista, idVoluntario, indicaciones);
        adopcion.programarVisita(visita);
        solicitudes.save(solicitud);
        return visita;
    }

    @Override
    public VisitaSeguimiento reprogramarVisita(String idSolicitud, String idVisita,
                                               LocalDate fechaPrevista, String idVoluntario) {
        SolicitudAdopcion solicitud = obtenerSolicitud(idSolicitud);
        VisitaSeguimiento visita = exigirProgramada(obtenerVisita(solicitud, idVisita));
        Validaciones.noAnterior(fechaPrevista, solicitud.getAdopcion().getFechaEntrega(),
                "fechaPrevista", "La fecha prevista no puede ser anterior a la fecha de entrega");
        if (idVoluntario != null && !idVoluntario.isBlank()) {
            // Conexión 10, igual que al programar.
            if (!usuarios.existeConRol(idVoluntario, Rol.VOLUNTARIO)) {
                throw new ReglaDeNegocioException(
                        "El usuario asignado debe existir y tener el rol Voluntario");
            }
            visita.setIdVoluntario(idVoluntario);
        }
        visita.reprogramar(fechaPrevista);
        solicitudes.save(solicitud);
        return visita;
    }

    @Override
    public VisitaSeguimiento cancelarVisita(String idSolicitud, String idVisita, String motivo) {
        Validaciones.obligatorio(motivo, "motivo", "El motivo es obligatorio");
        SolicitudAdopcion solicitud = obtenerSolicitud(idSolicitud);
        VisitaSeguimiento visita = exigirProgramada(obtenerVisita(solicitud, idVisita));
        visita.cancelar(motivo);
        solicitudes.save(solicitud);
        return visita;
    }

    @Override
    public VisitaSeguimiento registrarResultadoVisita(String idSolicitud, String idVisita,
                                                      LocalDate fechaRealizacion,
                                                      Calificacion calificacion,
                                                      String observaciones,
                                                      String idVoluntarioSesion) {
        Validaciones.obligatorio(observaciones, "observaciones", "Las observaciones son obligatorias");
        Validaciones.noFutura(fechaRealizacion, "fechaRealizacion",
                "La fecha de realización no puede ser futura");
        SolicitudAdopcion solicitud = obtenerSolicitud(idSolicitud);
        Adopcion adopcion = solicitud.getAdopcion();
        VisitaSeguimiento visita = obtenerVisita(solicitud, idVisita);
        if (!Objects.equals(visita.getIdVoluntario(), idVoluntarioSesion)) {
            throw new AccesoDenegadoException(
                    "Solo el voluntario asignado puede registrar el resultado de esta visita");
        }
        if (!visita.estaProgramada()) {
            throw new ReglaDeNegocioException(
                    "Solo se puede registrar el resultado de visitas programadas");
        }
        Validaciones.noAnterior(fechaRealizacion, adopcion.getFechaEntrega(), "fechaRealizacion",
                "La fecha de realización no puede ser anterior a la fecha de entrega");
        visita.registrarResultado(fechaRealizacion, calificacion, observaciones);
        solicitudes.save(solicitud);
        return visita;
    }

    // ----------------------------------------------------- IBajaSolicitudes

    @Override
    public int rechazarPendientesDe(String idAnimal, LocalDate fecha) {
        return rechazarPendientes(idAnimal, fecha, MOTIVO_ANIMAL_NO_DISPONIBLE);
    }

    // -------------------------------------------------- IConsultaAdopciones

    @Override
    public List<SolicitudAdopcion> solicitudesDe(String idAnimal, String idAdoptante,
                                                 EstadoSolicitud estado) {
        List<SolicitudAdopcion> base;
        if (idAnimal != null) {
            base = solicitudes.findByIdAnimalOrderByFechaSolicitudDesc(idAnimal);
        } else if (idAdoptante != null) {
            base = solicitudes.findByIdAdoptanteOrderByFechaSolicitudDesc(idAdoptante);
        } else {
            base = solicitudes.findAllByOrderByFechaSolicitudDesc();
        }
        return base.stream()
                .filter(s -> idAdoptante == null || idAdoptante.equals(s.getIdAdoptante()))
                .filter(s -> estado == null || estado == s.getEstado())
                .toList();
    }

    @Override
    public Optional<Adopcion> adopcionDe(String idAnimal) {
        return adopcionesDe(idAnimal)
                .max(Comparator.comparing(Adopcion::getFechaEntrega));
    }

    @Override
    public List<VisitaSeguimiento> visitasDe(String idAnimal) {
        return adopcionesDe(idAnimal)
                .flatMap(a -> a.getVisitas() == null ? Stream.empty() : a.getVisitas().stream())
                .sorted(Comparator.comparing(VisitaSeguimiento::getFechaPrevista))
                .toList();
    }

    @Override
    public List<VisitaAsignada> visitasAsignadasA(String idVoluntario) {
        return solicitudes.buscarConVisitasDe(idVoluntario).stream()
                .flatMap(s -> s.getAdopcion().getVisitas().stream()
                        .filter(v -> idVoluntario.equals(v.getIdVoluntario()))
                        .filter(VisitaSeguimiento::estaProgramada)
                        .map(v -> new VisitaAsignada(s.getId(), s.getIdAnimal(),
                                s.getIdAdoptante(), v)))
                .sorted(Comparator.comparing(va -> va.visita().getFechaPrevista()))
                .toList();
    }

    // ------------------------------------------------------------- ayudas

    private int rechazarPendientes(String idAnimal, LocalDate fecha, String motivo) {
        List<SolicitudAdopcion> pendientes =
                solicitudes.findByIdAnimalAndEstado(idAnimal, EstadoSolicitud.PENDIENTE);
        pendientes.forEach(s -> s.rechazarAutomaticamente(fecha, motivo));
        solicitudes.saveAll(pendientes);
        return pendientes.size();
    }

    private Stream<Adopcion> adopcionesDe(String idAnimal) {
        return solicitudes.findByIdAnimalOrderByFechaSolicitudDesc(idAnimal).stream()
                .map(SolicitudAdopcion::getAdopcion)
                .filter(Objects::nonNull);
    }

    private SolicitudAdopcion obtenerSolicitud(String idSolicitud) {
        return solicitudes.findById(idSolicitud)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada"));
    }

    private static VisitaSeguimiento obtenerVisita(SolicitudAdopcion solicitud, String idVisita) {
        Adopcion adopcion = solicitud.getAdopcion();
        VisitaSeguimiento visita = adopcion == null ? null : adopcion.buscarVisita(idVisita);
        if (visita == null) {
            throw new RecursoNoEncontradoException("Visita de seguimiento no encontrada");
        }
        return visita;
    }

    /** HU-18, criterio 6: reprogramar y cancelar solo aplican a visitas programadas. */
    private static VisitaSeguimiento exigirProgramada(VisitaSeguimiento visita) {
        if (!visita.estaProgramada()) {
            throw new ReglaDeNegocioException("Solo se pueden modificar visitas programadas");
        }
        return visita;
    }

    private static Adopcion exigirAdopcion(SolicitudAdopcion solicitud) {
        if (!solicitud.tieneAdopcion()) {
            throw new ReglaDeNegocioException("La solicitud no tiene una adopción registrada");
        }
        return solicitud.getAdopcion();
    }
}
