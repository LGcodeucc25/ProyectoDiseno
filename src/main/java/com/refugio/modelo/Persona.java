package com.refugio.modelo;

/**
 * Datos de contacto comunes a {@link Usuario} y {@link Adoptante}.
 *
 * <p>No es una colección de MongoDB: no se guarda ninguna Persona suelta.
 * Usuario y Adoptante tienen cada uno la suya, y heredan de aquí los cuatro
 * atributos que comparten de verdad. La generalización responde a atributos
 * compartidos, no a una clasificación por permisos.
 */
public abstract class Persona {

    private String id;
    private String identificacion;
    private String nombre;
    private String correo;
    private String telefono;

    protected Persona() {
    }

    protected Persona(String identificacion, String nombre, String correo, String telefono) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
