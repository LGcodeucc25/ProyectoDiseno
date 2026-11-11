### HU-16: Consultar solicitudes de adopción

> Origen: RF017

**Como** coordinador
**Quiero** consultar las solicitudes de adopción filtrando por adoptante o por animal
**Para** conocer el historial de un adoptante antes de evaluarlo y comparar a los interesados en un animal

**Validación INVEST**
- I — Independent: ✅ Requiere datos de solicitudes, preparables como datos de prueba.
- N — Negotiable: ✅ No impone formato de presentación.
- V — Valuable: ✅ Permite detectar adoptantes con rechazos o cancelaciones previas y elegir el mejor hogar.
- E — Estimable: ✅ Consulta simple con dos filtros.
- S — Small: ✅ Un listado de una sola entidad.
- T — Testable: ✅ El listado se compara contra datos conocidos.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Consulta de solicitudes de adopción

  Antecedentes:
    Dado que el coordinador ha iniciado sesión
    Y existen las siguientes solicitudes:
      | adoptante   | animal | fecha      | estado    |
      | Carlos Ruiz | Toby   | 2026-08-01 | Cancelada |
      | Carlos Ruiz | Misu   | 2026-09-15 | Rechazada |
      | Carlos Ruiz | Luna   | 2026-10-20 | Pendiente |
      | Ana Torres  | Luna   | 2026-10-22 | Pendiente |

  Escenario: Consulta de solicitudes por adoptante
    Cuando consulta las solicitudes del adoptante "Carlos Ruiz"
    Entonces el sistema muestra las tres solicitudes de "Carlos Ruiz" con su animal, fecha y estado
    Y las solicitudes aparecen ordenadas de la más reciente a la más antigua

  Escenario: Consulta de solicitudes por animal
    Cuando consulta las solicitudes del animal "Luna"
    Entonces el sistema muestra las solicitudes de "Carlos Ruiz" y de "Ana Torres" con su fecha y estado

  Escenario: Detalle de evaluación o cancelación en el listado
    Dado que la solicitud de "Carlos Ruiz" sobre "Toby" fue cancelada con motivo "Cambio de ciudad"
    Cuando consulta las solicitudes del adoptante "Carlos Ruiz"
    Entonces la solicitud sobre "Toby" muestra el motivo "Cambio de ciudad"

  Escenario: Filtrar por estado dentro de la consulta
    Cuando consulta las solicitudes del adoptante "Carlos Ruiz" filtrando por estado "Pendiente"
    Entonces el sistema muestra únicamente la solicitud sobre "Luna"

  Esquema del escenario: Consulta sin resultados
    Dado que <sujeto> no tiene solicitudes registradas
    Cuando consulta sus solicitudes
    Entonces el sistema muestra el mensaje "<mensaje>"

    Ejemplos:
      | sujeto                 | mensaje                                       |
      | el adoptante "Pedro Díaz" | El adoptante no tiene solicitudes registradas |
      | el animal "Rocky"      | El animal no tiene solicitudes registradas    |
```

**Supuestos / preguntas abiertas**
- Se asumió que el orden es de la más reciente a la más antigua.
- La consulta es exclusiva del coordinador (matriz en HU-02).
- Se agregó el filtro adicional por estado para facilitar la evaluación, aunque el requerimiento no lo pide. Se puede quitar si no aporta.
