package com.refugio.negocio;

import java.util.List;

import com.refugio.modelo.Rol;
import com.refugio.modelo.Usuario;

/**
 * Contrato interno de Gestión de Usuarios y Acceso.
 *
 * <p>Lo requieren Gestión de Animales, para el voluntario responsable de un
 * animal (HU-21), y Gestión de Adopciones, para el voluntario asignado a una
 * visita (HU-18). Es la misma regla sobre la misma entidad en los dos casos.
 */
public interface IConsultaUsuario {

    /** Verdadero si el usuario existe, está activo y tiene ese rol. */
    boolean existeConRol(String idUsuario, Rol rol);

    /** Usuarios activos con ese rol, para ofrecer la lista de voluntarios. */
    List<Usuario> listarPorRol(Rol rol);
}
