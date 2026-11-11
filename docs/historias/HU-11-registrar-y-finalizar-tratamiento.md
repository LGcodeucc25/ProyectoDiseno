### HU-11: Registrar un tratamiento médico y su finalización

> Origen: RF012

**Como** veterinario
**Quiero** registrar un tratamiento médico de un animal indicando el diagnóstico, la fecha de inicio y la fecha de finalización, que puedo completar después si aún no se conoce
**Para** dejar constancia de la atención médica del animal y saber con certeza si sigue en tratamiento

**Validación INVEST**
- I — Independent: ✅ Requiere un animal existente.
- N — Negotiable: ✅ No impone estructura del diagnóstico más allá de los datos requeridos.
- V — Valuable: ✅ Permite el seguimiento médico y saber si un animal tiene un tratamiento activo.
- E — Estimable: ✅ Datos y reglas de fechas definidos.
- S — Small: ✅ Un registro y su cierre posterior sobre la misma entidad.
- T — Testable: ✅ Registro, cierre, indicador de tratamiento activo y validaciones verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Registro y finalización de tratamientos médicos

  Antecedentes:
    Dado que el veterinario "Luis Pérez" ha iniciado sesión

  Escenario: Registro de un tratamiento en curso sin fecha de finalización
    Cuando registra para el animal "Luna" un tratamiento con diagnóstico "Dermatitis" y fecha de inicio "hoy" sin fecha de finalización
    Entonces el historial médico de "Luna" muestra el tratamiento "Dermatitis"
    Y el veterinario responsable registrado es "Luis Pérez"
    Y la ficha de "Luna" indica que tiene un tratamiento activo

  Escenario: Registro de un tratamiento con fecha de finalización
    Cuando registra para el animal "Toby" un tratamiento con diagnóstico "Otitis", fecha de inicio "2026-09-10" y fecha de finalización "2026-09-20"
    Entonces el historial médico de "Toby" muestra el tratamiento "Otitis" con ambas fechas

  Escenario: Finalización posterior de un tratamiento abierto
    Dado que el animal "Luna" tiene un tratamiento "Dermatitis" iniciado el "2026-10-01" sin fecha de finalización
    Cuando registra la fecha de finalización "2026-10-20" para ese tratamiento
    Entonces el tratamiento "Dermatitis" muestra la fecha de finalización "2026-10-20"
    Y la ficha de "Luna" indica que no tiene tratamientos activos

  Escenario: Un tratamiento ya finalizado no se vuelve a cerrar
    Dado que el animal "Toby" tiene un tratamiento "Otitis" con fecha de finalización "2026-09-20"
    Cuando consulta las acciones disponibles sobre el tratamiento "Otitis"
    Entonces no está disponible la acción "Registrar finalización"

  Escenario: Fecha de finalización anterior al inicio
    Dado que el animal "Luna" tiene un tratamiento iniciado el "2026-10-01"
    Cuando intenta registrar, al crearlo o después, la fecha de finalización "2026-09-25"
    Entonces el sistema no guarda la fecha
    Y muestra el mensaje "La fecha de finalización no puede ser anterior a la fecha de inicio"

  Escenario: Registro rechazado por fecha de inicio futura
    Cuando intenta registrar un tratamiento con fecha de inicio posterior a hoy
    Entonces el sistema no registra el tratamiento
    Y muestra el mensaje "La fecha de inicio no puede ser futura"

  Esquema del escenario: Registro rechazado por campo obligatorio vacío
    Cuando intenta registrar un tratamiento dejando vacío el campo "<campo>"
    Entonces el sistema no registra el tratamiento
    Y indica que el campo "<campo>" es obligatorio

    Ejemplos:
      | campo           |
      | diagnóstico     |
      | fecha de inicio |
```

**Supuestos / preguntas abiertas**
- El veterinario responsable es el de la sesión iniciada.
- Registrar un tratamiento no cambia el estado del animal. Que está en tratamiento se sabe por si tiene uno activo.
- Se asumió que cualquier veterinario puede cerrar el tratamiento, no solo quien lo registró, y que una fecha de finalización ya registrada no se corrige.
- Confirmado: la fecha de inicio no puede ser futura, porque un tratamiento se registra cuando empieza. La fecha de finalización queda abierta y se completa después.
