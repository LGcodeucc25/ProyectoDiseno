### HU-17: Registrar una adopción

> Origen: RF018

**Como** coordinador
**Quiero** registrar la adopción a partir de una solicitud aprobada, indicando la fecha de entrega del animal
**Para** formalizar la entrega del animal a su nuevo hogar y habilitar su seguimiento

**Validación INVEST**
- I — Independent: ⚠️ Requiere una solicitud aprobada (HU-14); se acepta por el orden natural del flujo.
- N — Negotiable: ✅ No impone documentos ni formato del acta de entrega.
- V — Valuable: ✅ Es el resultado final del proceso y libera capacidad del refugio.
- E — Estimable: ✅ Efectos automáticos acordados.
- S — Small: ✅ Un registro con dos efectos automáticos.
- T — Testable: ✅ Estado del animal y ocupación verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Registro de adopciones

  Antecedentes:
    Dado que el coordinador ha iniciado sesión

  Escenario: Registro exitoso de una adopción
    Dado que existe una solicitud "Aprobada" de "Carlos Ruiz" sobre "Toby"
    Y el animal "Toby" está "En proceso de adopción"
    Y "Toby" está alojado en "Caniles A" con ocupación 4 de 5
    Cuando registra la adopción de esa solicitud con fecha de entrega "hoy"
    Entonces se registra la adopción de "Toby" por "Carlos Ruiz" con fecha de entrega "hoy"
    Y el animal "Toby" pasa a estado "Adoptado"
    Y "Toby" deja de estar alojado en "Caniles A"
    Y la ocupación de "Caniles A" pasa a 3 de 5

  Esquema del escenario: Adopción rechazada sin solicitud aprobada
    Dado que existe una solicitud de "Carlos Ruiz" sobre "Toby" en estado "<estado>"
    Cuando intenta registrar una adopción a partir de esa solicitud
    Entonces el sistema no registra la adopción
    Y muestra el mensaje "Solo se puede registrar la adopción de una solicitud aprobada"

    Ejemplos:
      | estado    |
      | Pendiente |
      | Rechazada |
      | Cancelada |

  Escenario: Adopción ya registrada para la solicitud
    Dado que ya se registró la adopción de la solicitud aprobada de "Carlos Ruiz" sobre "Toby"
    Cuando intenta registrar otra adopción para esa misma solicitud
    Entonces el sistema no registra la adopción
    Y muestra el mensaje "Esta solicitud ya tiene una adopción registrada"

  Escenario: Adopción rechazada por fecha de entrega inválida
    Dado que la solicitud de "Carlos Ruiz" fue aprobada el "2026-10-20"
    Cuando intenta registrar la adopción con fecha de entrega "2026-10-15"
    Entonces el sistema no registra la adopción
    Y muestra el mensaje "La fecha de entrega no puede ser anterior a la aprobación de la solicitud"
```

**Supuestos / preguntas abiertas**
- Se asumió que la fecha de entrega no puede ser anterior a la aprobación ni futura.
- Se asumió que la solicitud sigue *Aprobada* tras registrar la adopción. La adopción queda como un registro aparte, ligado a la solicitud.
