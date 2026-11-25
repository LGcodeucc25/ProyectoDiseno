package com.refugio.config;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.refugio.modelo.Adoptante;
import com.refugio.modelo.Animal;
import com.refugio.modelo.EspacioAlojamiento;
import com.refugio.modelo.Especie;
import com.refugio.modelo.EstadoAnimal;
import com.refugio.modelo.OrigenIngreso;
import com.refugio.modelo.ResultadoEvaluacion;
import com.refugio.modelo.Rol;
import com.refugio.modelo.Sexo;
import com.refugio.modelo.SolicitudAdopcion;
import com.refugio.modelo.TipoVivienda;
import com.refugio.modelo.Usuario;
import com.refugio.negocio.IAccesoUsuarios;
import com.refugio.negocio.IAdministracionEspacios;
import com.refugio.negocio.IAtencionMedica;
import com.refugio.negocio.IGestionAdopciones;
import com.refugio.negocio.IGestionAnimales;

/**
 * Carga datos de demostración al arrancar, solo si la base está vacía.
 *
 * <p>Todo entra por las fachadas públicas, igual que entraría por la API: las
 * contraseñas quedan en BCrypt y los estados del animal se alcanzan por
 * transiciones válidas, con su historial. Así los datos de ejemplo cumplen las
 * mismas reglas que los datos reales.
 *
 * <p>Las credenciales están documentadas en el README (contraseña común
 * {@value #CLAVE}).
 */
@Component
public class DatosDeEjemplo implements CommandLineRunner {

    static final String CLAVE = "Refugio2026";

    private static final Logger LOG = LoggerFactory.getLogger(DatosDeEjemplo.class);
    private static final List<String> COLECCIONES = List.of("usuarios", "adoptantes", "espacios",
            "animales", "tiposVacuna", "vacunaciones", "tratamientos", "solicitudes");

    private final MongoTemplate mongo;
    private final IAccesoUsuarios acceso;
    private final IAdministracionEspacios espacios;
    private final IAtencionMedica atencion;
    private final IGestionAnimales animales;
    private final IGestionAdopciones adopciones;

    public DatosDeEjemplo(MongoTemplate mongo, IAccesoUsuarios acceso, IAdministracionEspacios espacios,
                          IAtencionMedica atencion, IGestionAnimales animales,
                          IGestionAdopciones adopciones) {
        this.mongo = mongo;
        this.acceso = acceso;
        this.espacios = espacios;
        this.atencion = atencion;
        this.animales = animales;
        this.adopciones = adopciones;
    }

    @Override
    public void run(String... args) {
        boolean vacia = COLECCIONES.stream().allMatch(c -> mongo.getCollection(c).countDocuments() == 0);
        if (!vacia) {
            LOG.info("La base ya tiene datos: no se cargan datos de ejemplo");
            return;
        }
        cargar();
        LOG.info("Datos de ejemplo cargados. Usuarios: ana@refugio.org (coordinadora), "
                + "luis@refugio.org (veterinario), marta@refugio.org (voluntaria); contraseña {}", CLAVE);
    }

    private void cargar() {
        LocalDate hoy = LocalDate.now();

        // --- 3 usuarios, uno por rol (HU-01) ---
        Usuario ana = acceso.registrarUsuario(new Usuario("1001", "Ana Gómez", "ana@refugio.org",
                "3001110001", null, Rol.COORDINADOR), CLAVE);
        Usuario luis = acceso.registrarUsuario(new Usuario("1002", "Luis Pérez", "luis@refugio.org",
                "3001110002", null, Rol.VETERINARIO), CLAVE);
        Usuario marta = acceso.registrarUsuario(new Usuario("1003", "Marta Ríos", "marta@refugio.org",
                "3001110003", null, Rol.VOLUNTARIO), CLAVE);

        // --- 4 espacios (HU-06) ---
        EspacioAlojamiento canilesA = espacios.crearEspacio("Caniles A", "Perros medianos", 5);
        EspacioAlojamiento canilesB = espacios.crearEspacio("Caniles B", "Perros grandes", 4);
        EspacioAlojamiento gatera = espacios.crearEspacio("Gatera 1", "Gatos", 6);
        EspacioAlojamiento cuarentena = espacios.crearEspacio("Cuarentena", "Aislamiento sanitario", 2);

        // --- 4 tipos de vacuna (HU-09) ---
        String rabia = atencion.crearTipoVacuna("Rabia", "Vacuna antirrábica anual").getId();
        atencion.crearTipoVacuna("Parvovirus", "Parvovirus canino");
        atencion.crearTipoVacuna("Moquillo", "Distemper canino");
        atencion.crearTipoVacuna("Triple felina", "Rinotraqueítis, calicivirus y panleucopenia");

        // --- 6 animales: todos entran En valoración y se mueven por transiciones válidas ---
        Animal toby = ingresar("Toby", Especie.PERRO, "Criollo", Sexo.MACHO, "2 años",
                hoy.minusDays(40), OrigenIngreso.RESCATE_VIA_PUBLICA, "Encontrado con herida en la pata", ana);
        Animal luna = ingresar("Luna", Especie.PERRO, "Labrador mestizo", Sexo.HEMBRA, "5 años",
                hoy.minusDays(3), OrigenIngreso.ENTREGA_VOLUNTARIA, "Llega con dermatitis", ana);
        Animal misu = ingresar("Misu", Especie.GATO, "Criollo", Sexo.HEMBRA, "1 año",
                hoy.minusDays(30), OrigenIngreso.RESCATE_VIA_PUBLICA, null, ana);
        Animal rocky = ingresar("Rocky", Especie.PERRO, "Pastor alemán", Sexo.MACHO, "4 años",
                hoy.minusDays(25), OrigenIngreso.REMISION_AUTORIDAD, null, ana);
        Animal nala = ingresar("Nala", Especie.GATO, "Siamés", Sexo.HEMBRA, "3 años",
                hoy.minusDays(90), OrigenIngreso.ENTREGA_VOLUNTARIA, null, ana);
        Animal rex = ingresar("Rex", Especie.PERRO, "Criollo", Sexo.MACHO, "10 años",
                hoy.minusDays(60), OrigenIngreso.RESCATE_VIA_PUBLICA, null, ana);

        animales.asignarAlojamiento(toby.getId(), canilesA.getId(), hoy.minusDays(40));
        animales.asignarAlojamiento(luna.getId(), cuarentena.getId(), hoy.minusDays(3));
        animales.asignarAlojamiento(misu.getId(), gatera.getId(), hoy.minusDays(30));
        animales.asignarAlojamiento(rocky.getId(), canilesB.getId(), hoy.minusDays(25));
        animales.asignarAlojamiento(nala.getId(), gatera.getId(), hoy.minusDays(90));
        animales.asignarVoluntario(toby.getId(), marta.getId(), hoy.minusDays(35));

        String vet = luis.getId();
        atencion.registrarVacunacion(toby.getId(), rabia, hoy.minusDays(35), vet);
        atencion.registrarTratamiento(luna.getId(), "Dermatitis", hoy.minusDays(2), null,
                "Baño medicado cada 3 días", vet);

        // Toby y Misu: Disponibles. Luna y Rocky siguen En valoración.
        animales.cambiarEstado(toby.getId(), EstadoAnimal.DISPONIBLE, hoy.minusDays(30),
                "Valoración médica apta", vet);
        animales.cambiarEstado(misu.getId(), EstadoAnimal.DISPONIBLE, hoy.minusDays(20),
                "Valoración médica apta", vet);
        animales.cambiarEstado(nala.getId(), EstadoAnimal.DISPONIBLE, hoy.minusDays(80),
                "Valoración médica apta", vet);
        animales.cambiarEstado(rex.getId(), EstadoAnimal.NO_DISPONIBLE, hoy.minusDays(10),
                "Traslado a otro refugio", ana.getId());

        // --- 3 adoptantes (HU-12) ---
        Adoptante carlos = adopciones.registrarAdoptante(new Adoptante("52123456", "Carlos Ruiz",
                "carlos@correo.com", "3001234567", "Calle 10 # 5-20", TipoVivienda.CASA, true));
        Adoptante anaTorres = adopciones.registrarAdoptante(new Adoptante("41987654", "Ana Torres",
                "ana.torres@correo.com", "3109876543", "Carrera 7 # 45-10", TipoVivienda.APARTAMENTO,
                false));
        adopciones.registrarAdoptante(new Adoptante("79555111", "Pedro Díaz", null, "3205550000",
                "Vereda El Placer, finca La Esperanza", TipoVivienda.FINCA, true));

        // --- 2 solicitudes ---
        // 1) Carlos adoptó a Nala: aprobada, entregada y con una visita programada para Marta.
        SolicitudAdopcion deNala = adopciones.registrarSolicitud(nala.getId(), carlos.getId(),
                hoy.minusDays(70));
        adopciones.evaluarSolicitud(deNala.getId(), ResultadoEvaluacion.APROBADA, hoy.minusDays(65),
                "Hogar adecuado y experiencia previa con gatos", ana.getId());
        adopciones.registrarAdopcion(deNala.getId(), hoy.minusDays(60));
        adopciones.programarVisita(deNala.getId(), hoy.plusDays(7), marta.getId(),
                "Revisar adaptación y peso");

        // 2) Ana Torres pidió a Toby: queda Pendiente para evaluarla en la demostración.
        adopciones.registrarSolicitud(toby.getId(), anaTorres.getId(), hoy.minusDays(2));
    }

    private Animal ingresar(String nombre, Especie especie, String raza, Sexo sexo, String edad,
                            LocalDate ingreso, OrigenIngreso origen, String descripcion,
                            Usuario quien) {
        return animales.registrarAnimal(
                new Animal(nombre, especie, raza, sexo, edad, ingreso, origen, descripcion),
                quien.getId());
    }
}
