package com.refugio.negocio;

import java.util.List;

import com.refugio.modelo.Animal;
import com.refugio.modelo.EstadoSolicitud;
import com.refugio.modelo.SolicitudAdopcion;
import com.refugio.presentacion.dto.FichaAnimal;

/**
 * Fachada pública de Consultas y Reportes (HU-07, HU-08, HU-16, HU-19).
 * La consume {@code ConsultaController}.
 *
 * <p>El componente no tiene repositorio ni clases propias: todo lo obtiene de
 * los demás por sus contratos internos.
 */
public interface IConsultas {

    List<Animal> buscarAnimales(IConsultaAnimal.Filtro filtro);

    FichaAnimal fichaDe(String idAnimal);

    List<SolicitudAdopcion> consultarSolicitudes(String idAnimal, String idAdoptante,
                                                 EstadoSolicitud estado);

    List<IConsultaAdopciones.VisitaAsignada> visitasAsignadasA(String idVoluntario);
}
