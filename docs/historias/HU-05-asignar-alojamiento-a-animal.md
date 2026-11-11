### HU-05: Asignar un animal a un espacio de alojamiento

> Origen: RF006

**Como** coordinador
**Quiero** registrar en qué espacio de alojamiento del refugio está ubicado un animal, indicando la fecha de asignación
**Para** saber dónde se encuentra cada animal y no exceder la capacidad de los espacios

**Validación INVEST**
- I — Independent: ✅ Requiere animales y espacios existentes, preparables como datos de prueba.
- N — Negotiable: ✅ No impone cómo se representa el espacio.
- V — Valuable: ✅ Evita hacinamiento y permite ubicar rápidamente a cada animal.
- E — Estimable: ✅ Reglas de capacidad, historial y liberación definidas.
- S — Small: ✅ Asignación y reubicación con una validación de capacidad.
- T — Testable: ✅ Ocupación y rechazos son verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Asignación de alojamiento

  Antecedentes:
    Dado que el coordinador ha iniciado sesión

  Escenario: Asignación de un animal a un espacio con cupo
    Dado que el animal "Toby" no tiene alojamiento asignado
    Y el espacio "Caniles A" tiene capacidad 5 y ocupación 3
    Cuando asigna "Toby" al espacio "Caniles A" con fecha de asignación "hoy"
    Entonces "Toby" queda alojado en "Caniles A"
    Y la ocupación de "Caniles A" pasa a 4 de 5

  Escenario: Asignación rechazada por espacio lleno
    Dado que el espacio "Gatera 1" tiene capacidad 4 y ocupación 4
    Cuando intenta asignar el animal "Misu" al espacio "Gatera 1"
    Entonces el sistema no registra la asignación
    Y muestra el mensaje "El espacio no tiene cupo disponible"

  Escenario: Reubicación de un animal a otro espacio
    Dado que el animal "Toby" está alojado en "Caniles A" con ocupación 4 de 5
    Y el espacio "Caniles B" tiene ocupación 1 de 5
    Cuando asigna "Toby" al espacio "Caniles B" con fecha de asignación "hoy"
    Entonces "Toby" queda alojado en "Caniles B"
    Y la ocupación de "Caniles A" pasa a 3 de 5
    Y la ocupación de "Caniles B" pasa a 2 de 5
    Y el historial de ubicaciones de "Toby" conserva su estancia en "Caniles A" con sus fechas

  Esquema del escenario: Asignación rechazada para animales fuera del refugio
    Dado que el animal "Rex" está en estado "<estado>"
    Cuando intenta asignar "Rex" a un espacio con cupo
    Entonces el sistema no registra la asignación
    Y muestra el mensaje "El animal no se encuentra en el refugio"

    Ejemplos:
      | estado        |
      | Adoptado      |
      | No disponible |

  Escenario: Asignación rechazada por fecha futura
    Cuando intenta asignar un animal a un espacio con fecha de asignación posterior a hoy
    Entonces el sistema no registra la asignación
    Y muestra el mensaje "La fecha de asignación no puede ser futura"
```

**Supuestos / preguntas abiertas**
- Los espacios y su capacidad se administran en HU-06.
- Se asumió que no se puede alojar a un animal *Adoptado* ni *No disponible*. Un animal devuelto (HU-20) vuelve sin alojamiento y se le asigna uno con esta historia.
- La liberación automática del espacio al adoptar o al pasar a *No disponible* está en HU-17 y HU-04.
