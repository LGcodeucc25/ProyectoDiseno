### HU-01: Registrar usuario del refugio

> Origen: RF001

**Como** coordinador
**Quiero** registrar a los usuarios del refugio con su identificación, nombre, correo electrónico, rol y una contraseña inicial
**Para** que cada miembro del equipo pueda acceder al sistema con las funciones que le corresponden

**Validación INVEST**
- I — Independent: ✅ Es la base del resto del sistema; no depende de otra historia.
- N — Negotiable: ✅ Define los datos requeridos, no cómo se almacenan ni cómo se protege la contraseña.
- V — Valuable: ✅ Sin usuarios registrados nadie puede operar el sistema.
- E — Estimable: ✅ Campos, roles y reglas de unicidad están definidos.
- S — Small: ✅ Un formulario con validaciones; cabe en un sprint.
- T — Testable: ✅ Registro exitoso, duplicados y campos faltantes son verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Registro de usuarios del refugio

  Esquema del escenario: Registro exitoso de un usuario con cada rol
    Dado que el coordinador ha iniciado sesión
    Y no existe ningún usuario con el correo "<correo>"
    Cuando registra un usuario con identificación "<identificación>", nombre "<nombre>", correo "<correo>", rol "<rol>" y una contraseña inicial
    Entonces el sistema guarda el usuario con el rol "<rol>"
    Y muestra el mensaje "Usuario registrado correctamente"
    Y el usuario puede iniciar sesión con el correo "<correo>" y la contraseña inicial

    Ejemplos:
      | identificación | nombre        | correo               | rol          |
      | 1001           | Ana Gómez     | ana@refugio.org      | Coordinador  |
      | 1002           | Luis Pérez    | luis@refugio.org     | Veterinario  |
      | 1003           | Marta Ríos    | marta@refugio.org    | Voluntario   |

  Escenario: Registro rechazado por correo duplicado
    Dado que el coordinador ha iniciado sesión
    Y ya existe un usuario con el correo "ana@refugio.org"
    Cuando intenta registrar otro usuario con el correo "ana@refugio.org"
    Entonces el sistema no registra el usuario
    Y muestra el mensaje "Ya existe un usuario con ese correo electrónico"

  Escenario: Registro rechazado por identificación duplicada
    Dado que el coordinador ha iniciado sesión
    Y ya existe un usuario con la identificación "1001"
    Cuando intenta registrar otro usuario con la identificación "1001"
    Entonces el sistema no registra el usuario
    Y muestra el mensaje "Ya existe un usuario con esa identificación"

  Esquema del escenario: Registro rechazado por campo obligatorio vacío
    Dado que el coordinador ha iniciado sesión
    Cuando intenta registrar un usuario dejando vacío el campo "<campo>"
    Entonces el sistema no registra el usuario
    Y indica que el campo "<campo>" es obligatorio

    Ejemplos:
      | campo               |
      | identificación      |
      | nombre              |
      | correo electrónico  |
      | rol                 |
      | contraseña inicial  |

  Escenario: Registro rechazado por correo con formato inválido
    Dado que el coordinador ha iniciado sesión
    Cuando intenta registrar un usuario con el correo "ana.refugio.org"
    Entonces el sistema no registra el usuario
    Y muestra el mensaje "El correo electrónico no tiene un formato válido"
```

**Supuestos / preguntas abiertas**
- Se confirmó que el correo es único. Se asumió que la identificación del usuario también lo es.
- Todos los campos se consideran obligatorios.
- Los textos de los mensajes son orientativos.
- La edición, desactivación y cambio de contraseña de usuarios no forman parte de los requerimientos y quedan fuera de alcance.
