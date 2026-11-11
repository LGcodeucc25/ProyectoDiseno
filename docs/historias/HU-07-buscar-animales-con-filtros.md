### HU-07: Buscar animales con filtros

> Origen: RF008

**Como** usuario del refugio (coordinador, veterinario o voluntario)
**Quiero** buscar animales filtrando por especie, raza, sexo, edad y estado
**Para** encontrar rápidamente a los animales que necesito atender o que pueden ofrecerse en adopción

**Validación INVEST**
- I — Independent: ✅ Solo requiere animales existentes.
- N — Negotiable: ✅ No impone el diseño de la búsqueda.
- V — Valuable: ✅ Agiliza la atención y la búsqueda de candidatos para adoptantes.
- E — Estimable: ✅ Filtros y valores posibles definidos.
- S — Small: ✅ Una búsqueda con cinco filtros.
- T — Testable: ✅ Los resultados esperados son deterministas para un conjunto de datos dado.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Búsqueda de animales

  Antecedentes:
    Dado que un usuario del refugio ha iniciado sesión
    Y existen los siguientes animales:
      | nombre | especie | raza    | sexo   | edad aproximada | estado        |
      | Toby   | Perro   | Criollo | Macho  | 2 años          | Disponible    |
      | Luna   | Perro   | Beagle  | Hembra | 5 años          | En valoración |
      | Misu   | Gato    | Siamés  | Hembra | 1 año           | Disponible    |
      | Rocky  | Perro   | Criollo | Macho  | 8 años          | Adoptado      |

  Esquema del escenario: Búsqueda con un filtro
    Cuando busca animales con el filtro "<filtro>" igual a "<valor>"
    Entonces el sistema muestra únicamente a <resultado>

    Ejemplos:
      | filtro  | valor      | resultado            |
      | especie | Gato       | Misu                 |
      | raza    | Criollo    | Toby y Rocky         |
      | sexo    | Hembra     | Luna y Misu          |
      | estado  | Disponible | Toby y Misu          |

  Escenario: Búsqueda por rango de edad
    Cuando busca animales con edad entre 1 y 3 años
    Entonces el sistema muestra únicamente a Toby y Misu

  Escenario: Búsqueda combinando varios filtros
    Cuando busca animales con especie "Perro", sexo "Macho" y estado "Disponible"
    Entonces el sistema muestra únicamente a Toby

  Escenario: Búsqueda sin resultados
    Cuando busca animales con especie "Gato" y estado "Adoptado"
    Entonces el sistema no muestra animales
    Y muestra el mensaje "No se encontraron animales con los filtros seleccionados"

  Escenario: Búsqueda sin filtros
    Cuando busca animales sin aplicar filtros
    Entonces el sistema muestra los cuatro animales registrados
```

**Supuestos / preguntas abiertas**
- Los filtros se combinan con "Y": el animal debe cumplir todos.
- Se asumió que la edad se filtra por rango (mínimo y máximo).
- Se asumió que la raza se filtra por coincidencia parcial y sin distinguir mayúsculas, ya que es texto libre.
- Se asumió que el resultado muestra nombre, especie, raza, sexo, edad y estado, y permite abrir la ficha (HU-08).
