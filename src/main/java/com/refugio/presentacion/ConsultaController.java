package com.refugio.presentacion;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.refugio.config.SecurityConfig;
import com.refugio.config.UsuarioAutenticado;
import com.refugio.modelo.Especie;
import com.refugio.modelo.EstadoAnimal;
import com.refugio.modelo.EstadoSolicitud;
import com.refugio.modelo.Sexo;
import com.refugio.modelo.SolicitudAdopcion;
import com.refugio.negocio.FichaAnimal;
import com.refugio.negocio.IConsultaAdopciones.VisitaAsignada;
import com.refugio.negocio.IConsultaAnimal.Filtro;
import com.refugio.negocio.IConsultas;
import com.refugio.presentacion.dto.AnimalRespuesta;

/**
 * Consultas y Reportes (HU-07, HU-08, HU-16, HU-19). Depende solo de {@link IConsultas}.
 *
 * <p>Los filtros van como parámetros de consulta, todos opcionales y
 * combinados con Y. Sin paginación: con el volumen de RNF003 no aporta.
 */
@RestController
@RequestMapping("/api")
public class ConsultaController {

    private final IConsultas consultas;

    public ConsultaController(IConsultas consultas) {
        this.consultas = consultas;
    }

    /** GET /api/animales?especie=&raza=&sexo=&edad=&estado=&voluntario= */
    @GetMapping("/animales")
    public List<AnimalRespuesta> buscarAnimales(@RequestParam(required = false) Especie especie,
                                                @RequestParam(required = false) String raza,
                                                @RequestParam(required = false) Sexo sexo,
                                                @RequestParam(required = false) String edad,
                                                @RequestParam(required = false) EstadoAnimal estado,
                                                @RequestParam(required = false) String voluntario) {
        return consultas.buscarAnimales(new Filtro(especie, raza, sexo, edad, estado, voluntario))
                .stream().map(AnimalRespuesta::de).toList();
    }

    @GetMapping("/animales/{id}/ficha")
    public FichaAnimal fichaDe(@PathVariable String id) {
        return consultas.fichaDe(id);
    }

    /** GET /api/solicitudes?animal=&adoptante=&estado= */
    @GetMapping("/solicitudes")
    public List<SolicitudAdopcion> consultarSolicitudes(
            @RequestParam(required = false) String animal,
            @RequestParam(required = false) String adoptante,
            @RequestParam(required = false) EstadoSolicitud estado) {
        return consultas.consultarSolicitudes(animal, adoptante, estado);
    }

    /** Las visitas programadas del voluntario de la sesión. */
    @GetMapping("/visitas")
    public List<VisitaAsignada> visitasAsignadasA(
            @RequestAttribute(SecurityConfig.USUARIO) UsuarioAutenticado yo) {
        return consultas.visitasAsignadasA(yo.id());
    }
}
