package com.refugio.config;

/**
 * Raíz de las excepciones propias del sistema.
 *
 * <p>Cada subclase corresponde a un código HTTP distinto, y la traducción la
 * hace {@link GlobalExceptionHandler} en un único lugar. Los services lanzan
 * estas excepciones sin saber nada de HTTP: solo dicen qué regla se incumplió.
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
