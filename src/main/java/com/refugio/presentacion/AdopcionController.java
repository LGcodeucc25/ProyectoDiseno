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
import com.refugio.modelo.SolicitudAdopcion;
import com.refugio.modelo.VisitaSeguimiento;
import com.refugio.negocio.IGestionAdopciones;
import com.refugio.presentacion.dto.AdopcionPeticion;
import com.refugio.presentacion.dto.AdoptantePeticion;
import com.refugio.presentacion.dto.AdoptanteRespuesta;
import com.refugio.presentacion.dto.EvaluacionPeticion;
import com.refugio.presentacion.dto.FechaMotivoPeticion;
import com.refugio.presentacion.dto.ResultadoVisitaPeticion;
import com.refugio.presentacion.dto.SolicitudPeticion;
import com.refugio.presentacion.dto.VisitaPeticion;

import jakarta.validation.Valid;

/**
 * Gestión de Adopciones (HU-12 a HU-20). Depende solo de {@link IGestionAdopciones}.
 *
 * <p>La adopción y las visitas cuelgan de {@code /api/solicitudes/{id}} porque
 * van incrustadas en el documento de la solicitud.
 */
@RestController
@RequestMapping("/api")
public class AdopcionController {

    private final IGestionAdopciones adopciones;

    public AdopcionController(IGestionAdopciones adopciones) {
        this.adopciones = adopciones;
    }

    @PostMapping("/adoptantes")
    @ResponseStatus(HttpStatus.CREATED)
    public AdoptanteRespuesta registrarAdoptante(@Valid @RequestBody AdoptantePeticion p) {
        return AdoptanteRespuesta.de(adopciones.registrarAdoptante(p.aAdoptante()));
    }

    @PostMapping("/solicitudes")
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudAdopcion registrarSolicitud(@Valid @RequestBody SolicitudPeticion p) {
        return adopciones.registrarSolicitud(p.idAnimal(), p.idAdoptante(), p.fechaSolicitud());
    }

    @PostMapping("/solicitudes/{id}/evaluacion")
    public SolicitudAdopcion evaluarSolicitud(@PathVariable String id,
                                              @Valid @RequestBody EvaluacionPeticion p,
                                              @RequestAttribute(SecurityConfig.USUARIO) UsuarioAutenticado yo) {
        return adopciones.evaluarSolicitud(id, p.resultado(), p.fecha(), p.observaciones(), yo.id());
    }

    @PostMapping("/solicitudes/{id}/cancelacion")
    public SolicitudAdopcion cancelarSolicitud(@PathVariable String id,
                                               @Valid @RequestBody FechaMotivoPeticion p) {
        return adopciones.cancelarSolicitud(id, p.fecha(), p.motivo());
    }

    @PostMapping("/solicitudes/{id}/adopcion")
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudAdopcion registrarAdopcion(@PathVariable String id,
                                               @Valid @RequestBody AdopcionPeticion p) {
        return adopciones.registrarAdopcion(id, p.fechaEntrega());
    }

    @PostMapping("/solicitudes/{id}/devolucion")
    public SolicitudAdopcion registrarDevolucion(@PathVariable String id,
                                                 @Valid @RequestBody FechaMotivoPeticion p) {
        return adopciones.registrarDevolucion(id, p.fecha(), p.motivo());
    }

    @PostMapping("/solicitudes/{id}/visitas")
    @ResponseStatus(HttpStatus.CREATED)
    public VisitaSeguimiento programarVisita(@PathVariable String id,
                                             @Valid @RequestBody VisitaPeticion p) {
        return adopciones.programarVisita(id, p.fechaPrevista(), p.idVoluntario(), p.indicaciones());
    }

    @PostMapping("/solicitudes/{id}/visitas/{idVisita}/resultado")
    public VisitaSeguimiento registrarResultadoVisita(@PathVariable String id,
                                                      @PathVariable String idVisita,
                                                      @Valid @RequestBody ResultadoVisitaPeticion p,
                                                      @RequestAttribute(SecurityConfig.USUARIO) UsuarioAutenticado yo) {
        return adopciones.registrarResultadoVisita(id, idVisita, p.fechaRealizacion(),
                p.calificacion(), p.observaciones(), yo.id());
    }
}
