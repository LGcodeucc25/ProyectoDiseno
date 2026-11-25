package com.refugio.datos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.refugio.modelo.Rol;
import com.refugio.modelo.Usuario;

/** Colección {@code usuarios}. Las búsquedas van por las copias indexadas de Usuario. */
public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByCorreoIndex(String correo);

    boolean existsByCorreoIndex(String correo);

    boolean existsByIdentificacionIndex(String identificacion);

    List<Usuario> findByRolAndActivoTrue(Rol rol);
}
