package com.refugio.presentacion;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.refugio.config.SecurityConfig;
import com.refugio.config.UsuarioAutenticado;
import com.refugio.modelo.TipoVacuna;
import com.refugio.modelo.Tratamiento;
import com.refugio.modelo.Vacunacion;
import com.refugio.negocio.IAtencionMedica;
import com.refugio.presentacion.dto.FinalizacionPeticion;
import com.refugio.presentacion.dto.TipoVacunaPeticion;
import com.refugio.presentacion.dto.TratamientoPeticion;
import com.refugio.presentacion.dto.VacunacionPeticion;

import jakarta.validation.Valid;

/** Atención Médica (HU-09, HU-10, HU-11). Depende solo de {@link IAtencionMedica}. */
@RestController
@RequestMapping("/api")
public class AtencionMedicaController {

    private final IAtencionMedica atencion;

    public AtencionMedicaController(IAtencionMedica atencion) {
        this.atencion = atencion;
    }

    @PostMapping("/tipos-vacuna")
    @ResponseStatus(HttpStatus.CREATED)
    public TipoVacuna crearTipoVacuna(@Valid @RequestBody TipoVacunaPeticion p) {
        return atencion.crearTipoVacuna(p.nombre(), p.descripcion());
    }

    @PostMapping("/tipos-vacuna/{id}/desactivacion")
    public TipoVacuna desactivarTipoVacuna(@PathVariable String id) {
        return atencion.desactivarTipoVacuna(id);
    }

    @PostMapping("/tipos-vacuna/{id}/reactivacion")
    public TipoVacuna reactivarTipoVacuna(@PathVariable String id) {
        return atencion.reactivarTipoVacuna(id);
    }

    @PostMapping("/animales/{id}/vacunaciones")
    @ResponseStatus(HttpStatus.CREATED)
    public Vacunacion registrarVacunacion(@PathVariable String id,
                                          @Valid @RequestBody VacunacionPeticion p,
                                          @RequestAttribute(SecurityConfig.USUARIO) UsuarioAutenticado yo) {
        return atencion.registrarVacunacion(id, p.idTipoVacuna(), p.fechaAplicacion(), yo.id());
    }

    @PostMapping("/animales/{id}/tratamientos")
    @ResponseStatus(HttpStatus.CREATED)
    public Tratamiento registrarTratamiento(@PathVariable String id,
                                            @Valid @RequestBody TratamientoPeticion p,
                                            @RequestAttribute(SecurityConfig.USUARIO) UsuarioAutenticado yo) {
        return atencion.registrarTratamiento(id, p.diagnostico(), p.fechaInicio(), p.fechaFin(),
                p.indicaciones(), yo.id());
    }

    @PostMapping("/tratamientos/{id}/finalizacion")
    public Tratamiento finalizarTratamiento(@PathVariable String id,
                                            @Valid @RequestBody FinalizacionPeticion p) {
        return atencion.finalizarTratamiento(id, p.fechaFin());
    }
}
