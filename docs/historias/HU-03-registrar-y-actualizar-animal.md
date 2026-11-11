### HU-03: Registrar y actualizar la información de un animal

> Origen: RF003, RF004

**Como** coordinador
**Quiero** registrar el ingreso de un animal rescatado con su nombre, especie, raza, sexo, edad aproximada, origen del ingreso, fecha de ingreso y descripción, y actualizar después esos datos
**Para** tener constancia exacta de cada animal que llega al refugio y comenzar su proceso de valoración

**Validación INVEST**
- I — Independent: ✅ No depende de otra historia funcional.
- N — Negotiable: ✅ Define los datos y las listas de valores, no la forma de captura.
- V — Valuable: ✅ Es el punto de entrada de todo el ciclo de vida del animal y mantiene sus datos confiables.
- E — Estimable: ✅ Campos, listas cerradas, estado inicial y reglas de edición definidos.
- S — Small: ✅ Alta y edición de una sola entidad con las mismas validaciones.
- T — Testable: ✅ Estado inicial, listas, validaciones y cambios guardados son verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Registro y actualización de animales

  Antecedentes:
    Dado que el coordinador ha iniciado sesión

  Escenario: Registro exitoso de un animal rescatado
    Cuando registra un animal con nombre "Toby", especie "Perro", raza "Criollo", sexo "Macho", edad aproximada "2 años", origen "Rescate en vía pública", fecha de ingreso "hoy" y descripción "Encontrado con herida en la pata"
    Entonces el sistema guarda el animal
    Y el animal queda en estado "En valoración"
    Y muestra el mensaje "Animal registrado correctamente"

  Esquema del escenario: Valores permitidos de especie, sexo y origen
    Cuando registra un animal con especie "<especie>", sexo "<sexo>" y origen "<origen>"
    Entonces el sistema guarda el animal con especie "<especie>", sexo "<sexo>" y origen "<origen>"

    Ejemplos:
      | especie | sexo        | origen                  |
      | Perro   | Macho       | Rescate en vía pública  |
      | Gato    | Hembra      | Entrega voluntaria      |
      | Otro    | Desconocido | Remisión de autoridad   |
      | Perro   | Hembra      | Otro                    |

  Escenario: La raza se registra como texto libre
    Cuando registra un animal con raza "Mestizo de pastor alemán"
    Entonces el sistema guarda el animal con la raza "Mestizo de pastor alemán"

  Esquema del escenario: Registro rechazado por campo obligatorio vacío
    Cuando intenta registrar un animal dejando vacío el campo "<campo>"
    Entonces el sistema no registra el animal
    Y indica que el campo "<campo>" es obligatorio

    Ejemplos:
      | campo              |
      | nombre             |
      | especie            |
      | sexo               |
      | edad aproximada    |
      | origen del ingreso |
      | fecha de ingreso   |

  Escenario: Registro rechazado por fecha de ingreso futura
    Cuando intenta registrar un animal con fecha de ingreso posterior a hoy
    Entonces el sistema no registra el animal
    Y muestra el mensaje "La fecha de ingreso no puede ser futura"

  Escenario: Actualización exitosa de datos del animal
    Dado que existe el animal "Toby" con raza "Criollo", sexo "Desconocido" y edad aproximada "2 años"
    Cuando actualiza la raza de "Toby" a "Labrador mestizo", el sexo a "Macho" y la edad aproximada a "3 años"
    Entonces el sistema guarda los nuevos datos
    Y la ficha de "Toby" muestra la raza "Labrador mestizo", el sexo "Macho" y la edad aproximada "3 años"
    Y muestra el mensaje "Información actualizada correctamente"

  Escenario: Actualización rechazada por campo obligatorio vacío
    Dado que existe el animal "Toby"
    Cuando intenta dejar vacío el nombre de "Toby"
    Entonces el sistema no guarda los cambios
    Y indica que el campo "nombre" es obligatorio

  Escenario: El estado del animal no se modifica desde la actualización de datos
    Dado que existe el animal "Toby"
    Cuando abre la edición de datos de "Toby"
    Entonces el campo de estado no es editable
    Y el sistema indica que el estado se cambia mediante "Registrar cambio de estado"
```

**Supuestos / preguntas abiertas**
- Se asumió que la raza y la descripción son opcionales y que los demás campos son obligatorios.
- Se asumió que la edad aproximada se registra como número más unidad (meses o años).
- Son editables los campos del ingreso. El estado solo cambia por HU-04 o por los flujos de adopción, y la ubicación por HU-05.
- Se asumió que se pueden editar los datos de un animal en cualquier estado, incluido *No disponible*, para corregir errores, y que no se guarda historial de esas ediciones.
