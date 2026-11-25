package com.refugio.modelo;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Persona que solicita adoptar un animal (HU-12).
 *
 * <p>No es actor del sistema: no inicia sesión ni opera nada. Sus datos los
 * registra el coordinador en su nombre. Está aquí porque esos datos se
 * almacenan y sustentan la evaluación de la solicitud.
 */
@Document(collection = "adoptantes")
public class Adoptante extends Persona {

    @Indexed(unique = true)
    private String identificacionIndex;

    private String direccion;

    private TipoVivienda tipoVivienda;

    private boolean experienciaPrevia;

    private String notasEntrevista;

    public Adoptante() {
    }

    public Adoptante(String identificacion, String nombre, String correo, String telefono,
                     String direccion, TipoVivienda tipoVivienda, boolean experienciaPrevia) {
        super(identificacion, nombre, correo, telefono);
        this.direccion = direccion;
        this.tipoVivienda = tipoVivienda;
        this.experienciaPrevia = experienciaPrevia;
        sincronizarIndices();
    }

    public void sincronizarIndices() {
        this.identificacionIndex = getIdentificacion();
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public TipoVivienda getTipoVivienda() {
        return tipoVivienda;
    }

    public void setTipoVivienda(TipoVivienda tipoVivienda) {
        this.tipoVivienda = tipoVivienda;
    }

    public boolean isExperienciaPrevia() {
        return experienciaPrevia;
    }

    public void setExperienciaPrevia(boolean experienciaPrevia) {
        this.experienciaPrevia = experienciaPrevia;
    }

    public String getNotasEntrevista() {
        return notasEntrevista;
    }

    public void setNotasEntrevista(String notasEntrevista) {
        this.notasEntrevista = notasEntrevista;
    }

    public String getIdentificacionIndex() {
        return identificacionIndex;
    }

    public void setIdentificacionIndex(String identificacionIndex) {
        this.identificacionIndex = identificacionIndex;
    }
}
