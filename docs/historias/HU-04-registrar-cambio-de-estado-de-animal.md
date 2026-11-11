### HU-04: Registrar un cambio de estado de un animal

> Origen: RF005

**Como** coordinador o veterinario
**Quiero** registrar el cambio de estado de un animal indicando el nuevo estado, la fecha del cambio y el motivo
**Para** reflejar la situación real del animal y conservar la trazabilidad de quién y cuándo cambió su estado

**Validación INVEST**
- I — Independent: ✅ Solo requiere un animal existente.
- N — Negotiable: ✅ Define las reglas de transición, no su implementación.
- V — Valuable: ✅ El estado determina si un animal puede adoptarse; un estado incorrecto genera adopciones inválidas.
- E — Estimable: ✅ Estados y transiciones están acordados.
- S — Small: ✅ Un registro con validación de transiciones.
- T — Testable: ✅ Cada transición está permitida, reservada a un flujo o prohibida.

**Estados y transiciones acordados**

| Desde | Hacia | Cómo |
|---|---|---|
| En valoración | Disponible, No disponible | Manual (esta historia) |
| Disponible | En valoración, No disponible | Manual (esta historia) |
| Disponible | En proceso de adopción | Automático al aprobar una solicitud (HU-14) |
| En proceso de adopción | Disponible | Automático, únicamente cuando el adoptante cancela la solicitud aprobada (HU-15) |
| En proceso de adopción | Adoptado | Automático al registrar la adopción (HU-17) |
| Adoptado | En valoración | Automático al registrar la devolución (HU-20) |
| Adoptado | No disponible | Manual (esta historia) |
| No disponible | — | Estado terminal |

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Cambio de estado del animal

  Esquema del escenario: Transiciones manuales permitidas
    Dado que un usuario con rol "<rol>" ha iniciado sesión
    Y el animal "Luna" está en estado "<estado actual>"
    Cuando registra el cambio de "Luna" al estado "<nuevo estado>" con fecha "hoy" y motivo "<motivo>"
    Entonces el animal "Luna" queda en estado "<nuevo estado>"
    Y el historial de estados de "Luna" registra el estado anterior, el nuevo estado, la fecha, el motivo y el usuario responsable

    Ejemplos:
      | rol         | estado actual | nuevo estado  | motivo                    |
      | Veterinario | En valoración | Disponible    | Valoración médica apta    |
      | Coordinador | En valoración | No disponible | Traslado a otro refugio   |
      | Veterinario | Disponible    | En valoración | Presenta síntomas         |
      | Coordinador | Disponible    | No disponible | Fallecimiento             |
      | Coordinador | Adoptado      | No disponible | Fallecimiento reportado   |

  Esquema del escenario: Transiciones reservadas a los flujos de adopción
    Dado que el coordinador ha iniciado sesión
    Y el animal "Luna" está en estado "<estado actual>"
    Cuando intenta registrar manualmente el cambio de "Luna" al estado "<nuevo estado>"
    Entonces el sistema no registra el cambio
    Y muestra el mensaje "Este cambio de estado se realiza desde el proceso de adopción"

    Ejemplos:
      | estado actual          | nuevo estado           |
      | Disponible             | En proceso de adopción |
      | En proceso de adopción | Adoptado               |
      | En proceso de adopción | Disponible             |
      | Adoptado               | En valoración          |

  Esquema del escenario: Transiciones no permitidas
    Dado que el coordinador ha iniciado sesión
    Y el animal "Luna" está en estado "<estado actual>"
    Cuando intenta registrar el cambio de "Luna" al estado "<nuevo estado>"
    Entonces el sistema no registra el cambio
    Y muestra el mensaje "Transición de estado no permitida"

    Ejemplos:
      | estado actual | nuevo estado           |
      | En valoración | En proceso de adopción |
      | En valoración | Adoptado               |
      | Disponible    | Adoptado               |
      | No disponible | Disponible             |
      | No disponible | En valoración          |

  Escenario: Paso a No disponible sin motivo
    Dado que el veterinario ha iniciado sesión
    Y el animal "Luna" está en estado "Disponible"
    Cuando intenta registrar el cambio de "Luna" a "No disponible" sin indicar motivo
    Entonces el sistema no registra el cambio
    Y indica que el motivo es obligatorio para el estado "No disponible"

  Esquema del escenario: Las solicitudes pendientes se rechazan cuando el animal deja de estar disponible
    Dado que el coordinador ha iniciado sesión
    Y el animal "Toby" está en estado "Disponible"
    Y existen solicitudes "Pendientes" de "Carlos Ruiz" y de "Ana Torres" sobre "Toby"
    Cuando registra el cambio de "Toby" al estado "<nuevo estado>"
    Entonces las solicitudes de "Carlos Ruiz" y de "Ana Torres" pasan a estado "Rechazada"
    Y ambas registran el motivo "Animal no disponible"

    Ejemplos:
      | nuevo estado  |
      | En valoración |
      | No disponible |

  Escenario: Paso a No disponible libera el espacio de alojamiento
    Dado que el coordinador ha iniciado sesión
    Y el animal "Luna" está alojado en el espacio "Caniles A" con ocupación 3 de 5
    Cuando registra el cambio de "Luna" a "No disponible" con motivo "Fallecimiento"
    Entonces "Luna" deja de estar alojada en "Caniles A"
    Y la ocupación de "Caniles A" pasa a 2 de 5

  Escenario: Cambio de estado con fecha futura
    Dado que el coordinador ha iniciado sesión
    Cuando intenta registrar un cambio de estado con fecha posterior a hoy
    Entonces el sistema no registra el cambio
    Y muestra el mensaje "La fecha del cambio no puede ser futura"

  Escenario: Voluntario no puede cambiar el estado
    Dado que un voluntario ha iniciado sesión
    Cuando intenta registrar un cambio de estado de un animal
    Entonces el sistema deniega el acceso
```

**Supuestos / preguntas abiertas**
- El usuario responsable es siempre el de la sesión iniciada.
- Se asumió que el motivo es obligatorio para *No disponible* (porque ahí se registra si fue fallecimiento o traslado) y opcional para los demás estados.
- Confirmado: si un animal con solicitudes *Pendientes* pasa a *En valoración* o *No disponible*, esas solicitudes se rechazan automáticamente con el motivo "Animal no disponible".
- Confirmado: el refugio no revierte una solicitud ya aprobada (fuera de alcance). *En proceso de adopción* → *Disponible* ocurre solo cuando el adoptante cancela.
