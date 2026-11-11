### HU-09: Administrar el catálogo de tipos de vacuna

> Origen: RF010

**Como** veterinario
**Quiero** crear, editar, desactivar y reactivar los tipos de vacuna del catálogo, con su nombre y descripción
**Para** registrar la vacunación de los animales de forma uniforme sin perder el historial existente

**Validación INVEST**
- I — Independent: ✅ No depende de otra historia funcional.
- N — Negotiable: ✅ No impone la forma de gestión del catálogo.
- V — Valuable: ✅ Estandariza los registros de vacunación, evita vacunas obsoletas y preserva el historial médico.
- E — Estimable: ✅ Dos campos, unicidad y cuatro operaciones claras.
- S — Small: ✅ CRUD de una sola entidad por un solo actor, sin eliminación.
- T — Testable: ✅ Cada operación tiene resultado verificable.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Administración del catálogo de tipos de vacuna

  Antecedentes:
    Dado que el veterinario ha iniciado sesión

  Escenario: Creación de un tipo de vacuna
    Cuando crea el tipo de vacuna con nombre "Rabia" y descripción "Vacuna antirrábica anual"
    Entonces el catálogo contiene el tipo "Rabia" en estado "Activo"
    Y el tipo "Rabia" puede seleccionarse al registrar una aplicación de vacuna

  Escenario: Creación o edición rechazada por nombre duplicado
    Dado que el catálogo ya contiene el tipo de vacuna "Rabia"
    Cuando intenta crear otro tipo de vacuna o renombrar uno existente como "rabia"
    Entonces el sistema no guarda el tipo de vacuna
    Y muestra el mensaje "Ya existe un tipo de vacuna con ese nombre"

  Escenario: Creación o edición rechazada por nombre vacío
    Cuando intenta guardar un tipo de vacuna sin nombre
    Entonces el sistema no guarda el tipo de vacuna
    Y indica que el campo "nombre" es obligatorio

  Escenario: Edición de un tipo de vacuna
    Dado que el catálogo contiene el tipo "Parvovirus"
    Y el animal "Toby" tiene registrada una aplicación de "Parvovirus"
    Cuando cambia el nombre de "Parvovirus" a "Parvovirus canino" y su descripción
    Entonces el catálogo muestra "Parvovirus canino" con la nueva descripción
    Y la ficha de "Toby" muestra la aplicación como "Parvovirus canino"

  Escenario: Desactivación de un tipo de vacuna
    Dado que el catálogo contiene el tipo "Moquillo" en estado "Activo"
    Y el animal "Toby" tiene registrada una aplicación de "Moquillo"
    Cuando desactiva el tipo de vacuna "Moquillo"
    Entonces "Moquillo" queda en estado "Inactivo"
    Y "Moquillo" no aparece entre las opciones al registrar una nueva aplicación de vacuna
    Y la ficha de "Toby" sigue mostrando la aplicación de "Moquillo"

  Escenario: Reactivación de un tipo de vacuna
    Dado que el catálogo contiene el tipo "Moquillo" en estado "Inactivo"
    Cuando reactiva el tipo de vacuna "Moquillo"
    Entonces "Moquillo" queda en estado "Activo"
    Y "Moquillo" vuelve a aparecer entre las opciones al registrar una aplicación de vacuna

  Escenario: Los tipos de vacuna no se pueden eliminar
    Dado que el catálogo contiene el tipo "Moquillo"
    Cuando consulta las acciones disponibles sobre "Moquillo"
    Entonces el sistema ofrece editar y desactivar o reactivar
    Y no ofrece eliminar
```

**Supuestos / preguntas abiertas**
- El nombre es único sin distinguir mayúsculas y la descripción es opcional.
- Las aplicaciones ya registradas muestran el nombre actualizado, porque apuntan al mismo tipo de vacuna.
- Ningún tipo se elimina, tenga o no aplicaciones. Solo se desactiva y se puede reactivar (confirmado).
