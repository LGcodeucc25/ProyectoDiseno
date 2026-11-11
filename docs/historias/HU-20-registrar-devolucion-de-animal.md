### HU-20: Registrar la devolución de un animal adoptado

> Origen: RF021

**Como** coordinador
**Quiero** registrar la devolución de un animal adoptado indicando la fecha y el motivo
**Para** reincorporar al animal al refugio, revalorarlo y conservar el historial de por qué no funcionó la adopción

**Validación INVEST**
- I — Independent: ⚠️ Requiere una adopción registrada (HU-17); se acepta por el orden natural del flujo.
- N — Negotiable: ✅ No impone el procedimiento físico de recepción.
- V — Valuable: ✅ Devuelve al animal al circuito de cuidado y deja trazabilidad del caso.
- E — Estimable: ✅ Efectos automáticos acordados.
- S — Small: ✅ Un registro con dos efectos automáticos.
- T — Testable: ✅ Estado del animal y de las visitas verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Devolución de animales adoptados

  Antecedentes:
    Dado que el coordinador ha iniciado sesión
    Y existe la adopción de "Toby" por "Carlos Ruiz" con fecha de entrega "2026-09-01"
    Y el animal "Toby" está en estado "Adoptado"

  Escenario: Registro exitoso de una devolución
    Dado que la adopción de "Toby" tiene una visita "Realizada" y dos visitas "Programadas"
    Cuando registra la devolución de "Toby" con fecha "hoy" y motivo "Alergia en la familia"
    Entonces la adopción de "Toby" queda registrada como devuelta con la fecha y el motivo
    Y el animal "Toby" pasa a estado "En valoración"
    Y las dos visitas "Programadas" pasan a estado "Cancelada"
    Y la visita "Realizada" conserva su estado y resultado

  Escenario: El animal devuelto queda sin alojamiento asignado
    Cuando registra la devolución de "Toby"
    Entonces "Toby" aparece sin alojamiento asignado
    Y puede asignarse a un espacio del refugio

  Escenario: Devolución rechazada sin motivo
    Cuando intenta registrar la devolución de "Toby" sin indicar motivo
    Entonces el sistema no registra la devolución
    Y indica que el motivo es obligatorio

  Escenario: Devolución rechazada por fecha anterior a la entrega
    Cuando intenta registrar la devolución de "Toby" con fecha "2026-08-25"
    Entonces el sistema no registra la devolución
    Y muestra el mensaje "La fecha de devolución no puede ser anterior a la fecha de entrega"

  Escenario: Devolución rechazada sobre una adopción ya devuelta
    Dado que ya se registró la devolución de esa adopción de "Toby"
    Cuando intenta registrar otra devolución de la misma adopción
    Entonces el sistema no registra la devolución
    Y muestra el mensaje "Esta adopción ya tiene una devolución registrada"
```

**Supuestos / preguntas abiertas**
- Se asumió que la fecha de devolución no puede ser futura.
- Se asumió que la adopción devuelta se conserva en el historial del animal y del adoptante.
- Se asumió que no hay plazo máximo para devolver un animal.
