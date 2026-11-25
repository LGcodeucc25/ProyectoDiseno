package com.refugio.presentacion;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.refugio.config.SecurityConfig;
import com.refugio.negocio.IAccesoUsuarios;
import com.refugio.presentacion.dto.InicioSesionPeticion;
import com.refugio.presentacion.dto.RegistroUsuarioPeticion;
import com.refugio.presentacion.dto.SesionRespuesta;
import com.refugio.presentacion.dto.UsuarioRespuesta;

import jakarta.validation.Valid;

/** Gestión de Usuarios y Acceso (HU-01, HU-02). Depende solo de {@link IAccesoUsuarios}. */
@RestController
@RequestMapping("/api")
public class UsuarioController {

    private final IAccesoUsuarios acceso;

    public UsuarioController(IAccesoUsuarios acceso) {
        this.acceso = acceso;
    }

    @PostMapping("/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioRespuesta registrar(@Valid @RequestBody RegistroUsuarioPeticion p) {
        return UsuarioRespuesta.de(acceso.registrarUsuario(p.aUsuario(), p.contrasena()));
    }

    /** Iniciar sesión crea el recurso "sesión": 201. */
    @PostMapping("/sesiones")
    @ResponseStatus(HttpStatus.CREATED)
    public SesionRespuesta autenticar(@Valid @RequestBody InicioSesionPeticion p) {
        return SesionRespuesta.de(acceso.autenticar(p.correo(), p.contrasena()));
    }

    @DeleteMapping("/sesiones/actual")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cerrarSesion(@RequestAttribute(SecurityConfig.TOKEN) String token) {
        acceso.cerrarSesion(token);
    }
}
