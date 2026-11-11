### HU-21: Asignar un voluntario responsable del cuidado de animales

> Origen: RF022

**Como** coordinador
**Quiero** asignar un voluntario como responsable del cuidado de uno o varios animales que estén en el refugio, indicando la fecha de asignación
**Para** que cada animal tenga una persona a cargo de su atención diaria

**Validación INVEST**
- I — Independent: ✅ Requiere animales y voluntarios existentes, preparables como datos de prueba.
- N — Negotiable: ✅ No impone cómo se seleccionan los animales.
- V — Valuable: ✅ Evita que algún animal quede sin cuidado y deja claro quién responde por él.
- E — Estimable: ✅ Regla de un responsable por animal, estados permitidos e historial acordados.
- S — Small: ✅ Una asignación con reemplazo e historial.
- T — Testable: ✅ Responsable actual, historial y rechazos verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Asignación de voluntario responsable

  Antecedentes:
    Dado que el coordinador ha iniciado sesión
    Y existen los usuarios "Marta Ríos" y "Jorge León" con rol "Voluntario"

  Escenario: Asignación de un voluntario a varios animales
    Dado que los animales "Toby", "Luna" y "Misu" están en el refugio y no tienen voluntario responsable
    Cuando asigna a "Marta Ríos" como responsable de "Toby", "Luna" y "Misu" con fecha "hoy"
    Entonces "Marta Ríos" queda como voluntaria responsable de los tres animales desde "hoy"

  Escenario: Una nueva asignación reemplaza a la anterior y queda en el historial
    Dado que "Marta Ríos" es la voluntaria responsable de "Toby" desde "2026-09-01"
    Cuando asigna a "Jorge León" como responsable de "Toby" con fecha "2026-10-15"
    Entonces "Jorge León" queda como único voluntario responsable de "Toby"
    Y el historial de responsables de "Toby" muestra a "Marta Ríos" del "2026-09-01" al "2026-10-15"
    Y muestra a "Jorge León" desde el "2026-10-15"

  Esquema del escenario: Asignación permitida a animales en el refugio
    Dado que el animal "Toby" está en estado "<estado>"
    Cuando asigna a "Marta Ríos" como responsable de "Toby"
    Entonces "Marta Ríos" queda como voluntaria responsable de "Toby"

    Ejemplos:
      | estado                 |
      | En valoración          |
      | Disponible             |
      | En proceso de adopción |

  Esquema del escenario: Asignación rechazada a animales fuera del refugio
    Dado que el animal "Rex" está en estado "<estado>"
    Cuando intenta asignar a "Marta Ríos" como responsable de "Rex"
    Entonces el sistema no registra la asignación
    Y muestra el mensaje "Solo se puede asignar voluntario a animales que están en el refugio"

    Ejemplos:
      | estado        |
      | Adoptado      |
      | No disponible |

  Esquema del escenario: La asignación se cierra automáticamente cuando el animal sale del refugio
    Dado que "Marta Ríos" es la voluntaria responsable de "Toby" desde "2026-09-01"
    Cuando "Toby" pasa a estado "<nuevo estado>" el "2026-10-20"
    Entonces "Toby" queda sin voluntario responsable
    Y el historial de responsables de "Toby" muestra a "Marta Ríos" del "2026-09-01" al "2026-10-20"

    Ejemplos:
      | nuevo estado  |
      | Adoptado      |
      | No disponible |

  Escenario: Solo se pueden asignar usuarios con rol voluntario
    Cuando abre la asignación de voluntario responsable
    Entonces la lista de responsables muestra únicamente usuarios con rol "Voluntario"

  Escenario: Asignación rechazada sin animales seleccionados
    Cuando intenta asignar a "Marta Ríos" sin seleccionar ningún animal
    Entonces el sistema no registra la asignación
    Y indica que debe seleccionar al menos un animal

  Escenario: Asignación rechazada por fecha futura
    Cuando intenta asignar un voluntario con fecha posterior a hoy
    Entonces el sistema no registra la asignación
    Y muestra el mensaje "La fecha de asignación no puede ser futura"
```

**Supuestos / preguntas abiertas**
- Confirmado: solo se asigna voluntario a animales *En valoración*, *Disponible* o *En proceso de adopción*, y se guarda el historial de asignaciones con su fecha.
- Si en una asignación múltiple alguno de los animales está fuera del refugio, se asumió que se rechaza toda la operación indicando cuál animal no cumple.
- Confirmado: cuando el animal pasa a *Adoptado* (HU-17) o a *No disponible* (HU-04), la asignación vigente se cierra automáticamente con fecha de fin igual a la del cambio. Es el mismo comportamiento que la liberación del espacio de alojamiento.
