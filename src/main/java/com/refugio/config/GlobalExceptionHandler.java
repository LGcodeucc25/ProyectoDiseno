package com.refugio.config;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Traduce las excepciones a códigos HTTP en un solo lugar:
 * 400 entrada mal formada, 401 sin sesión, 403 sin permiso, 404 no existe,
 * 409 contradice una regla del dominio, 500 fallo no previsto.
 *
 * <p>El cuerpo siempre tiene la misma forma, y cuando el error es de un campo
 * lo nombra (RNF002).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Cuerpo de toda respuesta de error. {@code campos} solo aparece si hay errores por campo. */
    public record ErrorRespuesta(int estado, String codigo, String mensaje,
                                 Map<String, String> campos, Instant fecha) {
    }

    // --------------------------------------------------- excepciones propias

    @ExceptionHandler(DatoInvalidoException.class)
    public ResponseEntity<ErrorRespuesta> datoInvalido(DatoInvalidoException e) {
        Map<String, String> campos = e.getCampo() == null ? null : Map.of(e.getCampo(), e.getMessage());
        return responder(HttpStatus.BAD_REQUEST, e.getCodigo(), e.getMessage(), campos);
    }

    @ExceptionHandler(NoAutenticadoException.class)
    public ResponseEntity<ErrorRespuesta> noAutenticado(NoAutenticadoException e) {
        return responder(HttpStatus.UNAUTHORIZED, e.getCodigo(), e.getMessage(), null);
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<ErrorRespuesta> accesoDenegado(AccesoDenegadoException e) {
        return responder(HttpStatus.FORBIDDEN, e.getCodigo(), e.getMessage(), null);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorRespuesta> noEncontrado(RecursoNoEncontradoException e) {
        return responder(HttpStatus.NOT_FOUND, e.getCodigo(), e.getMessage(), null);
    }

    /** Incluye las transiciones no permitidas y las reservadas, cada una con su código. */
    @ExceptionHandler(ReglaDeNegocioException.class)
    public ResponseEntity<ErrorRespuesta> reglaDeNegocio(ReglaDeNegocioException e) {
        return responder(HttpStatus.CONFLICT, e.getCodigo(), e.getMessage(), null);
    }

    // ------------------------------------------------ entrada mal formada

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRespuesta> validacion(MethodArgumentNotValidException e) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(error.getField(), mensajeDeCampo(error));
        }
        String mensaje = campos.size() == 1
                ? campos.values().iterator().next()
                : "Hay campos con errores";
        return responder(HttpStatus.BAD_REQUEST, "ENTRADA_INVALIDA", mensaje, campos);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorRespuesta> cuerpoIlegible(HttpMessageNotReadableException e) {
        return responder(HttpStatus.BAD_REQUEST, "ENTRADA_INVALIDA",
                "El cuerpo de la petición no es un JSON válido o tiene un valor con formato incorrecto "
                        + "(fechas como AAAA-MM-DD; enumeraciones con sus valores exactos)", null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorRespuesta> parametroInvalido(MethodArgumentTypeMismatchException e) {
        return responder(HttpStatus.BAD_REQUEST, "ENTRADA_INVALIDA",
                "El parámetro '" + e.getName() + "' tiene un valor no válido: " + e.getValue(),
                Map.of(e.getName(), "valor no válido"));
    }

    // ------------------------------------------------------------ otros

    /** Respaldo de los índices únicos de MongoDB si dos registros llegan a la vez. */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorRespuesta> duplicado(DuplicateKeyException e) {
        return responder(HttpStatus.CONFLICT, "DUPLICADO",
                "Ya existe un registro con esos datos", null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorRespuesta> rutaInexistente(NoResourceFoundException e) {
        return responder(HttpStatus.NOT_FOUND, "NO_ENCONTRADO", "La ruta no existe", null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorRespuesta> metodoNoSoportado(HttpRequestMethodNotSupportedException e) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED, "METODO_NO_PERMITIDO",
                "Método " + e.getMethod() + " no admitido en esta ruta", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRespuesta> inesperado(Exception e) {
        LOG.error("Error no previsto", e);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_INTERNO",
                "Ocurrió un error no previsto", null);
    }

    private static String mensajeDeCampo(FieldError error) {
        String codigo = error.getCode() == null ? "" : error.getCode();
        return switch (codigo) {
            case "NotBlank", "NotNull", "NotEmpty" -> "El campo '" + error.getField() + "' es obligatorio";
            case "Email" -> "El correo electrónico no tiene un formato válido";
            case "Positive" -> "El campo '" + error.getField() + "' debe ser un número entero mayor que cero";
            default -> "El campo '" + error.getField() + "' no es válido";
        };
    }

    private static ResponseEntity<ErrorRespuesta> responder(HttpStatus estado, String codigo,
                                                            String mensaje, Map<String, String> campos) {
        return ResponseEntity.status(estado)
                .body(new ErrorRespuesta(estado.value(), codigo, mensaje, campos, Instant.now()));
    }
}
