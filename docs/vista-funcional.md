# Vista funcional

Diagrama de componentes UML. Agrupa las funcionalidades del sistema en componentes con responsabilidades diferenciadas y expresa su colaboración mediante interfaces proporcionadas y requeridas. No describe clases ni despliegue.

![Diagrama de componentes](diagramas/vista-funcional.png)

## Componentes

La columna **Clases** enlaza esta vista con la [vista lógica](vista-logica.md); la columna **Historias**, con el backlog.

| Componente | Responsabilidad | Clases | Historias |
|---|---|---|---|
| Gestión de Usuarios y Acceso | Administrar las cuentas del equipo y controlar qué puede hacer cada rol | `Persona`, `Usuario` | HU-01, HU-02 |
| Gestión de Alojamiento | Administrar los espacios del refugio y su capacidad disponible | `EspacioAlojamiento` | HU-06 |
| Gestión de Animales | Mantener los datos del animal, su estado, su ubicación y su cuidador responsable | `Animal`, `RegistroEstado` | HU-03, HU-04, HU-05, HU-21 |
| Atención Médica | Llevar el catálogo de vacunas y el historial sanitario de cada animal | `TipoVacuna`, `Vacunacion`, `Tratamiento` | HU-09, HU-10, HU-11 |
| Gestión de Adopciones | Conducir el proceso completo: adoptante, solicitud, entrega, devolución y visitas de seguimiento | `Adoptante`, `SolicitudAdopcion`, `Adopcion`, `VisitaSeguimiento` | HU-12 a HU-20 |
| Consultas y Reportes | Consolidar y presentar información que proviene de varios componentes | — | HU-07, HU-08 |

## Interfaces públicas

Son las que el sistema ofrece al exterior; las consume la capa de presentación. En el diagrama se dibujan como bola sin semicírculo.

| Interfaz | La proporciona | Operaciones principales | Historias |
|---|---|---|---|
| `IGestionUsuarios` | Gestión de Usuarios y Acceso | `registrarUsuario` | HU-01 |
| `IAutenticacion` | Gestión de Usuarios y Acceso | `autenticar`, `cerrarSesion` | HU-02 |
| `IAdministracionEspacios` | Gestión de Alojamiento | `crearEspacio`, `actualizarCapacidad`, `desactivarEspacio`, `reactivarEspacio`, `consultarOcupacion` | HU-06 |
| `IRegistroAnimales` | Gestión de Animales | `registrarAnimal`, `actualizarAnimal` | HU-03 |
| `IEstadoAnimal` | Gestión de Animales | `cambiarEstado` | HU-04 |
| `IAsignacionesAnimal` | Gestión de Animales | `asignarAlojamiento`, `asignarVoluntario` | HU-05, HU-21 |
| `ICatalogoVacunas` | Atención Médica | `crearTipoVacuna`, `desactivarTipoVacuna`, `reactivarTipoVacuna` | HU-09 |
| `IRegistroSanitario` | Atención Médica | `registrarVacunacion`, `registrarTratamiento`, `finalizarTratamiento` | HU-10, HU-11 |
| `IGestionAdoptantes` | Gestión de Adopciones | `registrarAdoptante`, `actualizarAdoptante` | HU-12 |
| `IGestionSolicitudes` | Gestión de Adopciones | `registrarSolicitud`, `evaluarSolicitud`, `cancelarSolicitud`, `consultarSolicitudes` | HU-13 a HU-16 |
| `IGestionAdopciones` | Gestión de Adopciones | `registrarAdopcion`, `registrarDevolucion`, `programarVisita`, `registrarResultadoVisita` | HU-17 a HU-20 |
| `IBusquedaAnimales` | Consultas y Reportes | `buscar` | HU-07 |
| `IFichaIntegral` | Consultas y Reportes | `fichaDe` | HU-08 |

## Interfaces internas y conexiones de ensamblaje

Son los contratos entre componentes. En el diagrama cada una se dibuja con una sola bola en el proveedor y tantos semicírculos como consumidores tenga.

| Interfaz | La proporciona | La requieren | Operaciones | Por qué se necesita |
|---|---|---|---|---|
| `IConsultaUsuario` | Gestión de Usuarios y Acceso | Gestión de Animales, Atención Médica, Gestión de Adopciones | `existeConRol`, `obtenerNombre` | Validar que el voluntario, el veterinario o el coordinador que queda como responsable exista y tenga ese rol |
| `IEspacios` | Gestión de Alojamiento | Gestión de Animales | `tieneCupo`, `ocupar`, `liberar` | Comprobar el cupo antes de alojar un animal y liberar el espacio al adoptarse |
| `IConsultaAnimal` | Gestión de Animales | Atención Médica, Gestión de Adopciones, Consultas y Reportes | `obtenerAnimal`, `estadoActual`, `estaDisponible`, `listar` | Atención Médica no vacuna a un animal *No disponible*; Adopciones no acepta solicitudes sobre uno que no esté *Disponible*; Consultas arma la ficha |
| `ITransicionEstado` | Gestión de Animales | Gestión de Adopciones | `marcarEnProcesoAdopcion`, `marcarAdoptado`, `marcarEnValoracion` | Los cambios de estado automáticos al aprobar, entregar o recibir una devolución |
| `IHistorialMedico` | Atención Médica | Consultas y Reportes | `vacunasDe`, `tratamientosDe`, `tieneTratamientoActivo` | La ficha integral muestra el historial sanitario |
| `IConsultaAdopciones` | Gestión de Adopciones | Consultas y Reportes | `solicitudesDe`, `adopcionDe`, `visitasDe` | La ficha integral muestra solicitudes, adopción y visitas |

Son seis interfaces internas y diez conexiones de ensamblaje.

## Decisiones de esta vista

**Toda colaboración se expresa como interfaz, no como dependencia.** Una dependencia dice que un componente necesita a otro; una interfaz dice *qué* le pide y con qué firma. Esto último es lo que permite reemplazar la implementación de un componente sin tocar a sus clientes. En el diagrama no hay ninguna flecha discontinua de dependencia.

**Seguimiento Posadopción se fusionó dentro de Gestión de Adopciones.** Tenía una sola clase, dos historias y su única colaboración era con Adopciones. Las visitas de seguimiento son parte del ciclo de vida de la adopción, no una responsabilidad separada.

**Consultas y Reportes se mantiene como componente propio y no dentro de Atención Médica.** La ficha integral consolida cinco cosas: estado del animal, alojamiento, historial médico, solicitudes y visitas. Si viviera dentro de Atención Médica, ese componente tendría que depender de Gestión de Adopciones, lo que mezcla la responsabilidad sanitaria con la del proceso de adopción. Como componente de solo lectura, consume de tres y no es consumido por nadie.

**No hay dependencias circulares.** El grafo es un orden estricto: Gestión de Usuarios y Acceso y Gestión de Alojamiento no requieren nada; Gestión de Animales requiere de esas dos; Atención Médica y Gestión de Adopciones requieren de Gestión de Animales; Consultas y Reportes requiere de los tres anteriores. Eso es lo que se perdería si la ficha integral viviera dentro de Gestión de Animales: ese componente pasaría a requerir de quienes ya lo requieren a él.

**No existe un componente de interfaz de usuario ni de persistencia.** El alcance de esta vista es la distribución de responsabilidades funcionales; la capa de presentación y el almacenamiento corresponden a la vista de desarrollo y a la de despliegue.

## Notación

Diagrama de componentes UML. Cada componente es un rectángulo con el estereotipo «component» y el icono de componente. Una interfaz proporcionada se dibuja con la bola unida al componente que la ofrece; una requerida, con el semicírculo que la abraza. Cuando una interfaz tiene varios consumidores se dibuja una sola bola y varios semicírculos. No se usan dependencias ni multiplicidades: no corresponden a esta vista.
