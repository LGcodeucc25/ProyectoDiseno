package com.refugio.negocio;

import java.util.List;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.refugio.config.JwtService;
import com.refugio.config.NoAutenticadoException;
import com.refugio.config.ReglaDeNegocioException;
import com.refugio.datos.UsuarioRepository;
import com.refugio.modelo.Rol;
import com.refugio.modelo.Usuario;

/**
 * Gestión de Usuarios y Acceso. Proporciona la fachada {@link IAccesoUsuarios}
 * y el contrato interno {@link IConsultaUsuario}.
 */
@Service
public class UsuarioService implements IAccesoUsuarios, IConsultaUsuario {

    /** Mismo mensaje para correo inexistente y clave errónea: no revela qué correos existen (HU-02). */
    private static final String CREDENCIALES_INCORRECTAS = "Correo o contraseña incorrectos";

    private final UsuarioRepository usuarios;
    private final PasswordEncoder codificador;
    private final JwtService jwt;

    public UsuarioService(UsuarioRepository usuarios, PasswordEncoder codificador, JwtService jwt) {
        this.usuarios = usuarios;
        this.codificador = codificador;
        this.jwt = jwt;
    }

    // ------------------------------------------------------ IAccesoUsuarios

    @Override
    public Usuario registrarUsuario(Usuario nuevo, String claveEnClaro) {
        String correo = normalizarCorreo(nuevo.getCorreo());
        nuevo.setCorreo(correo);
        if (usuarios.existsByCorreoIndex(correo)) {
            throw new ReglaDeNegocioException("Ya existe un usuario con ese correo electrónico");
        }
        if (usuarios.existsByIdentificacionIndex(nuevo.getIdentificacion())) {
            throw new ReglaDeNegocioException("Ya existe un usuario con esa identificación");
        }
        nuevo.setId(null);
        nuevo.setActivo(true);
        nuevo.setContrasena(codificador.encode(claveEnClaro));
        nuevo.sincronizarIndices();
        return usuarios.save(nuevo);
    }

    @Override
    public Sesion autenticar(String correo, String claveEnClaro) {
        Usuario usuario = usuarios.findByCorreoIndex(normalizarCorreo(correo))
                .orElseThrow(() -> new NoAutenticadoException(CREDENCIALES_INCORRECTAS));
        // El dominio recibe el comparador para no depender de Spring Security.
        if (!usuario.autenticar(claveEnClaro, codificador::matches)) {
            throw new NoAutenticadoException(CREDENCIALES_INCORRECTAS);
        }
        return new Sesion(jwt.generar(usuario), usuario);
    }

    @Override
    public void cerrarSesion(String token) {
        jwt.revocar(token);
    }

    // ----------------------------------------------------- IConsultaUsuario

    @Override
    public boolean existeConRol(String idUsuario, Rol rol) {
        if (idUsuario == null) {
            return false;
        }
        return usuarios.findById(idUsuario)
                .map(u -> u.tienePermiso(rol))
                .orElse(false);
    }

    @Override
    public List<Usuario> listarPorRol(Rol rol) {
        return usuarios.findByRolAndActivoTrue(rol);
    }

    private static String normalizarCorreo(String correo) {
        return correo == null ? null : correo.trim().toLowerCase(Locale.ROOT);
    }
}
