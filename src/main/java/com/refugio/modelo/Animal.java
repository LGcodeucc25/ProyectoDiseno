package com.refugio.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Animal rescatado (HU-03, HU-04, HU-05, HU-21).
 *
 * <p>El estado no es un atributo: es el último elemento de {@code historialEstados}.
 * Esa es la decisión de diseño central del proyecto — HU-04 exige saber cuándo
 * cambió el estado y quién lo cambió, y un dato que se repite en el tiempo y
 * tiene atributos propios deja de ser atributo y pasa a ser clase.
 */
@Document(collection = "animales")
public class Animal {

    @Id
    private String id;

    private String nombre;
    private Especie especie;
    private String raza;
    private Sexo sexo;
    private String edadAproximada;
    private LocalDate fechaIngreso;
    private OrigenIngreso origenIngreso;
    private String descripcion;

    /** Espacio que ocupa, o null si todavía no tiene alojamiento asignado. */
    @Indexed
    private String idEspacio;

    /** Voluntario responsable de su cuidado, o null si no se le ha asignado uno. */
    @Indexed
    private String idVoluntario;

    /** Composición: el historial no existe sin el animal. Siempre tiene al menos un elemento. */
    private List<RegistroEstado> historialEstados = new ArrayList<>();

    public Animal() {
    }

    public Animal(String nombre, Especie especie, String raza, Sexo sexo, String edadAproximada,
                  LocalDate fechaIngreso, OrigenIngreso origenIngreso, String descripcion) {
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.sexo = sexo;
        this.edadAproximada = edadAproximada;
        this.fechaIngreso = fechaIngreso;
        this.origenIngreso = origenIngreso;
        this.descripcion = descripcion;
    }

    // ------------------------------------------------------------- estado

    /** Añade un registro al historial. Es la única forma de cambiar de estado. */
    public void cambiarEstado(RegistroEstado registro) {
        if (historialEstados == null) {
            historialEstados = new ArrayList<>();
        }
        historialEstados.add(registro);
    }

    /** Estado de hoy: el del último registro. Es un cálculo, no un campo guardado. */
    public EstadoAnimal estadoActual() {
        if (historialEstados == null || historialEstados.isEmpty()) {
            return null;
        }
        return historialEstados.get(historialEstados.size() - 1).getEstado();
    }

    public RegistroEstado ultimoRegistro() {
        if (historialEstados == null || historialEstados.isEmpty()) {
            return null;
        }
        return historialEstados.get(historialEstados.size() - 1);
    }

    public boolean estaDisponible() {
        return estadoActual() == EstadoAnimal.DISPONIBLE;
    }

    public boolean estaAlojado() {
        return idEspacio != null;
    }

    public void liberarAlojamiento() {
        this.idEspacio = null;
    }

    // ------------------------------------------------------------ getters

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

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public String getEdadAproximada() {
        return edadAproximada;
    }

    public void setEdadAproximada(String edadAproximada) {
        this.edadAproximada = edadAproximada;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public OrigenIngreso getOrigenIngreso() {
        return origenIngreso;
    }

    public void setOrigenIngreso(OrigenIngreso origenIngreso) {
        this.origenIngreso = origenIngreso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIdEspacio() {
        return idEspacio;
    }

    public void setIdEspacio(String idEspacio) {
        this.idEspacio = idEspacio;
    }

    public String getIdVoluntario() {
        return idVoluntario;
    }

    public void setIdVoluntario(String idVoluntario) {
        this.idVoluntario = idVoluntario;
    }

    public List<RegistroEstado> getHistorialEstados() {
        return historialEstados;
    }

    public void setHistorialEstados(List<RegistroEstado> historialEstados) {
        this.historialEstados = historialEstados;
    }
}
