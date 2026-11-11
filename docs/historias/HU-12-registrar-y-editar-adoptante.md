### HU-12: Registrar y editar un adoptante

> Origen: RF013

**Como** coordinador
**Quiero** registrar a un adoptante con su identificación, nombre, teléfono, correo electrónico, dirección de residencia, tipo de vivienda y experiencia previa con animales, y editar esos datos cuando cambien
**Para** contar con información actualizada para evaluar sus solicitudes de adopción

**Validación INVEST**
- I — Independent: ✅ No depende de otra historia funcional.
- N — Negotiable: ✅ Define datos y listas, no la forma de captura.
- V — Valuable: ✅ Es la base para evaluar si un hogar es adecuado.
- E — Estimable: ✅ Campos, listas y unicidad definidos.
- S — Small: ✅ Alta y edición de una sola entidad con las mismas validaciones.
- T — Testable: ✅ Registro, edición, duplicados y validaciones verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Registro y edición de adoptantes

  Antecedentes:
    Dado que el coordinador ha iniciado sesión

  Escenario: Registro exitoso de un adoptante
    Cuando registra un adoptante con identificación "52123456", nombre "Carlos Ruiz", teléfono "3001234567", correo "carlos@correo.com", dirección "Calle 10 # 5-20", tipo de vivienda "Casa" y experiencia previa "Sí" con descripción "Tuvo dos perros"
    Entonces el sistema guarda el adoptante
    Y muestra el mensaje "Adoptante registrado correctamente"

  Esquema del escenario: Valores permitidos de tipo de vivienda
    Cuando registra un adoptante con tipo de vivienda "<tipo de vivienda>"
    Entonces el sistema guarda el adoptante con tipo de vivienda "<tipo de vivienda>"

    Ejemplos:
      | tipo de vivienda |
      | Casa             |
      | Apartamento      |
      | Finca            |
      | Otro             |

  Escenario: Experiencia previa sin descripción
    Cuando registra un adoptante con experiencia previa "No" y sin descripción
    Entonces el sistema guarda el adoptante con experiencia previa "No"

  Escenario: Registro rechazado por identificación duplicada
    Dado que ya existe un adoptante con identificación "52123456"
    Cuando intenta registrar otro adoptante con identificación "52123456"
    Entonces el sistema no registra el adoptante
    Y muestra el mensaje "Ya existe un adoptante con esa identificación"

  Esquema del escenario: Registro o edición rechazada por campo obligatorio vacío
    Cuando intenta guardar un adoptante dejando vacío el campo "<campo>"
    Entonces el sistema no guarda el adoptante
    Y indica que el campo "<campo>" es obligatorio

    Ejemplos:
      | campo              |
      | identificación     |
      | nombre             |
      | teléfono           |
      | dirección          |
      | tipo de vivienda   |
      | experiencia previa |

  Escenario: Registro rechazado por correo con formato inválido
    Cuando intenta registrar un adoptante con el correo "carlos.correo.com"
    Entonces el sistema no registra el adoptante
    Y muestra el mensaje "El correo electrónico no tiene un formato válido"

  Escenario: Edición de los datos de un adoptante
    Dado que existe el adoptante "Carlos Ruiz" con teléfono "3001234567" y tipo de vivienda "Apartamento"
    Cuando cambia su teléfono a "3109876543" y su tipo de vivienda a "Casa"
    Entonces el sistema guarda los nuevos datos
    Y el adoptante "Carlos Ruiz" aparece con teléfono "3109876543" y tipo de vivienda "Casa"

  Escenario: Edición rechazada por identificación duplicada
    Dado que existen los adoptantes "Carlos Ruiz" con identificación "52123456" y "Ana Torres" con identificación "41987654"
    Cuando intenta cambiar la identificación de "Ana Torres" a "52123456"
    Entonces el sistema no guarda los cambios
    Y muestra el mensaje "Ya existe un adoptante con esa identificación"
```

**Supuestos / preguntas abiertas**
- Se asumió que el correo es opcional, porque no todos los adoptantes tienen uno, y que si se ingresa debe tener un formato válido.
- Se asumió que editar al adoptante no afecta las evaluaciones ya registradas de sus solicitudes.
