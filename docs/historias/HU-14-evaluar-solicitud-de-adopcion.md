### HU-14: Evaluar una solicitud de adopción

> Origen: RF015

**Como** coordinador
**Quiero** registrar la evaluación de una solicitud de adopción indicando la fecha, el resultado (*Aprobada* o *Rechazada*) y mis observaciones
**Para** decidir de forma documentada qué adoptante recibe al animal

**Validación INVEST**
- I — Independent: ⚠️ Requiere solicitudes registradas (HU-13); se acepta por ser el orden natural del flujo de adopción.
- N — Negotiable: ✅ No impone criterios de evaluación, solo el registro del resultado.
- V — Valuable: ✅ Formaliza la decisión y bloquea al animal para un único adoptante.
- E — Estimable: ✅ Resultados y efectos automáticos acordados.
- S — Small: ✅ Un registro con dos efectos automáticos claros.
- T — Testable: ✅ Estados de solicitudes y del animal verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Evaluación de solicitudes de adopción

  Antecedentes:
    Dado que el coordinador ha iniciado sesión
    Y el animal "Toby" está en estado "Disponible"

  Escenario: Aprobación de una solicitud
    Dado que existen solicitudes "Pendientes" de "Carlos Ruiz" y de "Ana Torres" sobre "Toby"
    Cuando evalúa la solicitud de "Carlos Ruiz" con resultado "Aprobada", fecha "hoy" y observaciones "Hogar adecuado"
    Entonces la solicitud de "Carlos Ruiz" queda en estado "Aprobada"
    Y el animal "Toby" pasa a estado "En proceso de adopción"
    Y la solicitud de "Ana Torres" pasa automáticamente a estado "Rechazada"
    Y la solicitud de "Ana Torres" registra la observación "Rechazada automáticamente por aprobación de otra solicitud"

  Escenario: Rechazo de una solicitud
    Dado que existe una solicitud "Pendiente" de "Carlos Ruiz" sobre "Toby"
    Cuando evalúa la solicitud con resultado "Rechazada", fecha "hoy" y observaciones "Vivienda sin espacio suficiente"
    Entonces la solicitud de "Carlos Ruiz" queda en estado "Rechazada"
    Y el animal "Toby" permanece en estado "Disponible"

  Esquema del escenario: Solo se evalúan solicitudes pendientes
    Dado que existe una solicitud de "Carlos Ruiz" sobre "Toby" en estado "<estado>"
    Cuando intenta evaluar esa solicitud
    Entonces el sistema no registra la evaluación
    Y muestra el mensaje "Solo se pueden evaluar solicitudes pendientes"

    Ejemplos:
      | estado    |
      | Aprobada  |
      | Rechazada |
      | Cancelada |

  Escenario: Evaluación rechazada sin observaciones
    Dado que existe una solicitud "Pendiente" de "Carlos Ruiz" sobre "Toby"
    Cuando intenta evaluar la solicitud sin observaciones
    Entonces el sistema no registra la evaluación
    Y indica que las observaciones son obligatorias

  Escenario: Evaluación rechazada por fecha anterior a la solicitud
    Dado que existe una solicitud "Pendiente" de "Carlos Ruiz" con fecha "2026-10-15"
    Cuando intenta evaluarla con fecha "2026-10-10"
    Entonces el sistema no registra la evaluación
    Y muestra el mensaje "La fecha de evaluación no puede ser anterior a la fecha de la solicitud"

  Escenario: Las solicitudes rechazadas automáticamente no se reabren si se cancela la aprobada
    Dado que la solicitud de "Carlos Ruiz" sobre "Toby" fue aprobada
    Y la solicitud de "Ana Torres" sobre "Toby" fue rechazada automáticamente por esa aprobación
    Cuando se registra la cancelación de la solicitud de "Carlos Ruiz"
    Entonces el animal "Toby" vuelve a estado "Disponible"
    Y la solicitud de "Ana Torres" sigue en estado "Rechazada"
    Y "Ana Torres" puede registrar una nueva solicitud sobre "Toby"
```

**Supuestos / preguntas abiertas**
- Se asumió que las observaciones son obligatorias en ambos resultados, para dejar documentada la decisión.
- Se asumió que la fecha de evaluación no puede ser anterior a la solicitud ni futura.
- Confirmado: si después se cancela la solicitud aprobada (HU-15), las que se rechazaron automáticamente siguen *Rechazadas*. Reabrirlas obligaría a revertir decisiones ya comunicadas. Quien siga interesado registra una solicitud nueva (HU-13).
