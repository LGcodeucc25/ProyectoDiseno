package com.refugio.config;

import com.refugio.modelo.Rol;

/**
 * Identidad de quien hace la petición, extraída del token por
 * {@link SecurityConfig}. Los controllers la reciben como atributo de la
 * petición y pasan su id a los services cuando la operación registra un
 * responsable (quién cambió el estado, quién aplicó la vacuna, quién evaluó).
 *
 * <p>Por eso esa identidad no aparece como conexión en la vista funcional: no
 * es una consulta a otro componente, viene del contexto de la sesión.
 */
public record UsuarioAutenticado(String id, String nombre, Rol rol) {
}
