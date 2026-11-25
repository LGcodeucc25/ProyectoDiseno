package com.refugio.negocio;

import org.springframework.stereotype.Service;

import com.refugio.config.DatoInvalidoException;
import com.refugio.config.RecursoNoEncontradoException;
import com.refugio.config.ReglaDeNegocioException;
import com.refugio.datos.EspacioAlojamientoRepository;
import com.refugio.modelo.EspacioAlojamiento;

/**
 * Gestión de Alojamiento. Proporciona la fachada {@link IAdministracionEspacios}
 * y el contrato interno {@link IEspacios}.
 *
 * <p>La ocupación no se guarda: la cuenta el repositorio sobre
 * {@code Animal.idEspacio}, que es la única fuente de verdad. La regla de cupo
 * ({@code activo && ocupacion < capacidad}) vive en el dominio, en
 * {@link EspacioAlojamiento#tieneCupo(long)}.
 */
@Service
public class EspacioAlojamientoService implements IAdministracionEspacios, IEspacios {

    private final EspacioAlojamientoRepository espacios;

    public EspacioAlojamientoService(EspacioAlojamientoRepository espacios) {
        this.espacios = espacios;
    }

    // ---------------------------------------------- IAdministracionEspacios

    @Override
    public EspacioAlojamiento crearEspacio(String nombre, String ubicacion, int capacidad) {
        validarCapacidad(capacidad);
        if (espacios.existsByNombreIgnoreCase(nombre.trim())) {
            throw new ReglaDeNegocioException("Ya existe un espacio con ese nombre");
        }
        return espacios.save(new EspacioAlojamiento(nombre.trim(), ubicacion, capacidad));
    }

    @Override
    public EspacioAlojamiento actualizarCapacidad(String idEspacio, int capacidad) {
        validarCapacidad(capacidad);
        EspacioAlojamiento espacio = obtenerEspacio(idEspacio);
        if (capacidad < espacios.contarAnimalesEn(idEspacio)) {
            throw new ReglaDeNegocioException(
                    "La capacidad no puede ser menor que la ocupación actual");
        }
        espacio.setCapacidad(capacidad);
        return espacios.save(espacio);
    }

    @Override
    public EspacioAlojamiento desactivarEspacio(String idEspacio) {
        EspacioAlojamiento espacio = obtenerEspacio(idEspacio);
        if (espacios.contarAnimalesEn(idEspacio) > 0) {
            throw new ReglaDeNegocioException(
                    "Solo se puede desactivar un espacio sin animales alojados");
        }
        espacio.desactivar();
        return espacios.save(espacio);
    }

    @Override
    public EspacioAlojamiento reactivarEspacio(String idEspacio) {
        EspacioAlojamiento espacio = obtenerEspacio(idEspacio);
        espacio.reactivar();
        return espacios.save(espacio);
    }

    @Override
    public Ocupacion consultarOcupacion(String idEspacio) {
        EspacioAlojamiento espacio = obtenerEspacio(idEspacio);
        long ocupados = espacios.contarAnimalesEn(idEspacio);
        return new Ocupacion(espacio, ocupados, espacio.cuposLibres(ocupados));
    }

    // ------------------------------------------------------------ IEspacios

    @Override
    public boolean tieneCupo(String idEspacio) {
        return obtenerEspacio(idEspacio).tieneCupo(espacios.contarAnimalesEn(idEspacio));
    }

    @Override
    public long ocupacionActual(String idEspacio) {
        obtenerEspacio(idEspacio);
        return espacios.contarAnimalesEn(idEspacio);
    }

    @Override
    public EspacioAlojamiento obtenerEspacio(String idEspacio) {
        return espacios.findById(idEspacio)
                .orElseThrow(() -> new RecursoNoEncontradoException("Espacio de alojamiento no encontrado"));
    }

    private static void validarCapacidad(int capacidad) {
        if (capacidad <= 0) {
            throw new DatoInvalidoException("capacidad",
                    "La capacidad debe ser un número entero mayor que cero");
        }
    }
}
