package com.refugio.modelo;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Aplicación concreta de una vacuna a un animal (HU-10).
 *
 * <p>Es parte del historial del animal, pero vive en su propia colección: la
 * composición cruza la frontera del componente. Atención Médica la posee, y
 * incrustarla obligaría a ese componente a escribir dentro de los documentos
 * de Gestión de Animales.
 */
@Document(collection = "vacunaciones")
public class Vacunacion {

    @Id
    private String id;

    @Indexed
    private String idAnimal;

    private String idTipoVacuna;

    /** Veterinario que la aplicó. */
    private String idVeterinario;

    private LocalDate fechaAplicacion;

    public Vacunacion() {
    }

    public Vacunacion(String idAnimal, String idTipoVacuna, String idVeterinario,
                      LocalDate fechaAplicacion) {
        this.idAnimal = idAnimal;
        this.idTipoVacuna = idTipoVacuna;
        this.idVeterinario = idVeterinario;
        this.fechaAplicacion = fechaAplicacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdAnimal() {
        return idAnimal;
    }

    public void setIdAnimal(String idAnimal) {
        this.idAnimal = idAnimal;
    }

    public String getIdTipoVacuna() {
        return idTipoVacuna;
    }

    public void setIdTipoVacuna(String idTipoVacuna) {
        this.idTipoVacuna = idTipoVacuna;
    }

    public String getIdVeterinario() {
        return idVeterinario;
    }

    public void setIdVeterinario(String idVeterinario) {
        this.idVeterinario = idVeterinario;
    }

    public LocalDate getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(LocalDate fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }
}
