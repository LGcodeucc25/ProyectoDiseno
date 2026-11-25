package com.refugio.negocio;

import com.refugio.modelo.Usuario;

/**
 * Fachada pública de Gestión de Usuarios y Acceso (HU-01, HU-02).
 * La consume {@code UsuarioController}.
 */
public interface IAccesoUsuarios {

    /** Guarda un usuario nuevo con la contraseña convertida en hash BCrypt (RNF006). */
    Usuario registrarUsuario(Usuario nuevo, String claveEnClaro);

    /** Verifica las credenciales y abre una sesión. */
    Sesion autenticar(String correo, String claveEnClaro);

    /** Invalida el token: deja de servir aunque no haya expirado. */
    void cerrarSesion(String token);

    /** Resultado de autenticar: el token y a quién pertenece. */
    record Sesion(String token, Usuario usuario) {
    }
}
