package com.refugio.config;

import static com.refugio.modelo.Rol.COORDINADOR;
import static com.refugio.modelo.Rol.VETERINARIO;
import static com.refugio.modelo.Rol.VOLUNTARIO;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.refugio.modelo.Rol;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Define qué rol puede llamar a cada ruta, según la columna Rol de la tabla de
 * endpoints del README, y exige sesión en todas menos el inicio de sesión (RNF005).
 *
 * <p>El proyecto depende solo de {@code spring-security-crypto} (para BCrypt),
 * no de Spring Security completo, así que el control se hace con un
 * interceptor de Spring MVC. La tabla {@link #REGLAS} es la matriz de permisos
 * de HU-02 traducida a rutas.
 */
@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    /** Atributo de la petición donde queda el {@link UsuarioAutenticado}. */
    public static final String USUARIO = "refugio.usuario";
    /** Atributo de la petición donde queda el token, para poder revocarlo. */
    public static final String TOKEN = "refugio.token";

    private static final Set<Rol> TODOS = EnumSet.allOf(Rol.class);
    private static final Set<Rol> SOLO_COORDINADOR = EnumSet.of(COORDINADOR);
    private static final Set<Rol> SOLO_VETERINARIO = EnumSet.of(VETERINARIO);
    private static final Set<Rol> SOLO_VOLUNTARIO = EnumSet.of(VOLUNTARIO);

    private record Regla(String metodo, String ruta, Set<Rol> roles) {
    }

    private static final List<Regla> REGLAS = List.of(
            new Regla("POST", "/api/usuarios", SOLO_COORDINADOR),
            new Regla("DELETE", "/api/sesiones/actual", TODOS),

            new Regla("POST", "/api/espacios", SOLO_COORDINADOR),
            new Regla("PATCH", "/api/espacios/{id}", SOLO_COORDINADOR),
            new Regla("POST", "/api/espacios/{id}/desactivacion", SOLO_COORDINADOR),
            new Regla("POST", "/api/espacios/{id}/reactivacion", SOLO_COORDINADOR),
            new Regla("GET", "/api/espacios/{id}/ocupacion", SOLO_COORDINADOR),

            new Regla("POST", "/api/animales", SOLO_COORDINADOR),
            new Regla("PUT", "/api/animales/{id}", SOLO_COORDINADOR),
            new Regla("POST", "/api/animales/{id}/estados", EnumSet.of(COORDINADOR, VETERINARIO)),
            new Regla("POST", "/api/animales/{id}/alojamiento", SOLO_COORDINADOR),
            new Regla("POST", "/api/animales/{id}/responsable", SOLO_COORDINADOR),

            new Regla("POST", "/api/tipos-vacuna", SOLO_VETERINARIO),
            new Regla("POST", "/api/tipos-vacuna/{id}/desactivacion", SOLO_VETERINARIO),
            new Regla("POST", "/api/animales/{id}/vacunaciones", SOLO_VETERINARIO),
            new Regla("POST", "/api/animales/{id}/tratamientos", SOLO_VETERINARIO),
            new Regla("POST", "/api/tratamientos/{id}/finalizacion", SOLO_VETERINARIO),

            new Regla("POST", "/api/adoptantes", SOLO_COORDINADOR),
            new Regla("POST", "/api/solicitudes", SOLO_COORDINADOR),
            new Regla("POST", "/api/solicitudes/{id}/evaluacion", SOLO_COORDINADOR),
            new Regla("POST", "/api/solicitudes/{id}/cancelacion", SOLO_COORDINADOR),
            new Regla("POST", "/api/solicitudes/{id}/adopcion", SOLO_COORDINADOR),
            new Regla("POST", "/api/solicitudes/{id}/devolucion", SOLO_COORDINADOR),
            new Regla("POST", "/api/solicitudes/{id}/visitas", SOLO_COORDINADOR),
            new Regla("POST", "/api/solicitudes/{id}/visitas/{idVisita}/resultado", SOLO_VOLUNTARIO),

            new Regla("GET", "/api/animales", TODOS),
            new Regla("GET", "/api/animales/{id}/ficha", TODOS),
            new Regla("GET", "/api/solicitudes", SOLO_COORDINADOR),
            new Regla("GET", "/api/visitas", SOLO_VOLUNTARIO));

    private final JwtService jwt;

    public SecurityConfig(JwtService jwt) {
        this.jwt = jwt;
    }

    /** BCrypt genera una sal distinta por contraseña (RNF006). */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ControlDeAcceso(jwt))
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/sesiones");
    }

    /**
     * Comprueba la sesión y el rol antes de llegar al controller. Las
     * excepciones que lanza las traduce {@link GlobalExceptionHandler} a 401 y 403.
     */
    private static final class ControlDeAcceso implements HandlerInterceptor {

        private final JwtService jwt;
        private final AntPathMatcher rutas = new AntPathMatcher();

        private ControlDeAcceso(JwtService jwt) {
            this.jwt = jwt;
        }

        @Override
        public boolean preHandle(HttpServletRequest peticion, HttpServletResponse respuesta,
                                 Object handler) {
            String cabecera = peticion.getHeader(HttpHeaders.AUTHORIZATION);
            if (cabecera == null || !cabecera.startsWith("Bearer ")) {
                throw new NoAutenticadoException("Debe iniciar sesión para usar esta función");
            }
            String token = cabecera.substring("Bearer ".length()).trim();
            UsuarioAutenticado usuario = jwt.validar(token);

            String ruta = peticion.getRequestURI().substring(peticion.getContextPath().length());
            REGLAS.stream()
                    .filter(r -> r.metodo().equals(peticion.getMethod()) && rutas.match(r.ruta(), ruta))
                    .findFirst()
                    .filter(r -> !r.roles().contains(usuario.rol()))
                    .ifPresent(r -> {
                        throw new AccesoDenegadoException("No tiene permisos para realizar esta acción");
                    });

            peticion.setAttribute(USUARIO, usuario);
            peticion.setAttribute(TOKEN, token);
            return true;
        }
    }
}
