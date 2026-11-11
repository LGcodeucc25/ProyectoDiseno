### HU-10: Registrar la aplicación de una vacuna

> Origen: RF011

**Como** veterinario
**Quiero** registrar la aplicación de una vacuna a un animal indicando el tipo de vacuna y la fecha de aplicación
**Para** mantener al día su historial de vacunación y garantizar que se entregue en adopción con sus vacunas registradas

**Validación INVEST**
- I — Independent: ✅ Requiere animales y tipos de vacuna existentes, preparables como datos de prueba.
- N — Negotiable: ✅ No impone cómo se registra.
- V — Valuable: ✅ Es información sanitaria clave para el cuidado y la adopción.
- E — Estimable: ✅ Datos y responsable definidos.
- S — Small: ✅ Un registro con validaciones simples.
- T — Testable: ✅ Registro, responsable y rechazos son verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Registro de vacunación

  Antecedentes:
    Dado que el veterinario "Luis Pérez" ha iniciado sesión
    Y el catálogo contiene el tipo de vacuna "Rabia" en estado "Activo"

  Escenario: Registro exitoso de una vacuna aplicada
    Cuando registra la aplicación de "Rabia" al animal "Toby" con fecha "hoy"
    Entonces el historial médico de "Toby" muestra la vacuna "Rabia" aplicada "hoy"
    Y el veterinario responsable registrado es "Luis Pérez"

  Escenario: El veterinario responsable no se puede elegir
    Cuando abre el registro de aplicación de vacuna
    Entonces el veterinario responsable aparece como "Luis Pérez" y no es editable

  Escenario: Tipo de vacuna inactivo no disponible
    Dado que el tipo de vacuna "Moquillo" está en estado "Inactivo"
    Cuando abre el registro de aplicación de vacuna
    Entonces "Moquillo" no aparece entre los tipos seleccionables

  Escenario: Registro rechazado por fecha futura
    Cuando intenta registrar la aplicación de "Rabia" con fecha posterior a hoy
    Entonces el sistema no registra la aplicación
    Y muestra el mensaje "La fecha de aplicación no puede ser futura"

  Escenario: Registro rechazado sin tipo de vacuna
    Cuando intenta registrar una aplicación sin seleccionar el tipo de vacuna
    Entonces el sistema no registra la aplicación
    Y indica que el tipo de vacuna es obligatorio

  Escenario: Registro rechazado para un animal No disponible
    Dado que el animal "Rex" está en estado "No disponible"
    Cuando intenta registrar la aplicación de "Rabia" a "Rex"
    Entonces el sistema no registra la aplicación
    Y muestra el mensaje "No se pueden registrar vacunas a un animal no disponible"

  Esquema del escenario: Registro permitido en los demás estados
    Dado que el animal "Toby" está en estado "<estado>"
    Cuando registra la aplicación de "Rabia" a "Toby"
    Entonces el historial médico de "Toby" muestra la vacuna "Rabia"

    Ejemplos:
      | estado                 |
      | En valoración          |
      | Disponible             |
      | En proceso de adopción |
      | Adoptado               |
```

**Supuestos / preguntas abiertas**
- Se asumió que la fecha de aplicación no puede ser futura.
- Confirmado: se pueden registrar vacunas en cualquier estado salvo *No disponible*.
