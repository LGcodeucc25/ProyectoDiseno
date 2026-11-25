package com.refugio.modelo;

import java.util.EnumSet;
import java.util.Set;

/**
 * Estado de un animal dentro del refugio.
 *
 * <p>Las transiciones viven aquí y no en el service porque son una regla del
 * dominio: el conjunto de cambios válidos no depende de quién los pida. El
 * service decide <em>si</em> corresponde el cambio; la enumeración decide si el
 * cambio es siquiera posible (HU-04).
 */
public enum EstadoAnimal {

    EN_VALORACION,
    DISPONIBLE,
    EN_PROCESO_ADOPCION,
    ADOPTADO,
    NO_DISPONIBLE;

    /** Transiciones que un usuario puede registrar a mano (HU-04). */
    public Set<EstadoAnimal> transicionesManuales() {
        return switch (this) {
            case EN_VALORACION -> EnumSet.of(DISPONIBLE, NO_DISPONIBLE);
            case DISPONIBLE -> EnumSet.of(EN_VALORACION, NO_DISPONIBLE);
            case ADOPTADO -> EnumSet.of(NO_DISPONIBLE);
            case EN_PROCESO_ADOPCION, NO_DISPONIBLE -> EnumSet.noneOf(EstadoAnimal.class);
        };
    }

    /** Transiciones que solo dispara el proceso de adopción (HU-14, HU-15, HU-17, HU-20). */
    public Set<EstadoAnimal> transicionesAutomaticas() {
        return switch (this) {
            case DISPONIBLE -> EnumSet.of(EN_PROCESO_ADOPCION);
            case EN_PROCESO_ADOPCION -> EnumSet.of(ADOPTADO, DISPONIBLE);
            case ADOPTADO -> EnumSet.of(EN_VALORACION);
            case EN_VALORACION, NO_DISPONIBLE -> EnumSet.noneOf(EstadoAnimal.class);
        };
    }

    public boolean permiteManual(EstadoAnimal destino) {
        return transicionesManuales().contains(destino);
    }

    public boolean permiteAutomatica(EstadoAnimal destino) {
        return transicionesAutomaticas().contains(destino);
    }

    /** True cuando el cambio existe pero está reservado al flujo de adopción. */
    public boolean esReservadaAlFlujoDeAdopcion(EstadoAnimal destino) {
        return !permiteManual(destino) && permiteAutomatica(destino);
    }

    /** Estado terminal: no admite ningún cambio posterior. */
    public boolean esTerminal() {
        return transicionesManuales().isEmpty() && transicionesAutomaticas().isEmpty();
    }
}
