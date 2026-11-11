### HU-13: Registrar una solicitud de adopción

> Origen: RF014

**Como** coordinador
**Quiero** registrar la solicitud de un adoptante para adoptar un animal disponible, indicando la fecha de la solicitud
**Para** iniciar formalmente el proceso de adopción y poder evaluarlo

**Validación INVEST**
- I — Independent: ✅ Requiere adoptantes y animales existentes, preparables como datos de prueba.
- N — Negotiable: ✅ No impone canal de recepción de la solicitud.
- V — Valuable: ✅ Es el inicio del proceso que lleva a la adopción.
- E — Estimable: ✅ Estado inicial y regla de disponibilidad definidos.
- S — Small: ✅ Un registro con validaciones.
- T — Testable: ✅ Estado de la solicitud y rechazos verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Registro de solicitudes de adopción

  Antecedentes:
    Dado que el coordinador ha iniciado sesión
    Y existe el adoptante "Carlos Ruiz"

  Escenario: Registro exitoso de una solicitud sobre un animal disponible
    Dado que el animal "Toby" está en estado "Disponible"
    Cuando registra una solicitud de "Carlos Ruiz" para adoptar a "Toby" con fecha "hoy"
    Entonces la solicitud queda en estado "Pendiente"
    Y el animal "Toby" permanece en estado "Disponible"

  Escenario: Varias solicitudes pendientes sobre el mismo animal
    Dado que el animal "Toby" está "Disponible"
    Y existe una solicitud "Pendiente" de "Ana Torres" sobre "Toby"
    Cuando registra una solicitud de "Carlos Ruiz" sobre "Toby"
    Entonces "Toby" tiene dos solicitudes en estado "Pendiente"

  Escenario: Un adoptante con solicitudes sobre animales distintos
    Dado que "Carlos Ruiz" tiene una solicitud "Pendiente" sobre "Toby"
    Y el animal "Misu" está "Disponible"
    Cuando registra una solicitud de "Carlos Ruiz" sobre "Misu"
    Entonces "Carlos Ruiz" tiene dos solicitudes en estado "Pendiente"

  Esquema del escenario: Solicitud rechazada sobre un animal no disponible
    Dado que el animal "Luna" está en estado "<estado>"
    Cuando intenta registrar una solicitud de "Carlos Ruiz" sobre "Luna"
    Entonces el sistema no registra la solicitud
    Y muestra el mensaje "Solo se pueden registrar solicitudes sobre animales disponibles"

    Ejemplos:
      | estado                 |
      | En valoración          |
      | En proceso de adopción |
      | Adoptado               |
      | No disponible          |

  Escenario: Solicitud duplicada del mismo adoptante sobre el mismo animal
    Dado que "Carlos Ruiz" tiene una solicitud "Pendiente" sobre "Toby"
    Cuando intenta registrar otra solicitud de "Carlos Ruiz" sobre "Toby"
    Entonces el sistema no registra la solicitud
    Y muestra el mensaje "El adoptante ya tiene una solicitud activa sobre este animal"

  Escenario: Solicitud rechazada por fecha futura
    Cuando intenta registrar una solicitud con fecha posterior a hoy
    Entonces el sistema no registra la solicitud
    Y muestra el mensaje "La fecha de la solicitud no puede ser futura"
```

**Supuestos / preguntas abiertas**
- Se asumió que un adoptante no puede tener dos solicitudes activas (*Pendiente* o *Aprobada*) sobre el mismo animal.
- Las solicitudes las registra el coordinador; los adoptantes no usan el sistema.
