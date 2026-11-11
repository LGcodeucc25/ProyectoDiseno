### HU-06: Administrar espacios de alojamiento y consultar su ocupación

> Origen: RF007 (ampliado con la administración de espacios)

**Como** coordinador
**Quiero** crear los espacios de alojamiento del refugio con su capacidad, modificarla cuando cambie, desactivarlos y reactivarlos, y consultar su ocupación actual
**Para** saber dónde hay cupo y planificar la ubicación de los animales

> La consulta de espacios también está disponible para veterinarios y voluntarios (ver matriz en HU-02).

**Validación INVEST**
- I — Independent: ✅ No depende de otra historia funcional.
- N — Negotiable: ✅ No impone cómo se representa el espacio ni el formato de la consulta.
- V — Valuable: ✅ Sin espacios definidos no se puede alojar a los animales ni controlar la capacidad.
- E — Estimable: ✅ Datos, validaciones y cálculo de ocupación definidos.
- S — Small: ✅ Administración y consulta de una sola entidad.
- T — Testable: ✅ Alta, edición, capacidad y ocupación mostradas son verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Administración y consulta de espacios de alojamiento

  Escenario: Creación de un espacio de alojamiento
    Dado que el coordinador ha iniciado sesión
    Cuando crea el espacio "Caniles C" con capacidad 6
    Entonces el espacio "Caniles C" aparece en la consulta con ocupación 0 de 6
    Y puede seleccionarse al asignar alojamiento a un animal

  Escenario: Creación rechazada por nombre duplicado
    Dado que el coordinador ha iniciado sesión
    Y ya existe el espacio "Caniles A"
    Cuando intenta crear otro espacio con nombre "Caniles A"
    Entonces el sistema no crea el espacio
    Y muestra el mensaje "Ya existe un espacio con ese nombre"

  Esquema del escenario: Creación rechazada por capacidad inválida
    Dado que el coordinador ha iniciado sesión
    Cuando intenta crear un espacio con capacidad "<capacidad>"
    Entonces el sistema no crea el espacio
    Y muestra el mensaje "La capacidad debe ser un número entero mayor que cero"

    Ejemplos:
      | capacidad |
      | 0         |
      | -2        |
      |           |

  Escenario: Modificación de la capacidad de un espacio
    Dado que el coordinador ha iniciado sesión
    Y el espacio "Caniles A" tiene capacidad 5 y ocupación 3
    Cuando cambia la capacidad de "Caniles A" a 8
    Entonces "Caniles A" aparece con ocupación 3 de 8

  Escenario: Capacidad no puede quedar por debajo de la ocupación actual
    Dado que el coordinador ha iniciado sesión
    Y el espacio "Caniles A" tiene capacidad 5 y ocupación 4
    Cuando intenta cambiar la capacidad de "Caniles A" a 3
    Entonces el sistema no guarda el cambio
    Y muestra el mensaje "La capacidad no puede ser menor que la ocupación actual"

  Escenario: Desactivación de un espacio sin animales
    Dado que el coordinador ha iniciado sesión
    Y el espacio "Caniles C" tiene ocupación 0
    Y "Toby" estuvo alojado en "Caniles C" en el pasado
    Cuando desactiva el espacio "Caniles C"
    Entonces "Caniles C" queda en estado "Inactivo"
    Y "Caniles C" no aparece entre las opciones al asignar alojamiento
    Y el historial de ubicaciones de "Toby" sigue mostrando su estancia en "Caniles C"

  Escenario: Desactivación rechazada para un espacio ocupado
    Dado que el coordinador ha iniciado sesión
    Y el espacio "Caniles A" tiene ocupación 2
    Cuando intenta desactivar el espacio "Caniles A"
    Entonces el sistema no desactiva el espacio
    Y muestra el mensaje "Solo se puede desactivar un espacio sin animales alojados"

  Escenario: Reactivación de un espacio
    Dado que el coordinador ha iniciado sesión
    Y el espacio "Caniles C" está en estado "Inactivo" con capacidad 6
    Y "Toby" estuvo alojado en "Caniles C" en el pasado
    Cuando reactiva el espacio "Caniles C"
    Entonces "Caniles C" queda en estado "Activo" con ocupación 0 de 6
    Y "Caniles C" vuelve a aparecer entre las opciones al asignar alojamiento
    Y el historial de ubicaciones de "Toby" sigue mostrando su estancia en "Caniles C"

  Escenario: Los espacios no se pueden eliminar
    Dado que el coordinador ha iniciado sesión
    Cuando consulta las acciones disponibles sobre el espacio "Caniles C"
    Entonces el sistema ofrece editar y desactivar o reactivar
    Y no ofrece eliminar

  Escenario: Listado de espacios con capacidad y ocupación
    Dado que un usuario del refugio ha iniciado sesión
    Y existen los espacios "Caniles A" con capacidad 5 y 3 animales alojados, y "Gatera 1" con capacidad 4 y 4 animales alojados
    Cuando consulta los espacios de alojamiento
    Entonces el sistema muestra "Caniles A" con capacidad 5 y ocupación 3
    Y muestra "Gatera 1" con capacidad 4 y ocupación 4, indicado como "Sin cupo"

  Escenario: La ocupación refleja cambios recientes
    Dado que un usuario del refugio ha iniciado sesión
    Y el espacio "Caniles A" tiene ocupación 3 de 5
    Y el animal "Toby" alojado en "Caniles A" fue registrado como adoptado
    Cuando consulta los espacios de alojamiento
    Entonces "Caniles A" aparece con ocupación 2 de 5
```

**Supuestos / preguntas abiertas**
- La ocupación es la cantidad de animales alojados en este momento en el espacio.
- Se asumió que el espacio tiene nombre único y capacidad, y que se puede renombrar.
- Confirmado: los espacios no se eliminan, para no perder el historial de ubicaciones. Se desactivan solo si su ocupación actual es cero (por ejemplo, por mantenimiento o reparación) y se pueden reactivar cuando vuelven al servicio. Es la misma regla que para los tipos de vacuna (HU-09).
- Se asumió que la consulta muestra por defecto solo los espacios activos.
- No se especificó si se pueden ver los animales de cada espacio desde la consulta. Se dejó fuera de alcance.
