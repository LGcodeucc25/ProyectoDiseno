### HU-08: Consultar la ficha integral de un animal

> Origen: RF009

**Como** usuario del refugio (coordinador, veterinario o voluntario)
**Quiero** consultar en un solo lugar el estado actual, el alojamiento, el historial médico, las solicitudes y los seguimientos de un animal
**Para** tomar decisiones sobre su cuidado y adopción con toda su información a la vista

**Validación INVEST**
- I — Independent: ✅ Puede entregarse mostrando las secciones vacías; cada sección se llena a medida que existan los registros de otras historias.
- N — Negotiable: ✅ Define qué información aparece, no cómo se organiza visualmente.
- V — Valuable: ✅ Evita consultar varias pantallas para conocer la situación de un animal.
- E — Estimable: ✅ Las secciones están enumeradas en el requerimiento.
- S — Small: ✅ Una vista de consulta.
- T — Testable: ✅ Cada sección muestra datos verificables.

**Criterios de aceptación (Gherkin)**
```gherkin
# language: es
Característica: Ficha integral del animal

  Antecedentes:
    Dado que un usuario del refugio ha iniciado sesión

  Escenario: Ficha con toda la información del animal
    Dado que el animal "Toby" está en estado "En proceso de adopción"
    Y está alojado en "Caniles A" desde "2026-09-01"
    Y tiene registrada la vacuna "Rabia" aplicada el "2026-09-05"
    Y tiene un tratamiento con diagnóstico "Otitis" iniciado el "2026-09-10" y finalizado el "2026-09-20"
    Y tiene una solicitud de adopción "Aprobada" del adoptante "Carlos Ruiz"
    Cuando consulta la ficha integral de "Toby"
    Entonces ve sus datos de ingreso
    Y ve el estado actual "En proceso de adopción"
    Y ve el alojamiento actual "Caniles A"
    Y ve la vacuna "Rabia" con su fecha y veterinario responsable
    Y ve el tratamiento "Otitis" con sus fechas y veterinario responsable
    Y ve la solicitud de "Carlos Ruiz" con su fecha y estado "Aprobada"

  Escenario: Indicador de tratamiento activo
    Dado que el animal "Luna" tiene un tratamiento con diagnóstico "Dermatitis" sin fecha de finalización
    Cuando consulta la ficha integral de "Luna"
    Entonces la ficha indica que "Luna" tiene un tratamiento activo

  Escenario: Ficha sin tratamiento activo
    Dado que todos los tratamientos del animal "Toby" tienen fecha de finalización anterior a hoy
    Cuando consulta la ficha integral de "Toby"
    Entonces la ficha indica que "Toby" no tiene tratamientos activos

  Escenario: Seguimientos de un animal adoptado
    Dado que el animal "Rocky" está "Adoptado"
    Y tiene una visita de seguimiento realizada con calificación "Satisfactoria"
    Y tiene una visita de seguimiento programada para una fecha futura
    Cuando consulta la ficha integral de "Rocky"
    Entonces ve ambas visitas con su fecha, voluntario asignado y estado

  Escenario: Visita con alertas visible en la ficha
    Dado que el animal "Rocky" tiene una visita de seguimiento realizada con calificación "Con alertas" y observaciones "Bajo de peso"
    Cuando consulta la ficha integral de "Rocky"
    Entonces ve la visita con calificación "Con alertas" y las observaciones "Bajo de peso"

  Escenario: Voluntario responsable actual
    Dado que "Jorge León" es el voluntario responsable actual de "Toby"
    Y antes lo fue "Marta Ríos"
    Cuando consulta la ficha integral de "Toby"
    Entonces ve como voluntario responsable a "Jorge León"

  Escenario: Ficha de un animal recién ingresado
    Dado que el animal "Misu" acaba de ser registrado y no tiene otros registros
    Cuando consulta la ficha integral de "Misu"
    Entonces ve sus datos de ingreso y el estado "En valoración"
    Y las secciones de alojamiento, historial médico, solicitudes, seguimientos y voluntario responsable indican "Sin registros"
```

**Supuestos / preguntas abiertas**
- Un tratamiento está activo si no tiene fecha de finalización o si esa fecha es hoy o posterior. *En tratamiento* no es un estado, para no tener dos fuentes de verdad.
- Se asumió que la ficha muestra también el historial de cambios de estado y de ubicaciones (HU-04 y HU-05).
- Confirmado: la ficha muestra el voluntario responsable actual (HU-21), aunque RF009 no lo mencione.
