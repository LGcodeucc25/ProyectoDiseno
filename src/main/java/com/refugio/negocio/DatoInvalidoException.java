package com.refugio.negocio;

/**
 * Un dato de entrada tiene un valor inaceptable aunque el formato sea correcto:
 * una fecha futura, una fecha de fin anterior a la de inicio, un motivo vacío
 * cuando la regla lo exige. Se responde con 400.
 */
public class DatoInvalidoException extends ExcepcionDelDominio {

    private final String campo;

    public DatoInvalidoException(String campo, String mensaje) {
        super("DATO_INVALIDO", mensaje);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
