package com.refugio.negocio;

/**
 * Raíz de las excepciones del dominio.
 *
 * <p>Son reglas de negocio, no infraestructura: viven aquí, junto a los
 * services que las lanzan, y no saben nada de HTTP; solo dicen qué regla se
 * incumplió. La capa de configuración las traduce a códigos de respuesta en un
 * único lugar, mirando hacia este paquete y no al revés.
 *
 * <p>El {@code codigo} es un identificador estable para quien consuma la API;
 * el mensaje es el texto que se le muestra al usuario.
 */
public abstract class ExcepcionDelDominio extends RuntimeException {

    private final String codigo;

    protected ExcepcionDelDominio(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
