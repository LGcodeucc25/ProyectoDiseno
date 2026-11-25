package com.refugio.negocio;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.refugio.datos.AnimalRepository;
import com.refugio.modelo.Animal;
import com.refugio.modelo.EspacioAlojamiento;
import com.refugio.modelo.EstadoAnimal;
import com.refugio.modelo.RegistroEstado;
import com.refugio.modelo.Rol;

/**
 * Gestión de Animales. Proporciona la fachada {@link IGestionAnimales} y los
 * contratos internos {@link IConsultaAnimal} e {@link ITransicionEstado}.
 *
 * <p>Requiere {@link IEspacios}, {@link IConsultaUsuario} e {@link IBajaSolicitudes},
 * siempre por el tipo interfaz.
 */
@Service
public class AnimalService implements IGestionAnimales, IConsultaAnimal, ITransicionEstado {

    /** Estados en los que el animal está físicamente en el refugio (HU-05, HU-21). */
    private static final Set<EstadoAnimal> EN_EL_REFUGIO = EnumSet.of(
            EstadoAnimal.EN_VALORACION, EstadoAnimal.DISPONIBLE, EstadoAnimal.EN_PROCESO_ADOPCION);

    private final AnimalRepository animales;
    private final IEspacios espacios;
    private final IConsultaUsuario usuarios;
    private final IBajaSolicitudes bajaSolicitudes;

    /**
     * {@code @Lazy} en {@code bajaSolicitudes}: AnimalService y AdopcionService se
     * necesitan mutuamente (IBajaSolicitudes en este sentido, ITransicionEstado e
     * IConsultaAnimal en el otro). Es el ciclo aceptado y justificado en el README
     * —HU-04 y HU-14 son reglas simétricas— y no un error de diseño. Con inyección
     * por constructor en los dos lados, Spring no puede crear ninguno primero y
     * falla con BeanCurrentlyInCreationException. {@code @Lazy} inyecta aquí un
     * proxy que resuelve AdopcionService en la primera llamada, cuando ambos ya
     * existen. Se pone en este lado porque es el que usa la dependencia con
     * menos frecuencia (solo al sacar a un animal de Disponible a mano).
     */
    public AnimalService(AnimalRepository animales, IEspacios espacios, IConsultaUsuario usuarios,
                         @Lazy IBajaSolicitudes bajaSolicitudes) {
        this.animales = animales;
        this.espacios = espacios;
        this.usuarios = usuarios;
        this.bajaSolicitudes = bajaSolicitudes;
    }

    // ----------------------------------------------------- IGestionAnimales

    @Override
    public Animal registrarAnimal(Animal datos, String idUsuario) {
        Validaciones.noFutura(datos.getFechaIngreso(), "fechaIngreso",
                "La fecha de ingreso no puede ser futura");
        datos.setId(null);
        datos.setIdEspacio(null);
        datos.setIdVoluntario(null);
        datos.setHistorialEstados(null);
        datos.cambiarEstado(new RegistroEstado(EstadoAnimal.EN_VALORACION, datos.getFechaIngreso(),
                "Ingreso al refugio", idUsuario, false));
        return animales.save(datos);
    }

    @Override
    public Animal actualizarAnimal(String idAnimal, Animal datos) {
        Validaciones.noFutura(datos.getFechaIngreso(), "fechaIngreso",
                "La fecha de ingreso no puede ser futura");
        Animal animal = obtenerAnimal(idAnimal);
        animal.setNombre(datos.getNombre());
        animal.setEspecie(datos.getEspecie());
        animal.setRaza(datos.getRaza());
        animal.setSexo(datos.getSexo());
        animal.setEdadAproximada(datos.getEdadAproximada());
        animal.setFechaIngreso(datos.getFechaIngreso());
        animal.setOrigenIngreso(datos.getOrigenIngreso());
        animal.setDescripcion(datos.getDescripcion());
        return animales.save(animal);
    }

    @Override
    public Animal cambiarEstado(String idAnimal, EstadoAnimal nuevo, LocalDate fecha, String motivo,
                                String idUsuario) {
        Validaciones.noFutura(fecha, "fecha", "La fecha del cambio no puede ser futura");
        if (nuevo == EstadoAnimal.NO_DISPONIBLE) {
            Validaciones.obligatorio(motivo, "motivo",
                    "El motivo es obligatorio para el estado No disponible");
        }
        Animal animal = obtenerAnimal(idAnimal);
        EstadoAnimal actual = animal.estadoActual();

        // La tabla de transiciones es del dominio: aquí solo se distingue el error.
        if (!actual.permiteManual(nuevo)) {
            if (actual.esReservadaAlFlujoDeAdopcion(nuevo)) {
                throw new TransicionReservadaException();
            }
            throw new TransicionNoPermitidaException();
        }

        animal.cambiarEstado(new RegistroEstado(nuevo, fecha, motivo, idUsuario, false));
        if (nuevo == EstadoAnimal.NO_DISPONIBLE) {
            salirDelRefugio(animal);
        }
        Animal guardado = animales.save(animal);

        // HU-04: si deja de estar Disponible, sus solicitudes pendientes se rechazan ya.
        if (actual == EstadoAnimal.DISPONIBLE) {
            bajaSolicitudes.rechazarPendientesDe(idAnimal, fecha);
        }
        return guardado;
    }

    @Override
    public Animal asignarAlojamiento(String idAnimal, String idEspacio, LocalDate fecha) {
        Validaciones.noFutura(fecha, "fecha", "La fecha de asignación no puede ser futura");
        Animal animal = obtenerAnimal(idAnimal);
        if (!EN_EL_REFUGIO.contains(animal.estadoActual())) {
            throw new ReglaDeNegocioException("El animal no se encuentra en el refugio");
        }
        if (idEspacio.equals(animal.getIdEspacio())) {
            throw new ReglaDeNegocioException("El animal ya está alojado en ese espacio");
        }
        EspacioAlojamiento espacio = espacios.obtenerEspacio(idEspacio);
        if (!espacio.estaActivo()) {
            throw new ReglaDeNegocioException("El espacio está inactivo");
        }
        if (!espacios.tieneCupo(idEspacio)) {
            throw new ReglaDeNegocioException("El espacio no tiene cupo disponible");
        }
        // Ocupar es fijar Animal.idEspacio; el espacio anterior queda libre por el mismo acto.
        animal.setIdEspacio(idEspacio);
        return animales.save(animal);
    }

    @Override
    public Animal asignarVoluntario(String idAnimal, String idVoluntario, LocalDate fecha) {
        Validaciones.noFutura(fecha, "fecha", "La fecha de asignación no puede ser futura");
        Animal animal = obtenerAnimal(idAnimal);
        if (!EN_EL_REFUGIO.contains(animal.estadoActual())) {
            throw new ReglaDeNegocioException(
                    "Solo se puede asignar voluntario a animales que están en el refugio");
        }
        if (!usuarios.existeConRol(idVoluntario, Rol.VOLUNTARIO)) {
            throw new ReglaDeNegocioException(
                    "El usuario asignado debe existir y tener el rol Voluntario");
        }
        animal.setIdVoluntario(idVoluntario);
        return animales.save(animal);
    }

    // ------------------------------------------------------ IConsultaAnimal

    @Override
    public Animal obtenerAnimal(String idAnimal) {
        return animales.findById(idAnimal)
                .orElseThrow(() -> new RecursoNoEncontradoException("Animal no encontrado"));
    }

    @Override
    public EstadoAnimal estadoActual(String idAnimal) {
        return obtenerAnimal(idAnimal).estadoActual();
    }

    @Override
    public boolean estaDisponible(String idAnimal) {
        return obtenerAnimal(idAnimal).estaDisponible();
    }

    @Override
    public List<Animal> listar(Filtro filtro) {
        return animales.buscar(filtro.especie(), filtro.raza(), filtro.sexo(), filtro.edad(),
                filtro.estado(), filtro.idVoluntario());
    }

    @Override
    public String ubicacionDe(String idAnimal) {
        Animal animal = obtenerAnimal(idAnimal);
        if (!animal.estaAlojado()) {
            return null;
        }
        EspacioAlojamiento espacio = espacios.obtenerEspacio(animal.getIdEspacio());
        String detalle = espacio.getUbicacion();
        return (detalle == null || detalle.isBlank())
                ? espacio.getNombre()
                : espacio.getNombre() + " — " + detalle;
    }

    // ---------------------------------------------------- ITransicionEstado

    @Override
    public void marcarEnProcesoAdopcion(String idAnimal, LocalDate fecha, String motivo) {
        aplicarAutomatica(idAnimal, EstadoAnimal.EN_PROCESO_ADOPCION, fecha, motivo);
    }

    @Override
    public void marcarAdoptado(String idAnimal, LocalDate fecha, String motivo) {
        aplicarAutomatica(idAnimal, EstadoAnimal.ADOPTADO, fecha, motivo);
    }

    @Override
    public void marcarEnValoracion(String idAnimal, LocalDate fecha, String motivo) {
        aplicarAutomatica(idAnimal, EstadoAnimal.EN_VALORACION, fecha, motivo);
    }

    @Override
    public void marcarDisponible(String idAnimal, LocalDate fecha, String motivo) {
        aplicarAutomatica(idAnimal, EstadoAnimal.DISPONIBLE, fecha, motivo);
    }

    /**
     * Los cambios automáticos no rechazan solicitudes: el único que saca al
     * animal de Disponible es la aprobación, y Adopciones ya rechaza las demás
     * con su propio motivo (HU-14). El responsable queda nulo y el registro
     * marcado como automático: lo disparó el flujo, no una persona.
     */
    private void aplicarAutomatica(String idAnimal, EstadoAnimal destino, LocalDate fecha,
                                   String motivo) {
        Animal animal = obtenerAnimal(idAnimal);
        if (!animal.estadoActual().permiteAutomatica(destino)) {
            throw new TransicionNoPermitidaException();
        }
        animal.cambiarEstado(new RegistroEstado(destino, fecha, motivo, null, true));
        if (destino == EstadoAnimal.ADOPTADO) {
            salirDelRefugio(animal);
        }
        if (destino == EstadoAnimal.EN_VALORACION) {
            // Vuelve de una devolución: sin espacio hasta que se le asigne uno (HU-20).
            animal.liberarAlojamiento();
        }
        animales.save(animal);
    }

    /**
     * Al salir del refugio (Adoptado o No disponible) el animal libera su
     * espacio y deja de tener voluntario responsable (HU-04, HU-17, HU-21).
     * Liberar el espacio es borrar Animal.idEspacio: la ocupación se recalcula sola.
     */
    private static void salirDelRefugio(Animal animal) {
        animal.liberarAlojamiento();
        animal.setIdVoluntario(null);
    }
}
