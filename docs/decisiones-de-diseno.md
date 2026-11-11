# Decisiones de diseño

Decisiones tomadas durante el modelado, con su justificación y la alternativa que se descartó. Están escritas para poder sostenerse ante una pregunta directa.

## El rol es un atributo de Usuario, no una jerarquía de herencia

**Alternativa descartada:** `Usuario` como clase padre con `Coordinador`, `Veterinario` y `Voluntario` como subclases.

**Por qué se descartó.** Los tres roles no tienen atributos propios; solo se diferencian en permisos. Además, el cambio de estado de un animal (HU-04) lo pueden hacer tanto el coordinador como el veterinario, así que esa asociación tendría que colgar del padre de todos modos y el modelo quedaría mixto, con unas asociaciones en la clase padre y otras en las hijas. Por último, la herencia fija el tipo del objeto: si un usuario cambiara de rol habría que recrearlo, mientras que con el atributo basta con modificarlo.

**Lo que sí se modeló como herencia** es `Persona` con `Usuario` y `Adoptante`, porque ahí la generalización responde a atributos compartidos reales (identificación, nombre, correo y teléfono) y no a una clasificación por permisos.

## El estado del animal es una clase con historial, no un atributo

**Alternativa descartada:** un atributo `estado` en `Animal`.

**Por qué se descartó.** HU-04 exige registrar la fecha del cambio y el usuario responsable. Un dato que se repite en el tiempo y tiene atributos propios deja de ser un atributo y pasa a ser una clase. Con un simple campo en `Animal` no se podría saber quién cambió el estado ni cuándo.

**Consecuencia.** `estadoActual()` es una operación calculada sobre el último `RegistroEstado`, no un atributo almacenado. Para filtrar animales disponibles (HU-07) hay que consultar el último registro de cada uno; es una decisión consciente, no un descuido.

## *En tratamiento* no es un estado del animal

**Alternativa descartada:** incluir *En tratamiento* entre los valores de `EstadoAnimal`.

**Por qué se descartó.** Esa información ya se deduce de la existencia de un `Tratamiento` sin fecha de finalización. Tenerla también como estado crea dos fuentes de verdad que pueden contradecirse. Se expone como la operación `tieneTratamientoActivo()`.

## Evaluación y devolución son atributos, no clases

**Alternativa descartada:** `Evaluacion` como clase asociada a `SolicitudAdopcion` y `Devolucion` como clase asociada a `Adopcion`.

**Por qué se descartó.** Ambas tienen multiplicidad 0..1 y tres atributos o menos. Una clase separada para cada una no aportaba comportamiento propio y sí cuatro cajas más al diagrama. Se absorbieron como atributos en la clase a la que pertenecen, junto con las operaciones `evaluar()` y `registrarDevolucion()`.

## Patrón tipo/instancia entre TipoVacuna y Vacunacion

`TipoVacuna` es el catálogo que se administra una sola vez (HU-09); `Vacunacion` es el hecho concreto de aplicar una de ellas a un animal en una fecha (HU-10). Es el mismo patrón que separa un libro de sus ejemplares. Sin esa separación, el tipo de vacuna sería texto libre y no habría catálogo que administrar.

## Interfaz IDesactivable

`EspacioAlojamiento` y `TipoVacuna` no tienen relación entre sí, pero cumplen el mismo contrato: se desactivan en lugar de eliminarse y pueden reactivarse. Dos clases sin relación que comparten contrato es el caso que justifica una interfaz. La regla existe para no perder la información histórica que las referencia: un espacio desactivado sigue apareciendo en ubicaciones pasadas y un tipo de vacuna desactivado, en vacunaciones ya aplicadas.

## Consulta Integral de Animales como componente propio

**Alternativa descartada:** dejar la ficha integral (HU-08) dentro de Gestión de Animales.

**Por qué se descartó.** La ficha consolida información de cuatro componentes. Si viviera en Gestión de Animales, ese componente dependería de Atención Médica, Gestión de Adopciones y Seguimiento Posadopción, mientras que los tres ya dependen de él para consultar el estado del animal: tres dependencias circulares. Un componente que solo consume y nunca es consumido las elimina sin añadir flechas.

## El adoptante no es actor del sistema

No opera el software: sus datos los registra el coordinador (HU-12) y sus solicitudes también (HU-13). Por eso no aparece como entidad externa en la vista de contexto ni como rol en la matriz de permisos. Aparece en la vista lógica como clase, porque sus datos sí se almacenan.

## El refugio no revierte una solicitud aprobada

Una solicitud aprobada solo puede cancelarla el adoptante (HU-15). Revertirla por decisión del refugio queda fuera de alcance, porque implicaría reevaluar decisiones ya comunicadas. Cuando una solicitud aprobada se cancela, el animal vuelve a *Disponible*, pero las solicitudes que se rechazaron automáticamente al aprobarla siguen rechazadas: quien siga interesado registra una nueva.

## Alcance excluido deliberadamente

Integraciones con sistemas externos, notificaciones por correo, gestión de donaciones y pagos, recomendación automática entre adoptante y animal, historial de ubicaciones y de asignaciones de voluntario, y reportes estadísticos. Los reportes se descartaron porque un reporte es información derivada y no aporta conceptos al modelo de dominio.
