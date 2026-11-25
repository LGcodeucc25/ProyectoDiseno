package com.refugio.modelo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entrada del catálogo de vacunas (HU-09).
 *
 * <p>Patrón tipo/instancia junto con {@link Vacunacion}: aquí vive el tipo que
 * se administra una sola vez, allí el hecho de aplicarlo a un animal en una
 * fecha. Sin esta separación el tipo de vacuna sería texto libre.
 */
@Document(collection = "tiposVacuna")
public class TipoVacuna implements IDesactivable {

    @Id
    private String id;

    @Indexed(unique = true)
    private String nombre;

    private String descripcion;

    private boolean activo = true;

    public TipoVacuna() {
    }

    public TipoVacuna(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
