package com.refugio.presentacion;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.refugio.modelo.EspacioAlojamiento;
import com.refugio.negocio.IAdministracionEspacios;
import com.refugio.presentacion.dto.CapacidadPeticion;
import com.refugio.presentacion.dto.EspacioPeticion;
import com.refugio.presentacion.dto.OcupacionRespuesta;

import jakarta.validation.Valid;

/** Gestión de Alojamiento (HU-06). Depende solo de {@link IAdministracionEspacios}. */
@RestController
@RequestMapping("/api/espacios")
public class EspacioAlojamientoController {

    private final IAdministracionEspacios espacios;

    public EspacioAlojamientoController(IAdministracionEspacios espacios) {
        this.espacios = espacios;
    }

    /** GET /api/espacios?inactivos=true — por defecto solo los activos (HU-06, criterio 10). */
    @GetMapping
    public List<OcupacionRespuesta> listar(
            @RequestParam(name = "inactivos", defaultValue = "false") boolean incluirInactivos) {
        return espacios.listarEspacios(incluirInactivos).stream().map(OcupacionRespuesta::de).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EspacioAlojamiento crear(@Valid @RequestBody EspacioPeticion p) {
        return espacios.crearEspacio(p.nombre(), p.ubicacion(), p.capacidad());
    }

    @PatchMapping("/{id}")
    public EspacioAlojamiento actualizarCapacidad(@PathVariable String id,
                                                  @Valid @RequestBody CapacidadPeticion p) {
        return espacios.actualizarCapacidad(id, p.capacidad());
    }

    @PostMapping("/{id}/desactivacion")
    public EspacioAlojamiento desactivar(@PathVariable String id) {
        return espacios.desactivarEspacio(id);
    }

    @PostMapping("/{id}/reactivacion")
    public EspacioAlojamiento reactivar(@PathVariable String id) {
        return espacios.reactivarEspacio(id);
    }

    @GetMapping("/{id}/ocupacion")
    public OcupacionRespuesta consultarOcupacion(@PathVariable String id) {
        return OcupacionRespuesta.de(espacios.consultarOcupacion(id));
    }
}
