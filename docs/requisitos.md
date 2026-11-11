# Requisitos del sistema

Sistema de gestión para un centro de rescate y adopción de mascotas.

La columna **Historia** enlaza cada requisito funcional con la historia de usuario que lo implementa. Ningún requisito queda sin historia y ninguna historia carece de requisito de origen.

## Requisitos funcionales

### Usuarios y acceso

| ID | Descripción | Historia |
|---|---|---|
| RF001 | El sistema debe permitir registrar usuarios del refugio con su identificación, nombre, correo electrónico y rol (coordinador, veterinario o voluntario). | [HU-01](historias/HU-01-registrar-usuario-del-refugio.md) |
| RF002 | El sistema debe permitir a un usuario autenticarse mediante credenciales y habilitar únicamente las funciones correspondientes a su rol. | [HU-02](historias/HU-02-iniciar-sesion-y-acceder-segun-rol.md) |

### Gestión de animales

| ID | Descripción | Historia |
|---|---|---|
| RF003 | El sistema debe permitir registrar el ingreso de un animal rescatado con su nombre, especie, raza, sexo, edad aproximada, origen del ingreso, fecha de ingreso y descripción. | [HU-03](historias/HU-03-registrar-y-actualizar-animal.md) |
| RF004 | El sistema debe permitir actualizar la información registrada de un animal. | [HU-03](historias/HU-03-registrar-y-actualizar-animal.md) |
| RF005 | El sistema debe permitir registrar un cambio de estado de un animal, indicando el nuevo estado, la fecha del cambio y el usuario responsable. | [HU-04](historias/HU-04-registrar-cambio-de-estado-de-animal.md) |
| RF008 | El sistema debe permitir buscar animales aplicando filtros por especie, raza, sexo, edad y estado. | [HU-07](historias/HU-07-buscar-animales-con-filtros.md) |
| RF009 | El sistema debe permitir consultar la ficha integral de un animal, incluyendo su estado actual, su alojamiento, su historial médico, sus solicitudes y sus seguimientos. | [HU-08](historias/HU-08-consultar-ficha-integral-de-animal.md) |
| RF022 | El sistema debe permitir asignar un voluntario como responsable del cuidado de uno o varios animales. | [HU-21](historias/HU-21-asignar-voluntario-responsable.md) |

### Alojamiento

| ID | Descripción | Historia |
|---|---|---|
| RF006 | El sistema debe permitir registrar la ubicación de un animal en un espacio de alojamiento del refugio, indicando la fecha de asignación. | [HU-05](historias/HU-05-asignar-alojamiento-a-animal.md) |
| RF007 | El sistema debe permitir consultar los espacios de alojamiento del refugio con su capacidad y su ocupación actual. | [HU-06](historias/HU-06-administrar-espacios-de-alojamiento.md) |

### Atención médica

| ID | Descripción | Historia |
|---|---|---|
| RF010 | El sistema debe permitir administrar un catálogo de tipos de vacuna, indicando el nombre y la descripción de cada tipo. | [HU-09](historias/HU-09-administrar-catalogo-de-vacunas.md) |
| RF011 | El sistema debe permitir registrar la aplicación de una vacuna a un animal, indicando el tipo de vacuna, la fecha de aplicación y el veterinario responsable. | [HU-10](historias/HU-10-registrar-aplicacion-de-vacuna.md) |
| RF012 | El sistema debe permitir registrar un tratamiento médico de un animal, indicando el diagnóstico, la fecha de inicio, la fecha de finalización y el veterinario responsable. | [HU-11](historias/HU-11-registrar-y-finalizar-tratamiento.md) |

### Proceso de adopción

| ID | Descripción | Historia |
|---|---|---|
| RF013 | El sistema debe permitir registrar un adoptante con su identificación, nombre, teléfono, correo electrónico, dirección de residencia, tipo de vivienda y experiencia previa con animales. | [HU-12](historias/HU-12-registrar-y-editar-adoptante.md) |
| RF014 | El sistema debe permitir registrar una solicitud de adopción de un adoptante sobre un animal disponible, indicando la fecha de la solicitud. | [HU-13](historias/HU-13-registrar-solicitud-de-adopcion.md) |
| RF015 | El sistema debe permitir registrar la evaluación de una solicitud de adopción, indicando la fecha, el resultado y las observaciones del coordinador. | [HU-14](historias/HU-14-evaluar-solicitud-de-adopcion.md) |
| RF016 | El sistema debe permitir registrar la cancelación de una solicitud de adopción por decisión del adoptante, indicando la fecha y el motivo. | [HU-15](historias/HU-15-cancelar-solicitud-de-adopcion.md) |
| RF017 | El sistema debe permitir consultar las solicitudes de adopción registradas por un adoptante y las solicitudes asociadas a un animal. | [HU-16](historias/HU-16-consultar-solicitudes-de-adopcion.md) |
| RF018 | El sistema debe permitir registrar una adopción a partir de una solicitud aprobada, indicando la fecha de entrega del animal. | [HU-17](historias/HU-17-registrar-adopcion.md) |
| RF021 | El sistema debe permitir registrar la devolución de un animal adoptado, indicando la fecha y el motivo. | [HU-20](historias/HU-20-registrar-devolucion-de-animal.md) |

### Seguimiento posadopción

| ID | Descripción | Historia |
|---|---|---|
| RF019 | El sistema debe permitir programar las visitas de seguimiento de una adopción, indicando la fecha prevista y el voluntario asignado. | [HU-18](historias/HU-18-programar-visitas-de-seguimiento.md) |
| RF020 | El sistema debe permitir registrar el resultado de una visita de seguimiento, indicando la fecha de realización y las observaciones sobre el estado del animal. | [HU-19](historias/HU-19-registrar-resultado-de-visita.md) |

## Requisitos no funcionales

### Usabilidad

| ID | Descripción | Métrica |
|---|---|---|
| RNF001 | El sistema debe poder ser operado por personal del refugio sin conocimientos técnicos avanzados. | Un usuario sin entrenamiento previo completa el registro de un animal en ≤ 3 minutos, medido con al menos 3 usuarios. |
| RNF002 | El sistema debe presentar mensajes de error específicos ante entradas inválidas. | 100 % de los campos con validación muestran un mensaje que identifica el campo y el motivo del rechazo. |

### Rendimiento

| ID | Descripción | Métrica |
|---|---|---|
| RNF003 | Las consultas y los registros deben responder sin interrumpir el trabajo del usuario. | Tiempo de respuesta ≤ 3 segundos con 50 animales activos y 200 registros históricos. |
| RNF004 | El sistema debe mantener su rendimiento al crecer el volumen de información. | Se mantienen los tiempos de RNF003 con 500 animales y 2000 adopciones registradas. |

### Seguridad

| ID | Descripción | Métrica |
|---|---|---|
| RNF005 | El sistema debe autenticar a todo usuario antes de permitir el acceso a cualquier funcionalidad. | 100 % de las funciones exigen sesión activa; 0 accesos logrados sin credenciales en pruebas de acceso directo. |
| RNF006 | Las contraseñas deben almacenarse de forma protegida, nunca en texto plano. | 100 % de las contraseñas almacenadas con función de hash y sal; 0 contraseñas recuperables en texto plano. |

### Confiabilidad

| ID | Descripción | Métrica |
|---|---|---|
| RNF007 | El sistema debe estar disponible durante el horario de operación del refugio. | Disponibilidad ≥ 95 % del horario de 8:00 a 18:00, medida mensualmente. |
| RNF008 | El sistema debe conservar la información registrada ante un cierre inesperado. | Copia de respaldo diaria; pérdida máxima tolerada de 24 horas de registros. |
| RNF009 | El sistema debe impedir el registro de información incompleta o duplicada. | 100 % de los campos obligatorios validados; 0 duplicados por identificación de usuario, correo o identificación de adoptante. |

### Mantenibilidad y compatibilidad

| ID | Descripción | Métrica |
|---|---|---|
| RNF010 | El sistema debe construirse de forma modular y documentada para facilitar su modificación. | Complejidad ciclomática ≤ 10 por método y 0 violaciones críticas de estilo por cada 1000 líneas, verificado con análisis estático. |
| RNF011 | El sistema debe funcionar en los navegadores de uso común del refugio. | Funciona sin errores bloqueantes en las dos últimas versiones estables de Chrome, Firefox y Edge. |
