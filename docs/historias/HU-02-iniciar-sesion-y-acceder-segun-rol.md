### HU-02: Iniciar sesión y acceder solo a las funciones de mi rol

> Origen: RF002

**Como** usuario del refugio (coordinador, veterinario o voluntario)
**Quiero** iniciar sesión con mi correo y mi contraseña, usar únicamente las funciones de mi rol y cerrar sesión al terminar
**Para** operar el sistema de forma segura, dentro de mi responsabilidad y con mis acciones atribuidas a mi identidad

**Validación INVEST**
- I — Independent: ✅ Solo requiere usuarios existentes, preparables como datos de prueba.
- N — Negotiable: ✅ No impone mecanismo técnico de sesión, cifrado ni control de acceso.
- V — Valuable: ✅ Protege la información del refugio y evita registros hechos por personas no autorizadas.
- E — Estimable: ✅ Credenciales, comportamiento ante error y matriz de permisos acordados.
- S — Small: ✅ Autenticación más una regla de autorización tabulada; cabe en un sprint.
- T — Testable: ✅ Acceso concedido o denegado por cada combinación rol/función es verificable.

**Matriz de permisos acordada** (condición que aplica a todas las demás historias)

| Función | Coordinador | Veterinario | Voluntario |
|---|:-:|:-:|:-:|
| Registrar usuarios | ✔ | | |
| Registrar y actualizar animales | ✔ | | |
| Registrar cambio de estado del animal | ✔ | ✔ | |
| Asignar alojamiento | ✔ | | |
| Administrar espacios de alojamiento | ✔ | | |
| Administrar catálogo de tipos de vacuna | | ✔ | |
| Registrar vacunas y tratamientos | | ✔ | |
| Registrar y editar adoptantes; registrar, evaluar y cancelar solicitudes | ✔ | | |
| Consultar solicitudes de adopción | ✔ | | |
| Registrar adopciones y devoluciones | ✔ | | |
| Programar, reprogramar y cancelar visitas de seguimiento | ✔ | | |
| Registrar resultado de visita asignada | | | ✔ |
| Asignar voluntario responsable | ✔ | | |
| Buscar animales, ver ficha integral, consultar espacios | ✔ | ✔ | ✔ |

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Autenticación y control de acceso por rol

  Escenario: Inicio de sesión con credenciales válidas
    Dado que existe un usuario con el correo "luis@refugio.org" y la contraseña "Clave123"
    Cuando inicia sesión con el correo "luis@refugio.org" y la contraseña "Clave123"
    Entonces el sistema le concede acceso
    Y muestra su nombre y su rol en la sesión iniciada

  Escenario: Inicio de sesión con contraseña incorrecta
    Dado que existe un usuario con el correo "luis@refugio.org"
    Cuando inicia sesión con el correo "luis@refugio.org" y una contraseña incorrecta
    Entonces el sistema no le concede acceso
    Y muestra el mensaje "Correo o contraseña incorrectos"

  Escenario: Inicio de sesión con correo no registrado
    Dado que no existe ningún usuario con el correo "nadie@refugio.org"
    Cuando se intenta iniciar sesión con el correo "nadie@refugio.org"
    Entonces el sistema no concede acceso
    Y muestra el mensaje "Correo o contraseña incorrectos"

  Escenario: Intentos fallidos repetidos no bloquean la cuenta
    Dado que existe un usuario con el correo "luis@refugio.org" y la contraseña "Clave123"
    Y se han realizado cinco intentos fallidos con ese correo
    Cuando inicia sesión con el correo "luis@refugio.org" y la contraseña "Clave123"
    Entonces el sistema le concede acceso

  Esquema del escenario: Inicio de sesión con campos vacíos
    Dado que el usuario está en la pantalla de inicio de sesión
    Cuando intenta iniciar sesión dejando vacío el campo "<campo>"
    Entonces el sistema no concede acceso
    Y indica que el campo "<campo>" es obligatorio

    Ejemplos:
      | campo              |
      | correo electrónico |
      | contraseña         |

  Escenario: Cierre de sesión
    Dado que un usuario ha iniciado sesión
    Cuando cierra sesión
    Entonces el sistema lo redirige a la pantalla de inicio de sesión
    Y ya no puede acceder a ninguna función sin volver a autenticarse

  Esquema del escenario: Acceso a funciones según el rol
    Dado que un usuario con rol "<rol>" ha iniciado sesión
    Cuando intenta usar la función "<función>"
    Entonces el sistema <resultado> el acceso a la función

    Ejemplos:
      | rol         | función                                 | resultado |
      | Coordinador | Registrar usuarios                      | permite   |
      | Veterinario | Registrar usuarios                      | deniega   |
      | Voluntario  | Registrar usuarios                      | deniega   |
      | Coordinador | Registrar cambio de estado del animal   | permite   |
      | Veterinario | Registrar cambio de estado del animal   | permite   |
      | Voluntario  | Registrar cambio de estado del animal   | deniega   |
      | Veterinario | Registrar aplicación de vacuna          | permite   |
      | Coordinador | Registrar aplicación de vacuna          | deniega   |
      | Coordinador | Evaluar solicitud de adopción           | permite   |
      | Veterinario | Evaluar solicitud de adopción           | deniega   |
      | Voluntario  | Registrar resultado de visita           | permite   |
      | Coordinador | Registrar resultado de visita           | deniega   |
      | Voluntario  | Consultar ficha integral del animal     | permite   |
      | Veterinario | Buscar animales                         | permite   |

  Escenario: Las funciones no permitidas no se muestran al usuario
    Dado que un usuario con rol "Voluntario" ha iniciado sesión
    Cuando visualiza el menú principal
    Entonces no ve la opción "Registrar usuarios"
    Y no ve la opción "Registrar aplicación de vacuna"

  Escenario: Intento de acceso directo a una función no permitida
    Dado que un usuario con rol "Veterinario" ha iniciado sesión
    Cuando intenta acceder directamente a la función "Registrar adopción"
    Entonces el sistema deniega el acceso
    Y muestra el mensaje "No tiene permisos para realizar esta acción"

  Escenario: Usuario sin sesión iniciada
    Dado que no hay una sesión iniciada
    Cuando se intenta acceder a cualquier función del sistema
    Entonces el sistema redirige a la pantalla de inicio de sesión
```

**Supuestos / preguntas abiertas**
- El mensaje de error es el mismo para correo inexistente y contraseña incorrecta, para no revelar qué correos están registrados.
- Confirmado fuera de alcance: bloqueo por intentos fallidos, y cambio o recuperación de contraseña.
- La tabla de ejemplos de permisos es representativa. Las pruebas completas deben cubrir toda la matriz.
