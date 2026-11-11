### HU-19: Registrar el resultado de una visita de seguimiento

> Origen: RF020

**Como** voluntario
**Quiero** registrar el resultado de una visita de seguimiento que tengo asignada, indicando la fecha en que la realicé, una calificación y mis observaciones sobre el estado del animal
**Para** que el refugio conozca cómo está el animal y pueda actuar si hay alertas

**Validación INVEST**
- I — Independent: ⚠️ Requiere visitas programadas (HU-18); se acepta por el orden natural del flujo.
- N — Negotiable: ✅ No impone formato del informe más allá de los datos requeridos.
- V — Valuable: ✅ Documenta el bienestar del animal y señala casos de riesgo.
- E — Estimable: ✅ Datos y calificaciones definidos.
- S — Small: ✅ Un registro con validaciones.
- T — Testable: ✅ Estado de la visita y permisos verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Resultado de visitas de seguimiento

  Antecedentes:
    Dado que la voluntaria "Marta Ríos" ha iniciado sesión
    Y existe una visita "Programada" de la adopción de "Toby" asignada a "Marta Ríos"

  Esquema del escenario: Registro exitoso del resultado de una visita
    Cuando registra el resultado de la visita con fecha de realización "hoy", calificación "<calificación>" y observaciones "<observaciones>"
    Entonces la visita queda en estado "Realizada"
    Y la ficha de "Toby" muestra la visita con calificación "<calificación>" y las observaciones registradas

    Ejemplos:
      | calificación  | observaciones                                |
      | Satisfactoria | Animal sano, buen peso y ambiente adecuado   |
      | Con alertas   | Animal sin vacuna de refuerzo y bajo de peso |

  Escenario: Un voluntario solo registra sus propias visitas
    Dado que existe una visita "Programada" asignada al voluntario "Jorge León"
    Cuando "Marta Ríos" intenta registrar el resultado de esa visita
    Entonces el sistema no registra el resultado
    Y muestra el mensaje "Solo el voluntario asignado puede registrar el resultado de esta visita"

  Esquema del escenario: Registro rechazado sobre una visita no programada
    Dado que la visita asignada a "Marta Ríos" está en estado "<estado>"
    Cuando intenta registrar su resultado
    Entonces el sistema no registra el resultado
    Y muestra el mensaje "Solo se puede registrar el resultado de visitas programadas"

    Ejemplos:
      | estado    |
      | Realizada |
      | Cancelada |

  Escenario: Registro rechazado por fecha de realización futura
    Cuando intenta registrar el resultado con fecha de realización posterior a hoy
    Entonces el sistema no registra el resultado
    Y muestra el mensaje "La fecha de realización no puede ser futura"

  Esquema del escenario: Registro rechazado por dato obligatorio vacío
    Cuando intenta registrar el resultado sin indicar "<campo>"
    Entonces el sistema no registra el resultado
    Y indica que "<campo>" es obligatorio

    Ejemplos:
      | campo                |
      | fecha de realización |
      | calificación         |
      | observaciones        |
```

**Supuestos / preguntas abiertas**
- Se asumió que la fecha de realización puede ser distinta de la fecha prevista, pero no anterior a la fecha de entrega.
- Confirmado: una visita *Con alertas* no dispara ninguna acción automática. Queda registrada y visible en la ficha del animal (HU-08).
