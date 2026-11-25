package com.refugio.config;

/** Hay sesión, pero el rol o la identidad del usuario no le permiten esta acción (HU-02). 403. */
public class AccesoDenegadoException extends ExcepcionDelDominio {

    public AccesoDenegadoException(String mensaje) {
        super("ACCESO_DENEGADO", mensaje);
    }
}
