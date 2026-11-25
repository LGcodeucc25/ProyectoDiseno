package com.refugio.presentacion.dto;

import java.time.LocalDate;

import com.refugio.modelo.Animal;
import com.refugio.modelo.Especie;
import com.refugio.modelo.OrigenIngreso;
import com.refugio.modelo.Sexo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * POST y PUT /api/animales (HU-03). Raza y descripción son opcionales.
 * No lleva estado: el estado solo cambia por HU-04 o por el flujo de adopción.
 */
public record AnimalPeticion(
        @NotBlank String nombre,
        @NotNull Especie especie,
        String raza,
        @NotNull Sexo sexo,
        @NotBlank String edadAproximada,
        @NotNull LocalDate fechaIngreso,
        @NotNull OrigenIngreso origenIngreso,
        String descripcion) {

    public Animal aAnimal() {
        return new Animal(nombre.trim(), especie, raza, sexo, edadAproximada.trim(), fechaIngreso,
                origenIngreso, descripcion);
    }
}
