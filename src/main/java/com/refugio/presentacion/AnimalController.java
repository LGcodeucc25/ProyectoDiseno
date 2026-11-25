package com.refugio.presentacion;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.refugio.config.SecurityConfig;
import com.refugio.config.UsuarioAutenticado;
import com.refugio.negocio.IGestionAnimales;
import com.refugio.presentacion.dto.AlojamientoPeticion;
import com.refugio.presentacion.dto.AnimalPeticion;
import com.refugio.presentacion.dto.AnimalRespuesta;
import com.refugio.presentacion.dto.CambioEstadoPeticion;
import com.refugio.presentacion.dto.ResponsablePeticion;

import jakarta.validation.Valid;

/**
 * Gestión de Animales (HU-03, HU-04, HU-05, HU-21). Depende solo de
 * {@link IGestionAnimales}. Las lecturas de animales están en {@link ConsultaController}.
 */
@RestController
@RequestMapping("/api/animales")
public class AnimalController {

    private final IGestionAnimales animales;

    public AnimalController(IGestionAnimales animales) {
        this.animales = animales;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnimalRespuesta registrar(@Valid @RequestBody AnimalPeticion p,
                                     @RequestAttribute(SecurityConfig.USUARIO) UsuarioAutenticado yo) {
        return AnimalRespuesta.de(animales.registrarAnimal(p.aAnimal(), yo.id()));
    }

    @PutMapping("/{id}")
    public AnimalRespuesta actualizar(@PathVariable String id, @Valid @RequestBody AnimalPeticion p) {
        return AnimalRespuesta.de(animales.actualizarAnimal(id, p.aAnimal()));
    }

    @PostMapping("/{id}/estados")
    public AnimalRespuesta cambiarEstado(@PathVariable String id,
                                         @Valid @RequestBody CambioEstadoPeticion p,
                                         @RequestAttribute(SecurityConfig.USUARIO) UsuarioAutenticado yo) {
        return AnimalRespuesta.de(
                animales.cambiarEstado(id, p.estado(), p.fecha(), p.motivo(), yo.id()));
    }

    @PostMapping("/{id}/alojamiento")
    public AnimalRespuesta asignarAlojamiento(@PathVariable String id,
                                              @Valid @RequestBody AlojamientoPeticion p) {
        return AnimalRespuesta.de(animales.asignarAlojamiento(id, p.idEspacio(), p.fecha()));
    }

    @PostMapping("/{id}/responsable")
    public AnimalRespuesta asignarVoluntario(@PathVariable String id,
                                             @Valid @RequestBody ResponsablePeticion p) {
        return AnimalRespuesta.de(animales.asignarVoluntario(id, p.idVoluntario(), p.fecha()));
    }
}
