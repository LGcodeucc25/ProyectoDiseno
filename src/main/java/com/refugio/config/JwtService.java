package com.refugio.config;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.refugio.modelo.Rol;
import com.refugio.modelo.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Emite y verifica el token de sesión (HU-02).
 *
 * <p>Un JWT no se puede "borrar": cerrar sesión (HU-02, criterio 6) exige
 * recordar los tokens revocados hasta que expiren. La lista vive en memoria:
 * basta para una sola instancia de la API, que es el despliegue previsto. Si la
 * API se reinicia, los tokens revocados volverían a valer hasta su expiración.
 */
@Component
public class JwtService {

    private static final String CLAIM_ROL = "rol";
    private static final String CLAIM_NOMBRE = "nombre";

    private final SecretKey clave;
    private final Duration duracion;
    /** jti revocado → instante en que habría expirado. */
    private final Map<String, Instant> revocados = new ConcurrentHashMap<>();

    public JwtService(@Value("${refugio.jwt.secreto}") String secreto,
                      @Value("${refugio.jwt.horas-expiracion}") long horas) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.duracion = Duration.ofHours(horas);
    }

    public String generar(Usuario usuario) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(usuario.getId())
                .claim(CLAIM_ROL, usuario.getRol().name())
                .claim(CLAIM_NOMBRE, usuario.getNombre())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plus(duracion)))
                .signWith(clave)
                .compact();
    }

    /** Devuelve la identidad del token o lanza {@link NoAutenticadoException}. */
    public UsuarioAutenticado validar(String token) {
        Claims claims = leer(token);
        if (revocados.containsKey(claims.getId())) {
            throw new NoAutenticadoException("La sesión fue cerrada. Inicie sesión de nuevo");
        }
        return new UsuarioAutenticado(claims.getSubject(), claims.get(CLAIM_NOMBRE, String.class),
                Rol.valueOf(claims.get(CLAIM_ROL, String.class)));
    }

    public void revocar(String token) {
        Claims claims = leer(token);
        revocados.put(claims.getId(), claims.getExpiration().toInstant());
        Instant ahora = Instant.now();
        revocados.values().removeIf(expira -> expira.isBefore(ahora));
    }

    private Claims leer(String token) {
        try {
            return Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new NoAutenticadoException("Sesión inválida o expirada. Inicie sesión de nuevo");
        }
    }
}
