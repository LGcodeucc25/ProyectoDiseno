### HU-15: Registrar la cancelación de una solicitud por decisión del adoptante

> Origen: RF016

**Como** coordinador
**Quiero** registrar la cancelación de una solicitud de adopción cuando el adoptante desiste, indicando la fecha y el motivo
**Para** liberar al animal para otros adoptantes y dejar constancia de por qué no se concretó la adopción

**Validación INVEST**
- I — Independent: ⚠️ Requiere solicitudes registradas (HU-13) y, para el caso aprobado, la evaluación (HU-14); se acepta por el orden natural del flujo.
- N — Negotiable: ✅ No impone el canal por el que el adoptante comunica su decisión.
- V — Valuable: ✅ Evita que un animal quede bloqueado en proceso de adopción sin adoptante.
- E — Estimable: ✅ Estados cancelables y efecto sobre el animal acordados.
- S — Small: ✅ Un registro con un efecto automático.
- T — Testable: ✅ Estados resultantes verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Cancelación de solicitudes de adopción

  Antecedentes:
    Dado que el coordinador ha iniciado sesión

  Escenario: Cancelación de una solicitud pendiente
    Dado que el animal "Toby" está "Disponible"
    Y existe una solicitud "Pendiente" de "Carlos Ruiz" sobre "Toby"
    Cuando registra la cancelación de esa solicitud con fecha "hoy" y motivo "Cambio de ciudad"
    Entonces la solicitud queda en estado "Cancelada" con la fecha y el motivo registrados
    Y el animal "Toby" permanece en estado "Disponible"

  Escenario: Cancelación de una solicitud aprobada antes de la entrega
    Dado que existe una solicitud "Aprobada" de "Carlos Ruiz" sobre "Toby"
    Y el animal "Toby" está "En proceso de adopción"
    Y no se ha registrado la adopción de "Toby"
    Cuando registra la cancelación de esa solicitud con fecha "hoy" y motivo "Problemas económicos"
    Entonces la solicitud queda en estado "Cancelada"
    Y el animal "Toby" vuelve a estado "Disponible"

  Esquema del escenario: Cancelación no permitida
    Dado que existe una solicitud de "Carlos Ruiz" sobre "Toby" en estado "<estado>"
    Y <situación>
    Cuando intenta registrar la cancelación de esa solicitud
    Entonces el sistema no registra la cancelación
    Y muestra el mensaje "<mensaje>"

    Ejemplos:
      | estado    | situación                                  | mensaje                                                         |
      | Rechazada | no hay otra condición                      | Solo se pueden cancelar solicitudes pendientes o aprobadas      |
      | Cancelada | no hay otra condición                      | Solo se pueden cancelar solicitudes pendientes o aprobadas      |
      | Aprobada  | ya se registró la adopción de "Toby"       | No se puede cancelar una solicitud cuya adopción ya se registró |

  Escenario: Cancelación rechazada sin motivo
    Dado que existe una solicitud "Pendiente" de "Carlos Ruiz" sobre "Toby"
    Cuando intenta registrar la cancelación sin indicar motivo
    Entonces el sistema no registra la cancelación
    Y indica que el motivo es obligatorio
```

**Supuestos / preguntas abiertas**
- La cancelación es por decisión del adoptante y la registra el coordinador.
- Confirmado: *En proceso de adopción* → *Disponible* ocurre únicamente por cancelación del adoptante. Que el refugio revierta una solicitud ya aprobada queda fuera de alcance.
- Se asumió que la fecha de cancelación no puede ser futura ni anterior a la solicitud.
