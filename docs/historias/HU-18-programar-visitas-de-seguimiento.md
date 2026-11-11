### HU-18: Programar, reprogramar y cancelar visitas de seguimiento

> Origen: RF019

**Como** coordinador
**Quiero** programar las visitas de seguimiento de una adopción indicando la fecha prevista y el voluntario asignado, y reprogramarlas o cancelarlas cuando haga falta
**Para** verificar que el animal adoptado está bien cuidado en su nuevo hogar

**Validación INVEST**
- I — Independent: ⚠️ Requiere adopciones registradas (HU-17) y usuarios voluntarios (HU-01); se acepta por el orden natural del flujo.
- N — Negotiable: ✅ No impone frecuencia ni canal de aviso al voluntario.
- V — Valuable: ✅ Asegura el bienestar posadopción y detecta a tiempo situaciones de riesgo.
- E — Estimable: ✅ Sin regla fija de cantidad; datos y operaciones definidos.
- S — Small: ✅ Gestión de una sola entidad (la visita) por un solo actor.
- T — Testable: ✅ Visitas programadas, reprogramadas, canceladas y rechazos verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Gestión de visitas de seguimiento

  Antecedentes:
    Dado que el coordinador ha iniciado sesión
    Y existe la adopción de "Toby" por "Carlos Ruiz" con fecha de entrega "2026-10-01"
    Y existen los usuarios "Marta Ríos" y "Jorge León" con rol "Voluntario"

  Escenario: Programación exitosa de una visita
    Cuando programa una visita de seguimiento de la adopción de "Toby" con fecha prevista "2026-11-01" y voluntario "Marta Ríos"
    Entonces la visita queda en estado "Programada"
    Y la visita aparece en la ficha de "Toby" con fecha prevista "2026-11-01" y voluntario "Marta Ríos"

  Escenario: Varias visitas para una misma adopción
    Dado que la adopción de "Toby" ya tiene una visita programada para "2026-11-01"
    Cuando programa otra visita para "2026-12-01"
    Entonces la adopción de "Toby" tiene dos visitas programadas

  Escenario: Solo se pueden asignar usuarios con rol voluntario
    Cuando abre la programación de una visita
    Entonces la lista de responsables muestra únicamente usuarios con rol "Voluntario"

  Escenario: Reprogramación de una visita
    Dado que existe una visita "Programada" de "Toby" para "2026-11-01" asignada a "Marta Ríos"
    Cuando la reprograma para "2026-11-08" con el voluntario "Jorge León"
    Entonces la visita sigue en estado "Programada"
    Y tiene fecha prevista "2026-11-08" y voluntario asignado "Jorge León"

  Escenario: Cancelación de una visita
    Dado que existe una visita "Programada" de "Toby" para "2026-11-01"
    Cuando cancela la visita indicando el motivo "El adoptante cambió de ciudad temporalmente"
    Entonces la visita queda en estado "Cancelada" con el motivo registrado
    Y deja de aparecer entre las visitas pendientes del voluntario asignado

  Esquema del escenario: Solo se reprograman o cancelan visitas programadas
    Dado que existe una visita de "Toby" en estado "<estado>"
    Cuando intenta <acción> esa visita
    Entonces el sistema no modifica la visita
    Y muestra el mensaje "Solo se pueden modificar visitas programadas"

    Ejemplos:
      | estado    | acción      |
      | Realizada | reprogramar |
      | Realizada | cancelar    |
      | Cancelada | reprogramar |
      | Cancelada | cancelar    |

  Escenario: Programación rechazada por fecha anterior a la entrega
    Cuando intenta programar o reprogramar una visita con fecha prevista "2026-09-25"
    Entonces el sistema no guarda la visita
    Y muestra el mensaje "La fecha prevista no puede ser anterior a la fecha de entrega"

  Escenario: Programación rechazada sobre una adopción con devolución
    Dado que se registró la devolución de "Toby"
    Cuando intenta programar una visita de seguimiento de esa adopción
    Entonces el sistema no programa la visita
    Y muestra el mensaje "No se pueden programar visitas de una adopción con devolución"

  Esquema del escenario: Programación rechazada por dato obligatorio vacío
    Cuando intenta programar una visita sin indicar "<campo>"
    Entonces el sistema no programa la visita
    Y indica que "<campo>" es obligatorio

    Ejemplos:
      | campo               |
      | fecha prevista      |
      | voluntario asignado |
```

**Supuestos / preguntas abiertas**
- La cantidad de visitas la decide el coordinador, sin regla fija (confirmado).
- Una visita pasa por los estados *Programada*, *Realizada* (HU-19) y *Cancelada*, ya sea manualmente en esta historia o por devolución (HU-20).
- Se asumió que la cancelación manual exige un motivo y que reprogramar no deja historial de la fecha anterior.
