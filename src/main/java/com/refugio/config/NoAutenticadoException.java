package com.refugio.config;

/** No hay sesión válida: falta el token, expiró, fue revocado o las credenciales no coinciden. 401. */
public class NoAutenticadoException extends ExcepcionDelDominio {

    public NoAutenticadoException(String mensaje) {
        super("NO_AUTENTICADO", mensaje);
    }
}
