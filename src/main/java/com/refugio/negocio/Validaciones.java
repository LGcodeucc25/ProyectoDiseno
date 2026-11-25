package com.refugio.negocio;

import java.time.LocalDate;

import com.refugio.config.DatoInvalidoException;

/**
 * Reglas de valor que se repiten en varios casos de uso: fechas que no pueden
 * ser futuras ni anteriores a otra, y textos obligatorios. El formato de la
 * entrada lo valida la capa de presentación; esto es lo que depende del dominio.
 */
final class Validaciones {

    private Validaciones() {
    }

    static void noFutura(LocalDate fecha, String campo, String mensaje) {
        if (fecha != null && fecha.isAfter(LocalDate.now())) {
            throw new DatoInvalidoException(campo, mensaje);
        }
    }

    static void noAnterior(LocalDate fecha, LocalDate referencia, String campo, String mensaje) {
        if (fecha != null && referencia != null && fecha.isBefore(referencia)) {
            throw new DatoInvalidoException(campo, mensaje);
        }
    }

    static void obligatorio(String valor, String campo, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new DatoInvalidoException(campo, mensaje);
        }
    }
}
