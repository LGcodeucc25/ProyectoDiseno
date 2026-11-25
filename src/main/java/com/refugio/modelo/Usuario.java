package com.refugio.modelo;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Miembro del equipo del refugio (HU-01, HU-02).
 *
 * <p>El rol es un atributo y no una jerarquía de herencia: los tres roles no
 * tienen atributos propios, solo permisos distintos, y una persona puede
 * cambiar de rol sin dejar de ser el mismo usuario.
 */
@Document(collection = "usuarios")
public class Usuario extends Persona {

    @Indexed(unique = true)
    private String identificacionIndex;

    @Indexed(unique = true)
    private String correoIndex;

    /** Hash BCrypt. Nunca la contraseña en claro (RNF006). */
    @Field("contrasena")
    private String contrasena;

    private Rol rol;

    private boolean activo = true;

    public Usuario() {
    }

    public Usuario(String identificacion, String nombre, String correo, String telefono,
                   String contrasena, Rol rol) {
        super(identificacion, nombre, correo, telefono);
        this.contrasena = contrasena;
        this.rol = rol;
        this.activo = true;
        sincronizarIndices();
    }

    /**
     * Mantiene las copias indexadas de los campos heredados.
     *
     * <p>Los índices únicos de Spring Data se declaran sobre campos de la propia
     * clase, y identificación y correo viven en {@link Persona}. Estas dos copias
     * existen solo para que MongoDB imponga la unicidad que piden HU-01 y RNF009.
     */
    public void sincronizarIndices() {
        this.identificacionIndex = getIdentificacion();
        this.correoIndex = getCorreo();
    }

    /** Verdadero si la clave en claro corresponde al hash guardado. */
    public boolean autenticar(String claveEnClaro,
                              java.util.function.BiPredicate<String, String> comparador) {
        return activo && comparador.test(claveEnClaro, contrasena);
    }

    /** Verdadero si este usuario tiene el rol indicado y su cuenta sigue activa. */
    public boolean tienePermiso(Rol rolRequerido) {
        return activo && this.rol == rolRequerido;
    }

    public boolean esVoluntario() {
        return rol == Rol.VOLUNTARIO;
    }

    public void desactivar() {
        this.activo = false;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getIdentificacionIndex() {
        return identificacionIndex;
    }

    public void setIdentificacionIndex(String identificacionIndex) {
        this.identificacionIndex = identificacionIndex;
    }

    public String getCorreoIndex() {
        return correoIndex;
    }

    public void setCorreoIndex(String correoIndex) {
        this.correoIndex = correoIndex;
    }
}
