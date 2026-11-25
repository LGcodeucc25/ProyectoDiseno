package com.refugio.config;

/**
 * La operación está bien formada pero contradice una regla del dominio:
 * solicitar un animal que no está disponible, alojar en un espacio sin cupo,
 * registrar un correo que ya existe. Se responde con 409.
 */
public class ReglaDeNegocioException extends ExcepcionDelDominio {

    public ReglaDeNegocioException(String mensaje) {
        super("REGLA_DE_NEGOCIO", mensaje);
    }

    protected ReglaDeNegocioException(String codigo, String mensaje) {
        super(codigo, mensaje);
    }
}
