package com.refugio.presentacion.dto;

import java.time.LocalDate;
import java.util.List;

import com.refugio.modelo.Animal;
import com.refugio.modelo.Especie;
import com.refugio.modelo.EstadoAnimal;
import com.refugio.modelo.OrigenIngreso;
import com.refugio.modelo.RegistroEstado;
import com.refugio.modelo.Sexo;

/** Un animal con su estado actual calculado, que no es un campo guardado. */
public record AnimalRespuesta(String id, String nombre, Especie especie, String raza, Sexo sexo,
                              String edadAproximada, LocalDate fechaIngreso,
                              OrigenIngreso origenIngreso, String descripcion,
                              EstadoAnimal estadoActual, String idEspacio, String idVoluntario,
                              List<RegistroEstado> historialEstados) {

    public static AnimalRespuesta de(Animal a) {
        return new AnimalRespuesta(a.getId(), a.getNombre(), a.getEspecie(), a.getRaza(),
                a.getSexo(), a.getEdadAproximada(), a.getFechaIngreso(), a.getOrigenIngreso(),
                a.getDescripcion(), a.estadoActual(), a.getIdEspacio(), a.getIdVoluntario(),
                a.getHistorialEstados());
    }
}
