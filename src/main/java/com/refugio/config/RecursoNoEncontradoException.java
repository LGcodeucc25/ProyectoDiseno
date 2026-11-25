package com.refugio.config;

/** El recurso pedido no existe. Se responde con 404. */
public class RecursoNoEncontradoException extends ExcepcionDelDominio {

    public RecursoNoEncontradoException(String mensaje) {
        super("NO_ENCONTRADO", mensaje);
    }
}
