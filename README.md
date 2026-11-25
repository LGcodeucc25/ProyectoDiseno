# Sistema de gestión para un centro de rescate y adopción de mascotas

Proyecto final de Diseño de Software. Backend REST en **Java 21 + Spring Boot 3 + MongoDB**, diseñado sobre cinco vistas de arquitectura.

| | |
|---|---|
| Diagramas | [`docs/diagramas/`](docs/diagramas) — un `.vpp` de Visual Paradigm y su `.png` por vista |
| Mockups | [`docs/mockups/`](docs/mockups) — 21 pantallas, una por historia de usuario |
| Backlog | [Issues](../../issues) — 21 historias con validación INVEST y criterios en Gherkin |
| Código | [`src/main/java/com/refugio/`](src/main/java/com/refugio) |

**Índice** · [El problema](#el-problema) · [Alcance](#alcance) · [Vistas de arquitectura](#vistas-de-arquitectura) · [Decisiones de diseño](#decisiones-de-diseño) · [Requisitos](#requisitos) · [El backend](#el-backend) · [Cómo correrlo](#cómo-correrlo) · [Pendientes](#pendientes)

---

## El problema

Los refugios de animales operan sin trazabilidad: no se sabe con certeza en qué estado se encuentra cada animal, quién lo atendió, cuántas solicitudes de adopción ha recibido ni qué ocurrió con él después de ser entregado. Esa falta de registro dificulta la operación diaria y favorece las adopciones fallidas.

El sistema cubre el ciclo completo del animal dentro del refugio: el registro de su ingreso y el origen del rescate, su ubicación en los espacios de alojamiento, el control de su estado a lo largo del tiempo, el historial médico de vacunas y tratamientos, el proceso de adopción desde la solicitud hasta la entrega o la devolución, y las visitas de seguimiento posteriores.

### Roles

| Rol | Qué hace |
|---|---|
| Coordinador | Administra animales, espacios, adoptantes y el proceso de adopción completo, incluidas la evaluación de solicitudes, la entrega, la devolución y la programación de visitas |
| Veterinario | Registra vacunas y tratamientos y administra el catálogo de tipos de vacuna |
| Voluntario | Consulta los animales que tiene a cargo y registra el resultado de las visitas que se le programan |

El adoptante no opera el sistema: sus datos y sus solicitudes los registra el coordinador en su nombre.

## Alcance

El entregable es la **API** (`refugio-api.jar`). La interfaz web que aparece en la vista de despliegue no se construye: está representada por los mockups de [`docs/mockups/`](docs/mockups). Las consultas se prueban con Postman.

En producción el HTTPS lo pone el servidor donde se publica la API; en local corre en HTTP por el puerto 8080.

**Fuera de alcance:** integraciones con sistemas externos, notificaciones por correo, gestión de donaciones y pagos, recomendación automática entre adoptante y animal, historial de ubicaciones y de asignaciones de voluntario, y reportes estadísticos. Los reportes se descartaron porque un reporte es información derivada y no aporta conceptos al modelo de dominio.

---

# Vistas de arquitectura

## 1. Vista de contexto

Delimita la frontera del sistema: quién intercambia información con él y qué información es. El sistema se representa como un único proceso, sin detallar su interior.

Diagrama: [`vista-contexto.vpp`](docs/diagramas/vista-contexto.vpp) (fuente de Visual Paradigm) · [`vista-contexto.png`](docs/diagramas/vista-contexto.png) (imagen)

**Proceso 0:** Sistema de Gestión del Refugio.

| Entidad externa | Quién es | Historias en las que actúa |
|---|---|---|
| Coordinador | Rol administrativo del refugio. Registra animales, adoptantes y el proceso de adopción completo. | HU-01, HU-03 a HU-06, HU-12 a HU-18, HU-20, HU-21 |
| Veterinario | Responsable de la atención médica de los animales. | HU-04, HU-09, HU-10, HU-11 |
| Voluntario | Cuida animales asignados y realiza las visitas de seguimiento. | HU-19, HU-21 |

| # | Origen | Destino | Flujo |
|---|---|---|---|
| 1 | Coordinador | Sistema | Datos de animales, adoptantes y solicitudes |
| 2 | Sistema | Coordinador | Fichas de animales, espacios y estado de solicitudes |
| 3 | Veterinario | Sistema | Vacunas y tratamientos |
| 4 | Sistema | Veterinario | Historial médico del animal |
| 5 | Voluntario | Sistema | Resultado de las visitas |
| 6 | Sistema | Voluntario | Visitas programadas y animales a cargo |

Los flujos del nivel 0 son agregados por definición: se desglosan al descomponer el proceso en el nivel 1. Por eso cada actor tiene un flujo por sentido y no uno por tipo de dato.

**Decisiones de esta vista**

- **No hay entidades externas de software.** El alcance excluye integraciones con terceros: no hay correo, pasarela de pagos, ni conexión con entidades veterinarias o municipales. Un diagrama de contexto sin sistemas externos es un resultado válido, no un diagrama incompleto.
- **El adoptante no es una entidad externa.** Es el beneficiario del proceso, pero no entrega ni recibe datos directamente del sistema: sus datos los registra el coordinador (HU-12) y sus solicitudes también (HU-13). Una entidad externa es la que intercambia datos con el sistema, y el adoptante no lo hace.
- **No aparecen almacenes de datos.** Los almacenes pertenecen al nivel 1 de descomposición, no al diagrama de contexto.

*Notación:* diagrama de flujo de datos en notación Yourdon — el proceso es un círculo, las entidades externas son rectángulos y los flujos son flechas dirigidas con nombre. Cada sentido lleva su propia flecha, porque la información que entra y la que sale son distintas.

## 2. Vista funcional

Agrupa las funcionalidades en componentes con responsabilidades diferenciadas y expresa su colaboración mediante interfaces proporcionadas y requeridas. No describe clases ni despliegue.

Diagrama: [`vista-funcional.vpp`](docs/diagramas/vista-funcional.vpp) (fuente de Visual Paradigm) · [`vista-funcional.png`](docs/diagramas/vista-funcional.png) (imagen)

| Componente | Responsabilidad | Clases | Historias |
|---|---|---|---|
| Gestión de Usuarios y Acceso | Administrar las cuentas del equipo y controlar qué puede hacer cada rol | `Persona`, `Usuario` | HU-01, HU-02 |
| Gestión de Alojamiento | Administrar los espacios del refugio y su capacidad disponible | `EspacioAlojamiento` | HU-06 |
| Gestión de Animales | Mantener los datos del animal, su estado, su ubicación y su cuidador responsable | `Animal`, `RegistroEstado` | HU-03, HU-04, HU-05, HU-21 |
| Atención Médica | Llevar el catálogo de vacunas y el historial sanitario de cada animal | `TipoVacuna`, `Vacunacion`, `Tratamiento` | HU-09, HU-10, HU-11 |
| Gestión de Adopciones | Conducir el proceso completo: adoptante, solicitud, entrega, devolución y visitas de seguimiento | `Adoptante`, `SolicitudAdopcion`, `Adopcion`, `VisitaSeguimiento` | HU-12 a HU-20 |
| Consultas y Reportes | Consolidar y presentar información que proviene de varios componentes | — | HU-07, HU-08, HU-19 |

### Interfaces públicas

Cada componente ofrece **una sola** fachada hacia la capa de presentación. En el diagrama se dibujan como bola sin semicírculo.

| Interfaz | La proporciona | Operaciones |
|---|---|---|
| `IAccesoUsuarios` | Gestión de Usuarios y Acceso | `registrarUsuario`, `autenticar`, `cerrarSesion` |
| `IAdministracionEspacios` | Gestión de Alojamiento | `crearEspacio`, `actualizarCapacidad`, `desactivarEspacio`, `reactivarEspacio`, `consultarOcupacion` |
| `IGestionAnimales` | Gestión de Animales | `registrarAnimal`, `actualizarAnimal`, `cambiarEstado`, `asignarAlojamiento`, `asignarVoluntario` |
| `IAtencionMedica` | Atención Médica | `crearTipoVacuna`, `desactivarTipoVacuna`, `registrarVacunacion`, `registrarTratamiento`, `finalizarTratamiento` |
| `IGestionAdopciones` | Gestión de Adopciones | `registrarAdoptante`, `registrarSolicitud`, `evaluarSolicitud`, `cancelarSolicitud`, `registrarAdopcion`, `registrarDevolucion`, `programarVisita`, `registrarResultadoVisita` |
| `IConsultas` | Consultas y Reportes | `buscarAnimales`, `fichaDe`, `consultarSolicitudes`, `visitasAsignadasA` |

### Interfaces internas y conexiones de ensamblaje

Son los contratos entre componentes. Cada una se dibuja con una sola bola en el proveedor y tantos semicírculos como consumidores tenga.

| Interfaz | La proporciona | La requieren | Operaciones | Por qué se necesita |
|---|---|---|---|---|
| `IConsultaUsuario` | Usuarios y Acceso | Gestión de Animales | `existeConRol`, `listarPorRol` | Al asignar el cuidador de un animal hay que ofrecer la lista de voluntarios y verificar que el elegido tenga ese rol |
| `IEspacios` | Gestión de Alojamiento | Gestión de Animales | `tieneCupo`, `ocupar`, `liberar` | Comprobar el cupo antes de alojar un animal y liberar el espacio al adoptarse |
| `IConsultaAnimal` | Gestión de Animales | Atención Médica, Gestión de Adopciones, Consultas y Reportes | `obtenerAnimal`, `estadoActual`, `estaDisponible`, `listar` | Atención Médica no vacuna a un animal *No disponible*; Adopciones no acepta solicitudes sobre uno que no esté *Disponible*; Consultas arma la ficha |
| `ITransicionEstado` | Gestión de Animales | Gestión de Adopciones | `marcarEnProcesoAdopcion`, `marcarAdoptado`, `marcarEnValoracion`, `marcarDisponible` | Los cambios de estado automáticos al aprobar, entregar, cancelar o recibir una devolución |
| `IBajaSolicitudes` | Gestión de Adopciones | Gestión de Animales | `rechazarPendientesDe` | Cuando un animal deja de estar *Disponible* (HU-04), sus solicitudes pendientes se rechazan en ese mismo momento |
| `IHistorialMedico` | Atención Médica | Consultas y Reportes | `vacunasDe`, `tratamientosDe`, `tieneTratamientoActivo` | La ficha integral muestra el historial sanitario |
| `IConsultaAdopciones` | Gestión de Adopciones | Consultas y Reportes | `solicitudesDe`, `adopcionDe`, `visitasDe`, `visitasAsignadasA` | La ficha integral muestra solicitudes, adopción y visitas; el voluntario consulta las suyas |

Siete interfaces internas y nueve conexiones de ensamblaje. En total **trece interfaces**: seis fachadas públicas y siete contratos internos.

| # | Cliente | Interfaz | Nombre de la conexión |
|---|---|---|---|
| 1 | Consultas y Reportes | `IHistorialMedico` | obtiene el historial médico |
| 2 | Consultas y Reportes | `IConsultaAdopciones` | obtiene solicitudes y visitas |
| 3 | Consultas y Reportes | `IConsultaAnimal` | obtiene los datos del animal |
| 4 | Atención Médica | `IConsultaAnimal` | verifica el estado antes de vacunar |
| 5 | Gestión de Adopciones | `IConsultaAnimal` | verifica que el animal esté disponible |
| 6 | Gestión de Adopciones | `ITransicionEstado` | solicita el cambio de estado automático |
| 7 | Gestión de Animales | `IEspacios` | verifica cupo y ocupa espacio |
| 8 | Gestión de Animales | `IConsultaUsuario` | valida el rol del voluntario |
| 9 | Gestión de Animales | `IBajaSolicitudes` | rechaza las solicitudes pendientes |

Las conexiones 3, 4 y 5 comparten la misma interfaz, así que comparten una sola bola: las tres líneas llegan a un tronco común y de ahí sale un único semicírculo. Dibujar tres bolas con el mismo nombre significaría que Gestión de Animales realiza tres veces el mismo contrato.

**Decisiones de esta vista**

- **Toda colaboración se expresa como interfaz, no como dependencia.** Una dependencia dice que un componente necesita a otro; una interfaz dice *qué* le pide y con qué firma. Eso es lo que permite reemplazar la implementación de un componente sin tocar a sus clientes. En el diagrama no hay ninguna flecha discontinua.
- **Una sola fachada pública por componente.** La primera versión tenía trece interfaces públicas, una por grupo de historias: eso era el backlog reescrito en forma de interfaz, no información de diseño. El criterio aplicado fue segregar por **tipo de cliente**, no por caso de uso. La capa de presentación es un único cliente de cada componente, así que recibe una fachada; los componentes internos reciben un contrato propio solo cuando lo que necesitan es distinto.
- **`IConsultaAnimal` e `ITransicionEstado` están separadas a propósito.** Ambas las proporciona Gestión de Animales, pero tienen clientes con derechos distintos: la primera solo lee y la consumen tres componentes; la segunda modifica el estado y solo la consume Gestión de Adopciones. Si fueran una sola, Consultas y Reportes, que es de solo lectura, quedaría con la capacidad de cambiar el estado de un animal. Es el mismo criterio que separa `IConsultaAdopciones` de `IBajaSolicitudes`.
- **Hay un ciclo aceptado entre Gestión de Animales y Gestión de Adopciones**, y es deliberado. HU-04 exige que al dejar un animal de estar *Disponible* sus solicitudes pendientes se rechacen de inmediato; HU-14 exige lo inverso, que al aprobarse una solicitud el animal pase a *En proceso de adopción*. Son dos reglas de negocio simétricas, así que la dependencia es mutua en el dominio, no un defecto del diseño. Se expresa con dos interfaces distintas y estrechas (`ITransicionEstado` e `IBajaSolicitudes`), cada una con un solo consumidor, en lugar de un contrato ancho compartido. La alternativa —orquestar las dos llamadas desde el controller— habría movido una regla de negocio a la capa de presentación.
- **Seguimiento Posadopción se fusionó dentro de Gestión de Adopciones.** Tenía una sola clase, dos historias y su única colaboración era con Adopciones. Las visitas de seguimiento son parte del ciclo de vida de la adopción, no una responsabilidad separada.
- **La identidad del usuario de la sesión no se dibuja.** Atención Médica y Gestión de Adopciones registran quién aplicó una vacuna o quién evaluó una solicitud, pero ese dato viene del contexto de la sesión, no de una consulta a otro componente. El único caso en que sí hay consulta es la asignación del voluntario responsable, donde hay que ofrecer la lista de usuarios con ese rol.
- **No existe un componente de interfaz de usuario ni de persistencia.** El alcance de esta vista es la distribución de responsabilidades funcionales; la presentación y el almacenamiento corresponden a la vista de desarrollo y a la de despliegue.

## 3. Vista lógica

Diagrama de clases. Especifica las clases que implementan las responsabilidades de la vista funcional, con sus atributos, operaciones y relaciones.

Diagrama: [`vista-logica.vpp`](docs/diagramas/vista-logica.vpp) (fuente de Visual Paradigm) · [`vista-logica.png`](docs/diagramas/vista-logica.png) (imagen)

Notación: `-` privado, `+` público. Las clases abstractas van en cursiva. Se omiten los constructores y los métodos `get` y `set`.

### Personas

| Clase | Atributos | Operaciones |
|---|---|---|
| `Persona` *{abstract}* | `identificacion`, `nombre`, `correo`, `telefono` | — |
| `Usuario` ▷ `Persona` | `contrasena`, `rol : Rol`, `activo` | `autenticar(clave) : boolean`, `tienePermiso(funcion) : boolean` |
| `Adoptante` ▷ `Persona` | `direccion`, `tipoVivienda : TipoVivienda`, `experienciaPrevia` | — |

### Animal y su historial

| Clase | Atributos | Operaciones |
|---|---|---|
| `Animal` | `nombre`, `especie : Especie`, `raza`, `sexo : Sexo`, `edadAproximada`, `fechaIngreso`, `origenIngreso : OrigenIngreso` | `cambiarEstado(estado)`, `estadoActual() : EstadoAnimal`, `estaDisponible() : boolean`, `tieneTratamientoActivo() : boolean` |
| `RegistroEstado` | `estado : EstadoAnimal`, `fecha`, `motivo` | — |

`estadoActual()` y `tieneTratamientoActivo()` son operaciones **calculadas**, no atributos almacenados: el estado se deriva del último `RegistroEstado`, y el tratamiento activo, de la existencia de un `Tratamiento` sin fecha de finalización.

### Alojamiento y atención médica

| Clase | Atributos | Operaciones |
|---|---|---|
| `EspacioAlojamiento` ⊳ `IDesactivable` | `nombre`, `capacidad`, `activo` | `desactivar()`, `reactivar()`, `estaActivo()`, `tieneCupo()`, `ocupacionActual()`, `ocupar(animal)`, `liberar(animal)` |
| `TipoVacuna` ⊳ `IDesactivable` | `nombre`, `descripcion`, `activo` | `desactivar()`, `reactivar()`, `estaActivo()` |
| `Vacunacion` | `fechaAplicacion` | `registrar()` |
| `Tratamiento` | `diagnostico`, `fechaInicio`, `fechaFin` | `finalizar(fecha)`, `estaActivo()` |

### Proceso de adopción

| Clase | Atributos | Operaciones |
|---|---|---|
| `SolicitudAdopcion` | `fechaSolicitud`, `estado : EstadoSolicitud`, `fechaEvaluacion`, `resultado : ResultadoEvaluacion`, `observaciones`, `motivoCancelacion` | `evaluar(res)`, `cancelar(motivo)`, `estaPendiente()` |
| `Adopcion` | `fechaEntrega`, `fechaDevolucion`, `motivoDevolucion` | `registrarDevolucion(motivo)`, `fueDevuelta()` |
| `VisitaSeguimiento` | `fechaPrevista`, `fechaRealizacion`, `estado : EstadoVisita`, `calificacion : Calificacion`, `observaciones` | `reprogramar(fecha)`, `cancelar(motivo)`, `registrarResultado(calif)` |

### Interfaz `IDesactivable` «interface»

`desactivar()`, `reactivar()`, `estaActivo()`.

La implementan `EspacioAlojamiento` y `TipoVacuna`. Las dos entidades siguen la misma regla de negocio: se desactivan en lugar de eliminarse y pueden reactivarse después, para no perder la información histórica que las referencia.

### Relaciones

| Origen | Tipo | Destino | Multiplicidad | Nombre | Regla que la justifica |
|---|---|---|---|---|---|
| Usuario | herencia | Persona | — | — | Comparte los datos de contacto |
| Adoptante | herencia | Persona | — | — | Comparte los datos de contacto |
| EspacioAlojamiento | realización | IDesactivable | — | — | Se desactiva en lugar de eliminarse |
| TipoVacuna | realización | IDesactivable | — | — | Se desactiva en lugar de eliminarse |
| Animal | composición | RegistroEstado | 1 — 1..* | tiene | Todo animal ingresa *En valoración*, así que siempre hay al menos un registro |
| Usuario | asociación | RegistroEstado | 1 — 0..* | registra | Cada cambio de estado deja constancia de quién lo hizo |
| EspacioAlojamiento | asociación | Animal | 0..1 — 0..* | aloja | Un animal está en un espacio o en ninguno |
| Usuario | asociación | Animal | 0..1 — 0..* | cuida | Un animal tiene a lo sumo un voluntario responsable a la vez |
| Animal | composición | Vacunacion | 1 — 0..* | recibe | El historial de vacunas no existe sin el animal |
| TipoVacuna | asociación | Vacunacion | 1 — 0..* | corresponde | Patrón tipo/instancia: el catálogo frente al hecho concreto |
| Usuario | asociación | Vacunacion | 1 — 0..* | aplica | Queda registrado el veterinario responsable |
| Animal | composición | Tratamiento | 1 — 0..* | recibe | El historial médico no existe sin el animal |
| Usuario | asociación | Tratamiento | 1 — 0..* | atiende | Queda registrado el veterinario responsable |
| Animal | asociación | SolicitudAdopcion | 1 — 0..* | recibe | Un animal puede acumular varias solicitudes pendientes a la vez |
| Adoptante | asociación | SolicitudAdopcion | 1 — 0..* | realiza | Un adoptante puede solicitar varios animales |
| Usuario | asociación | SolicitudAdopcion | 0..1 — 0..* | evalúa | Una solicitud pendiente todavía no tiene evaluador |
| SolicitudAdopcion | composición | Adopcion | 1 — 0..1 | origina | Solo las solicitudes aprobadas y entregadas generan adopción |
| Adopcion | composición | VisitaSeguimiento | 1 — 0..* | programa | Las visitas no existen sin la adopción que las origina |
| Usuario | asociación | VisitaSeguimiento | 1 — 0..* | realiza | Queda registrado el voluntario que hizo la visita |

### Enumeraciones

| Enumeración | Valores |
|---|---|
| `Rol` | COORDINADOR, VETERINARIO, VOLUNTARIO |
| `Especie` | PERRO, GATO, OTRO |
| `Sexo` | MACHO, HEMBRA, DESCONOCIDO |
| `OrigenIngreso` | RESCATE_VIA_PUBLICA, ENTREGA_VOLUNTARIA, REMISION_AUTORIDAD, OTRO |
| `EstadoAnimal` | EN_VALORACION, DISPONIBLE, EN_PROCESO_ADOPCION, ADOPTADO, NO_DISPONIBLE |
| `TipoVivienda` | CASA, APARTAMENTO, FINCA, OTRO |
| `EstadoSolicitud` | PENDIENTE, APROBADA, RECHAZADA, CANCELADA |
| `ResultadoEvaluacion` | APROBADA, RECHAZADA |
| `EstadoVisita` | PROGRAMADA, REALIZADA, CANCELADA |
| `Calificacion` | SATISFACTORIA, CON_ALERTAS |

### Transiciones de estado del animal

| Desde | Hacia | Cómo |
|---|---|---|
| En valoración | Disponible, No disponible | Manual (HU-04) |
| Disponible | En valoración, No disponible | Manual (HU-04); las solicitudes pendientes se rechazan automáticamente |
| Disponible | En proceso de adopción | Automático al aprobarse una solicitud (HU-14) |
| En proceso de adopción | Disponible | Automático, únicamente por cancelación del adoptante (HU-15) |
| En proceso de adopción | Adoptado | Automático al registrarse la adopción (HU-17) |
| Adoptado | En valoración | Automático al registrarse una devolución (HU-20) |
| Adoptado | No disponible | Manual (HU-04) |

*No disponible* es un estado terminal y cubre el fallecimiento o el traslado a otra entidad; el motivo queda en el `RegistroEstado`.

## 4. Vista de desarrollo

Diagrama de clases organizado por capas. Muestra cómo se estructura el código: qué clase recibe la petición, cuál aplica las reglas y cuál habla con la base de datos.

Diagrama: [`vista-desarrollo.vpp`](docs/diagramas/vista-desarrollo.vpp) (fuente de Visual Paradigm) · [`vista-desarrollo.png`](docs/diagramas/vista-desarrollo.png) (imagen)

| Capa | Marco en el diagrama | Responsabilidad | Sufijo |
|---|---|---|---|
| Presentación | `Presentation_Controller` | Recibir la petición HTTP, validar el formato de la entrada y devolver la respuesta con su código de estado | `…Controller` |
| Negocio | `Business_Service` | Coordinar el caso de uso y hacer cumplir las reglas del dominio | `…Service` |
| Datos | `Data_Repository` | Guardar y recuperar documentos de MongoDB | `…Repository` |

El cuarto marco, `Domain_Model`, contiene las clases de la vista lógica. **No es una cuarta capa de ejecución**: es el modelo de dominio que las tres capas comparten como tipo de sus parámetros y de sus retornos. La dirección de las dependencias es siempre hacia abajo: Presentación conoce a Negocio, Negocio conoce a Datos, y ninguna capa conoce a la que está encima.

| Relación | Trazo | Cuándo se usa | Ejemplo |
|---|---|---|---|
| Asociación navegable | Línea continua, flecha abierta | El objeto se inyecta en el constructor y queda como atributo | `AnimalController → AnimalService` |
| Dependencia | Línea punteada, flecha abierta | El tipo solo aparece como parámetro o como retorno | `AnimalRepository ⇢ Animal` |
| Generalización | Línea continua, triángulo hueco | Herencia dentro del modelo de dominio | `Usuario ▷ Persona` |
| Composición | Línea continua, rombo relleno en el todo | La parte no existe sin el todo | `Animal ◆ RegistroEstado` |

Los repositorios se dibujan como `«interface»` porque en Spring Data el programador declara la interfaz y el framework genera la implementación en tiempo de ejecución; por eso tampoco aparece una clase `…RepositoryImpl`. Las dependencias `Service ⇢ entidad` («crea y devuelve») existen en el modelo pero no se dibujan: cruzarían el lienzo entero.

### Correspondencia con la vista funcional

Hay **un Controller y un Service por cada componente**, así que esta vista no inventa una estructura nueva: traduce a código el reparto ya fijado.

| Componente | Controller | Service | Repositorios que usa | Historias |
|---|---|---|---|---|
| Gestión de Usuarios y Acceso | `UsuarioController` | `UsuarioService` | `UsuarioRepository` | HU-01, HU-02 |
| Gestión de Alojamiento | `EspacioAlojamientoController` | `EspacioAlojamientoService` | `EspacioAlojamientoRepository` | HU-06 |
| Gestión de Animales | `AnimalController` | `AnimalService` | `AnimalRepository` | HU-03, HU-04, HU-05, HU-21 |
| Atención Médica | `AtencionMedicaController` | `AtencionMedicaService` | `TipoVacunaRepository`, `VacunacionRepository`, `TratamientoRepository` | HU-09, HU-10, HU-11 |
| Gestión de Adopciones | `AdopcionController` | `AdopcionService` | `AdoptanteRepository`, `SolicitudAdopcionRepository` | HU-12 a HU-20 |
| Consultas y Reportes | `ConsultaController` | `ConsultaService` | ninguno propio | HU-07, HU-08, HU-16, HU-19 |

`ConsultaService` **no tiene repositorio propio**, igual que el componente Consultas y Reportes no tiene clases propias. Obtiene todo a través de los otros services, respetando sus reglas en lugar de leer las colecciones por su cuenta.

### Colaboración entre services

Son las nueve conexiones de ensamblaje de la vista funcional trasladadas a la capa de negocio. Se documentan en tabla y no como líneas en el diagrama, para que las tres capas se sigan leyendo de arriba abajo.

| Service que pide | Service que responde | Interfaz | Para qué |
|---|---|---|---|
| `ConsultaService` | `AnimalService` | `IConsultaAnimal` | Obtener el animal y su estado para la ficha integral |
| `ConsultaService` | `AtencionMedicaService` | `IHistorialMedico` | Añadir vacunas y tratamientos a la ficha |
| `ConsultaService` | `AdopcionService` | `IConsultaAdopciones` | Añadir solicitudes, adopción y visitas a la ficha |
| `AtencionMedicaService` | `AnimalService` | `IConsultaAnimal` | Verificar que el animal existe antes de registrar vacuna o tratamiento |
| `AdopcionService` | `AnimalService` | `IConsultaAnimal` | Verificar que el animal está disponible antes de aceptar la solicitud |
| `AdopcionService` | `AnimalService` | `ITransicionEstado` | Mover el animal a *en proceso*, *adoptado*, *disponible* o *en valoración* |
| `AnimalService` | `EspacioAlojamientoService` | `IEspacios` | Comprobar cupo, ocupar y liberar al alojar o adoptar |
| `AnimalService` | `UsuarioService` | `IConsultaUsuario` | Verificar que el usuario asignado tenga el rol de voluntario |
| `AnimalService` | `AdopcionService` | `IBajaSolicitudes` | Rechazar las solicitudes pendientes de un animal que deja de estar disponible |

### `FichaAnimal`: el único objeto que no es entidad

`fichaDe` devuelve información de cuatro componentes a la vez: el animal, su espacio, su historial médico y sus solicitudes. No existe ninguna entidad del dominio que corresponda a eso, y crearla sería inventar una clase sin identidad ni ciclo de vida propios.

Por eso `FichaAnimal` es un `«record»` que `ConsultaService` arma pidiendo las partes a los demás services. Esto explica por qué el componente Consultas y Reportes aparece sin clases propias en la vista funcional: su salida es un DTO, no una entidad.

## 5. Vista de despliegue

Dónde se ejecuta cada pieza y cómo se comunican los nodos.

Diagrama: [`vista-despliegue.vpp`](docs/diagramas/vista-despliegue.vpp) (fuente de Visual Paradigm) · [`vista-despliegue.png`](docs/diagramas/vista-despliegue.png) (imagen)

| Nodo «device» | Entorno de ejecución | Artefacto desplegado |
|---|---|---|
| `EquipoDelRefugio` | `NavegadorWeb` | `interfaz-web` |
| `ServidorDeAplicaciones` | `JVM 21` | `refugio-api.jar` |
| `ServidorDeBaseDeDatos` | `MongoDB 7.0` | `refugio` (base de datos) |

| Ruta de comunicación | Entre | Protocolo |
|---|---|---|
| HTTPS | EquipoDelRefugio — ServidorDeAplicaciones | TCP 443, REST sobre JSON |
| MongoDB Wire Protocol | ServidorDeAplicaciones — ServidorDeBaseDeDatos | TCP 27017 |

El `.jar` despliega los seis componentes de la vista funcional; la base de datos contiene las ocho colecciones. Un solo refugio, un solo despliegue: no hay sistemas externos, lo que coincide con la vista de contexto. El navegador carga la interfaz que sirve el mismo `.jar`.

El artefacto va **anidado dentro** de su entorno de ejecución: en UML el anidamiento ya *es* el despliegue, así que una dependencia `«deploy»` adicional sería redundante.

---

# Decisiones de diseño

Decisiones tomadas durante el modelado, con su justificación y la alternativa que se descartó.

### El rol es un atributo de `Usuario`, no una jerarquía de herencia

**Alternativa descartada:** `Usuario` como clase padre con `Coordinador`, `Veterinario` y `Voluntario` como subclases.

Los tres roles no tienen atributos propios; solo se diferencian en permisos. Además, el cambio de estado de un animal (HU-04) lo pueden hacer tanto el coordinador como el veterinario, así que esa asociación tendría que colgar del padre de todos modos y el modelo quedaría mixto. Por último, la herencia fija el tipo del objeto: si un usuario cambiara de rol habría que recrearlo, mientras que con el atributo basta con modificarlo.

Lo que sí se modeló como herencia es `Persona` con `Usuario` y `Adoptante`, porque ahí la generalización responde a atributos compartidos reales y no a una clasificación por permisos.

### El estado del animal es una clase con historial, no un atributo

**Alternativa descartada:** un atributo `estado` en `Animal`.

HU-04 exige registrar la fecha del cambio y el usuario responsable. Un dato que se repite en el tiempo y tiene atributos propios deja de ser un atributo y pasa a ser una clase.

**Consecuencia asumida:** `estadoActual()` es una operación calculada sobre el último `RegistroEstado`. Para filtrar animales disponibles (HU-07) hay que consultar el último registro de cada uno; es una decisión consciente, no un descuido.

### *En tratamiento* no es un estado del animal

Esa información ya se deduce de la existencia de un `Tratamiento` sin fecha de finalización. Tenerla también como estado crea dos fuentes de verdad que pueden contradecirse. Se expone como la operación `tieneTratamientoActivo()`.

### Evaluación y devolución son atributos, no clases

Ambas tienen multiplicidad 0..1 y tres atributos o menos. Una clase separada para cada una no aportaba comportamiento propio y sí cuatro cajas más al diagrama. Se absorbieron como atributos en la clase a la que pertenecen, junto con las operaciones `evaluar()` y `registrarDevolucion()`.

### Patrón tipo/instancia entre `TipoVacuna` y `Vacunacion`

`TipoVacuna` es el catálogo que se administra una sola vez (HU-09); `Vacunacion` es el hecho concreto de aplicar una de ellas a un animal en una fecha (HU-10). Es el mismo patrón que separa un libro de sus ejemplares. Sin esa separación, el tipo de vacuna sería texto libre y no habría catálogo que administrar.

### La interfaz `IDesactivable`

`EspacioAlojamiento` y `TipoVacuna` no tienen relación entre sí, pero cumplen el mismo contrato: se desactivan en lugar de eliminarse y pueden reactivarse. Dos clases sin relación que comparten contrato es el caso que justifica una interfaz. La regla existe para no perder la información histórica que las referencia.

### Consultas y Reportes como componente propio

**Alternativa descartada:** dejar la ficha integral (HU-08) dentro de Gestión de Animales.

La ficha consolida información de cuatro componentes. Si viviera en Gestión de Animales, ese componente dependería de Atención Médica y de Gestión de Adopciones, que ya dependen de él: dependencias circulares evitables. Un componente que solo consume y nunca es consumido las elimina sin añadir flechas.

### El adoptante no es actor del sistema

No opera el software: sus datos los registra el coordinador (HU-12) y sus solicitudes también (HU-13). Por eso no aparece como entidad externa en la vista de contexto ni como rol en la matriz de permisos. Aparece en la vista lógica como clase, porque sus datos sí se almacenan.

### El refugio no revierte una solicitud aprobada

Una solicitud aprobada solo puede cancelarla el adoptante (HU-15). Revertirla por decisión del refugio queda fuera de alcance, porque implicaría reevaluar decisiones ya comunicadas. Cuando una solicitud aprobada se cancela, el animal vuelve a *Disponible*, pero las solicitudes que se rechazaron automáticamente al aprobarla siguen rechazadas: quien siga interesado registra una nueva.

### Qué se incrusta en MongoDB y qué no

MongoDB no tiene tablas ni claves foráneas: la decisión es **qué vive dentro de un documento y qué en su propia colección**. El criterio aplicado es la composición de la vista lógica *más* la frontera del componente que la posee.

- Cuando el todo y la parte **pertenecen al mismo componente**, la parte se incrusta: `RegistroEstado` dentro de `animales`, y `Adopcion` con sus `VisitaSeguimiento` dentro de `solicitudes`. Nadie fuera del componente las lee por separado y se borran con su documento padre.
- Cuando la composición **cruza la frontera de un componente**, la parte va a su propia colección con una referencia al animal. Es el caso de `Vacunacion` y `Tratamiento`: son partes de `Animal`, pero las posee Atención Médica. Incrustarlas obligaría a Atención Médica a escribir dentro de los documentos de Gestión de Animales, rompiendo el encapsulamiento de la vista funcional.

En el segundo caso la composición sigue siendo cierta en el dominio: si se elimina un animal, `AnimalService` elimina sus vacunaciones y tratamientos. La diferencia es que esa regla la hace cumplir el service, no el motor de base de datos.

---

# Requisitos

La columna **Historia** enlaza con la issue que implementa cada requisito. Ningún requisito queda sin historia y ninguna historia carece de requisito de origen.

### Funcionales

| ID | Descripción | Historia |
|---|---|---|
| RF001 | Registrar usuarios del refugio con identificación, nombre, correo y rol. | [HU-01](../../issues/1) |
| RF002 | Autenticar a un usuario y habilitar únicamente las funciones de su rol. | [HU-02](../../issues/2) |
| RF003 | Registrar el ingreso de un animal rescatado con sus datos y su origen. | [HU-03](../../issues/3) |
| RF004 | Actualizar la información registrada de un animal. | [HU-03](../../issues/3) |
| RF005 | Registrar un cambio de estado de un animal con fecha, motivo y responsable. | [HU-04](../../issues/4) |
| RF006 | Registrar la ubicación de un animal en un espacio de alojamiento. | [HU-05](../../issues/5) |
| RF007 | Consultar los espacios de alojamiento con su capacidad y su ocupación. | [HU-06](../../issues/6) |
| RF008 | Buscar animales con filtros por especie, raza, sexo, edad y estado. | [HU-07](../../issues/7) |
| RF009 | Consultar la ficha integral de un animal. | [HU-08](../../issues/8) |
| RF010 | Administrar un catálogo de tipos de vacuna. | [HU-09](../../issues/9) |
| RF011 | Registrar la aplicación de una vacuna a un animal. | [HU-10](../../issues/10) |
| RF012 | Registrar un tratamiento médico y su finalización. | [HU-11](../../issues/11) |
| RF013 | Registrar un adoptante con sus datos de contacto y vivienda. | [HU-12](../../issues/12) |
| RF014 | Registrar una solicitud de adopción sobre un animal disponible. | [HU-13](../../issues/13) |
| RF015 | Registrar la evaluación de una solicitud con fecha, resultado y observaciones. | [HU-14](../../issues/14) |
| RF016 | Registrar la cancelación de una solicitud por decisión del adoptante. | [HU-15](../../issues/15) |
| RF017 | Consultar las solicitudes de un adoptante y las asociadas a un animal. | [HU-16](../../issues/16) |
| RF018 | Registrar una adopción a partir de una solicitud aprobada. | [HU-17](../../issues/17) |
| RF019 | Programar las visitas de seguimiento de una adopción. | [HU-18](../../issues/18) |
| RF020 | Registrar el resultado de una visita de seguimiento. | [HU-19](../../issues/19) |
| RF021 | Registrar la devolución de un animal adoptado. | [HU-20](../../issues/20) |
| RF022 | Asignar un voluntario como responsable del cuidado de uno o varios animales. | [HU-21](../../issues/21) |

### No funcionales

| ID | Descripción | Métrica |
|---|---|---|
| RNF001 | Operable por personal sin conocimientos técnicos avanzados. | Un usuario sin entrenamiento completa el registro de un animal en ≤ 3 minutos, medido con al menos 3 usuarios. |
| RNF002 | Mensajes de error específicos ante entradas inválidas. | 100 % de los campos con validación muestran un mensaje que identifica el campo y el motivo. |
| RNF003 | Las consultas y los registros responden sin interrumpir el trabajo. | Tiempo de respuesta ≤ 3 s con 50 animales activos y 200 registros históricos. |
| RNF004 | El rendimiento se mantiene al crecer el volumen. | Se mantienen los tiempos de RNF003 con 500 animales y 2000 adopciones. |
| RNF005 | Autenticación obligatoria antes de cualquier funcionalidad. | 100 % de las funciones exigen sesión activa; 0 accesos sin credenciales. |
| RNF006 | Contraseñas almacenadas de forma protegida. | 100 % con función de hash y sal; 0 contraseñas recuperables en texto plano. |
| RNF007 | Disponible durante el horario de operación del refugio. | Disponibilidad ≥ 95 % de 8:00 a 18:00, medida mensualmente. |
| RNF008 | Conserva la información ante un cierre inesperado. | Copia de respaldo diaria; pérdida máxima tolerada de 24 h. |
| RNF009 | Impide el registro de información incompleta o duplicada. | 100 % de campos obligatorios validados; 0 duplicados por identificación o correo. |
| RNF010 | Construido de forma modular y documentada. | Complejidad ciclomática ≤ 10 por método y 0 violaciones críticas por cada 1000 líneas. |
| RNF011 | Funciona en los navegadores de uso común del refugio. | Sin errores bloqueantes en las dos últimas versiones de Chrome, Firefox y Edge. |

### Backlog

Las 21 historias están publicadas como [issues](../../issues), una por historia. Cada una lleva su validación INVEST, los criterios de aceptación en Gherkin, los supuestos y decisiones, y el mockup de la pantalla. Están etiquetadas por componente y agrupadas en cinco hitos según el orden de implementación.

---

# El backend

```
com.refugio
├── presentacion   → UsuarioController, EspacioAlojamientoController, AnimalController,
│                    AtencionMedicaController, AdopcionController, ConsultaController
│                    (+ dto/: FichaAnimal)
├── negocio        → UsuarioService, EspacioAlojamientoService, AnimalService,
│                    AtencionMedicaService, AdopcionService, ConsultaService
├── datos          → UsuarioRepository, AdoptanteRepository, EspacioAlojamientoRepository,
│                    AnimalRepository, TipoVacunaRepository, VacunacionRepository,
│                    TratamientoRepository, SolicitudAdopcionRepository
└── modelo         → Persona {abstract}, Usuario, Adoptante, Animal, RegistroEstado,
                     EspacioAlojamiento, TipoVacuna, Vacunacion, Tratamiento,
                     SolicitudAdopcion, Adopcion, VisitaSeguimiento, IDesactivable,
                     y las diez enumeraciones
```

Controller → Service → Repository, con inyección por constructor. Cada controller usa solo su service, y `modelo` no depende de ningún otro paquete del proyecto.

### Clases transversales

| Clase | Qué hace |
|---|---|
| `SecurityConfig` | Define qué rol puede llamar a cada ruta, según la columna Rol de la tabla de endpoints |
| `GlobalExceptionHandler` | Traduce las excepciones del dominio a los códigos `400`, `404` y `409` |
| `MongoConfig` | Conexión a la base de datos y conversores de los tipos de fecha |

### Colecciones de MongoDB

| Colección | Documento raíz | Incrusta |
|---|---|---|
| `usuarios` | `Usuario` | — |
| `adoptantes` | `Adoptante` | — |
| `espacios` | `EspacioAlojamiento` | — |
| `animales` | `Animal` | `RegistroEstado` |
| `tiposVacuna` | `TipoVacuna` | — |
| `vacunaciones` | `Vacunacion` | — |
| `tratamientos` | `Tratamiento` | — |
| `solicitudes` | `SolicitudAdopcion` | `Adopcion`, y dentro de ella `VisitaSeguimiento` |

### Endpoints REST

Las transiciones de estado se modelan como una acción sobre un subrecurso (`POST`) y no como una edición del campo (`PATCH`), porque cada transición tiene reglas propias.

| Método | Ruta | Controller.método | Rol | Historia |
|---|---|---|---|---|
| `POST` | `/api/usuarios` | `UsuarioController.registrar` | Coordinador | HU-01 |
| `POST` | `/api/sesiones` | `UsuarioController.autenticar` | Todos | HU-02 |
| `DELETE` | `/api/sesiones/actual` | `UsuarioController.cerrarSesion` | Todos | HU-02 |
| `POST` | `/api/espacios` | `EspacioAlojamientoController.crear` | Coordinador | HU-06 |
| `PATCH` | `/api/espacios/{id}` | `EspacioAlojamientoController.actualizarCapacidad` | Coordinador | HU-06 |
| `POST` | `/api/espacios/{id}/desactivacion` | `EspacioAlojamientoController.desactivar` | Coordinador | HU-06 |
| `POST` | `/api/espacios/{id}/reactivacion` | `EspacioAlojamientoController.reactivar` | Coordinador | HU-06 |
| `GET` | `/api/espacios/{id}/ocupacion` | `EspacioAlojamientoController.consultarOcupacion` | Coordinador | HU-06 |
| `POST` | `/api/animales` | `AnimalController.registrar` | Coordinador | HU-03 |
| `PUT` | `/api/animales/{id}` | `AnimalController.actualizar` | Coordinador | HU-03 |
| `POST` | `/api/animales/{id}/estados` | `AnimalController.cambiarEstado` | Coordinador, Veterinario | HU-04 |
| `POST` | `/api/animales/{id}/alojamiento` | `AnimalController.asignarAlojamiento` | Coordinador | HU-05 |
| `POST` | `/api/animales/{id}/responsable` | `AnimalController.asignarVoluntario` | Coordinador | HU-21 |
| `POST` | `/api/tipos-vacuna` | `AtencionMedicaController.crearTipoVacuna` | Veterinario | HU-09 |
| `POST` | `/api/tipos-vacuna/{id}/desactivacion` | `AtencionMedicaController.desactivarTipoVacuna` | Veterinario | HU-09 |
| `POST` | `/api/animales/{id}/vacunaciones` | `AtencionMedicaController.registrarVacunacion` | Veterinario | HU-10 |
| `POST` | `/api/animales/{id}/tratamientos` | `AtencionMedicaController.registrarTratamiento` | Veterinario | HU-11 |
| `POST` | `/api/tratamientos/{id}/finalizacion` | `AtencionMedicaController.finalizarTratamiento` | Veterinario | HU-11 |
| `POST` | `/api/adoptantes` | `AdopcionController.registrarAdoptante` | Coordinador | HU-12 |
| `POST` | `/api/solicitudes` | `AdopcionController.registrarSolicitud` | Coordinador | HU-13 |
| `POST` | `/api/solicitudes/{id}/evaluacion` | `AdopcionController.evaluarSolicitud` | Coordinador | HU-14 |
| `POST` | `/api/solicitudes/{id}/cancelacion` | `AdopcionController.cancelarSolicitud` | Coordinador | HU-15 |
| `POST` | `/api/solicitudes/{id}/adopcion` | `AdopcionController.registrarAdopcion` | Coordinador | HU-17 |
| `POST` | `/api/solicitudes/{id}/devolucion` | `AdopcionController.registrarDevolucion` | Coordinador | HU-20 |
| `POST` | `/api/solicitudes/{id}/visitas` | `AdopcionController.programarVisita` | Coordinador | HU-18 |
| `POST` | `/api/solicitudes/{id}/visitas/{idVisita}/resultado` | `AdopcionController.registrarResultadoVisita` | Voluntario | HU-19 |
| `GET` | `/api/animales` | `ConsultaController.buscarAnimales` | Todos | HU-07 |
| `GET` | `/api/animales/{id}/ficha` | `ConsultaController.fichaDe` | Todos | HU-08 |
| `GET` | `/api/solicitudes` | `ConsultaController.consultarSolicitudes` | Coordinador | HU-16 |
| `GET` | `/api/visitas` | `ConsultaController.visitasAsignadasA` | Voluntario | HU-19 |

`VisitaSeguimiento` no tiene colección propia: va incrustada en la adopción, que a su vez va dentro de la solicitud. Por eso su ruta de escritura cuelga de `/api/solicitudes/{id}`. La ruta refleja la estructura de almacenamiento.

Códigos de respuesta: `200` consultas y actualizaciones, `201` creaciones, `204` cierre de sesión, `400` entrada mal formada, `404` recurso inexistente, `409` la operación contradice una regla del dominio (adoptar un animal no disponible, alojar en un espacio sin cupo), `500` fallos no previstos.

---

# Cómo correrlo

| Herramienta | Versión |
|---|---|
| Java (JDK) | 21 |
| Maven | 3.9 o superior |
| MongoDB | 7 en local, o un clúster de MongoDB Atlas |
| Postman | para probar los endpoints |

### Configuración

`src/main/resources/application.properties`:

| Propiedad | Variable de entorno | Valor por defecto |
|---|---|---|
| `spring.data.mongodb.uri` | `MONGODB_URI` | `mongodb://localhost:27017/refugio` |
| `refugio.jwt.secreto` | `JWT_SECRETO` | clave de desarrollo (cámbiala en producción, mínimo 32 caracteres) |
| `server.port` | — | `8080` |

Para apuntar a Atlas:

```powershell
$env:MONGODB_URI = "mongodb+srv://<usuario>:<contrasena>@<cluster>.mongodb.net/refugio?retryWrites=true&w=majority"
```

### Arrancar

```powershell
mvn spring-boot:run
```

O empaquetado como un solo artefacto (`target/refugio-api.jar`, el de la vista de despliegue):

```powershell
mvn clean package
java -jar target/refugio-api.jar
```

### Colección de Postman

`RefugioMascotas.postman_collection.json` — *Import* → elegir el archivo → *Run collection* en orden. Se publicará junto con la implementación de los endpoints.

---

# Pendientes

- Corregir en la vista lógica la multiplicidad `0..1` de `Usuario — SolicitudAdopcion` («evalúa»), el `1` que falta en el extremo `SolicitudAdopcion` de la composición «origina», y `string` → `String`. Reexportar el PNG después.
- Definir la paginación de `GET /api/animales` y `GET /api/solicitudes`.
- Decidir el formato de los filtros de `buscarAnimales`: parámetros de consulta sueltos o un objeto de criterios.
- Implementar el backend y publicar la colección de Postman.
