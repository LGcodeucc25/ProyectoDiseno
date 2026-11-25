package com.refugio.modelo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Espacio físico del refugio donde se alojan los animales (HU-05, HU-06).
 *
 * <p>La ocupación no se guarda como campo: se cuenta sobre los animales que
 * referencian este espacio. Guardarla sería duplicar un dato que ya existe y
 * que puede quedar desincronizado. Por eso {@link #tieneCupo(long)} recibe la
 * ocupación en vez de calcularla: la regla vive aquí, el conteo lo hace el
 * repositorio.
 */
@Document(collection = "espacios")
public class EspacioAlojamiento implements IDesactivable {

    @Id
    private String id;

    @Indexed(unique = true)
    private String nombre;

    private String ubicacion;

    private int capacidad;

    private boolean activo = true;

    public EspacioAlojamiento() {
    }

    public EspacioAlojamiento(String nombre, String ubicacion, int capacidad) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.capacidad = capacidad;
        this.activo = true;
    }

    @Override
    public void desactivar() {
        this.activo = false;
    }

    @Override
    public void reactivar() {
        this.activo = true;
    }

    @Override
    public boolean estaActivo() {
        return activo;
    }

    /** Admite un animal más si está activo y la ocupación no llegó a la capacidad. */
    public boolean tieneCupo(long ocupacionActual) {
        return activo && ocupacionActual < capacidad;
    }

    public long cuposLibres(long ocupacionActual) {
        return Math.max(0, capacidad - ocupacionActual);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
