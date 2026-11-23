# Runbook — Clase demostrativa de IntelliJ (Arquitectura de Software)

Nov 22, 2026 · @JoeyOut · versión 2 (decisiones tomadas)

## Decisiones tomadas

| Decisión | Valor |
| --- | --- |
| Lenguaje y framework | Java 25 (igual que el proyecto del equipo) + Spring Boot |
| Build | Maven (`mvn test`) |
| Duración | 60 minutos |
| Dominio del ejercicio | Préstamo de libros en la biblioteca universitaria |
| Paquete raíz (ejemplo) | `ec.edu.uce.biblioteca` |
| Repositorio de la demo | Lo prepara el docente; el runbook solo lo describe |

**Por qué este dominio:** el grupo ya vio DDD y Confluence, así que el ejemplo tiene que poder recorrer las páginas 5 a 10 de la guía sin forzarlas. La biblioteca da tres contextos claros (Préstamos, Catálogo, Lectores), un core evidente (Préstamos), un agregado con invariantes fuertes (Ejemplar), eventos naturales (EjemplarPrestado, EjemplarDevuelto) y una regla que cruza agregados para la tarea. Además, todos los estudiantes de la UCE lo conocen, así que no hay que explicar el negocio.

## Decisiones aún pendientes

- [ ] **Tarea práctica:** se define después de la rúbrica (propuesta al final).
- [ ] **Confluence:** confirmar que las páginas 5 a 10 existen y que Negocio y Dominio están completas. Lo ideal es cargar en ellas el ejemplo de la biblioteca (glosario y reglas de este runbook) para que el bloque 5 lea datos reales.
- [ ] **Figma:** confirmar que existe una pantalla "Prestar ejemplar" (aunque sea un wireframe).
- [ ] **Accesos MCP:** confirmar el acceso a Atlassian y a Figma desde la computadora de la clase.
- [ ] **Licencia de IntelliJ:** cuenta de estudiante en trámite (ver Preparación previa).

## Objetivo

Al terminar la clase, los estudiantes habrán visto en vivo cómo se arranca un proyecto monolítico con DDD en IntelliJ, usando Claude Code alimentado con el Confluence y el Figma del proyecto.

**Resultado esperado:** el contexto `prestamos` del monolito, con el agregado `Ejemplar` y su regla RN-01 protegida por una prueba, generado en vivo con nombres tomados del lenguaje ubicuo del Confluence.

## Preparación previa (el día anterior)

Prueba todo en la misma computadora y en la misma red que usarás en clase.

- [ ] **Licencia de IntelliJ.** Pide el GitHub Student Developer Pack (education.github.com) con el correo `@uce.edu.ec` o con el certificado de matrícula, y luego vincúlalo en jetbrains.com/student con la opción GitHub. Si no llega a tiempo, usa la prueba gratuita o la edición gratuita de IntelliJ: alcanza para esta demo (Maven, JUnit, terminal y el plugin de Claude Code).
- [ ] Proyecto creado en start.spring.io (Java 25, Maven, dependencia Spring Web), abierto en IntelliJ y compilando.
- [ ] `mvn test` en verde desde la terminal integrada.
- [ ] Paquetes de los tres contextos ya creados (`prestamos`, `catalogo`, `lectores`) con sus capas vacías. En 60 minutos no conviene crearlos en vivo.
- [ ] Claude Code instalado y con la sesión iniciada; `claude` responde en la terminal integrada de IntelliJ.
- [ ] Plugin de Claude Code para JetBrains (opcional; si falla, basta la terminal integrada).
- [ ] Páginas 5 a 10 del Confluence con el ejemplo de la biblioteca cargado.
- [ ] Conectores MCP de Atlassian y Figma configurados y probados con una consulta real.
- [ ] `CLAUDE.md` en la raíz del repo (ver bloque 4).
- [ ] Rama limpia `demo/clase-intellij` y rama `demo/respaldo` con el resultado final de los bloques 4 y 5.
- [ ] Capturas del antes y después del bloque 5, por si falla internet.

## Agenda (60 min)

| Min | Bloque | Qué se muestra | Qué queda al final |
| --- | --- | --- | --- |
| 0–5 | Apertura | Objetivo y mapa de la clase | Repo abierto en IntelliJ |
| 5–12 | 1. Ejercicio | Reglas del préstamo en lenguaje del negocio | RN-01 y RN-02 escritas |
| 12–18 | 2. Estructura monolítica | Árbol de paquetes ya creado | Todos ubican dónde irá cada clase |
| 18–27 | 3. DDD | Del Confluence al diseño (sin escribir código) | Agregado `Ejemplar` decidido |
| 27–42 | 4. Claude Code | Prompt, plan, diff, prueba | Agregado y prueba generados y revisados |
| 42–54 | 5. Confluence y Figma | El mismo prompt con contexto vía MCP | Comparación antes y después |
| 54–60 | Cierre | Criterios de éxito, mensajes y tarea | Tarea entregada |

**Cambio respecto a la versión de 90 min:** en el bloque 3 ya no se escribe código a mano. Se diseña en voz alta y el código lo genera Claude en el bloque 4. Así el agregado se construye una sola vez.

## Bloque 1 — Ejercicio de ejemplo (5–12 min)

**Planteamiento (lo que lees en voz alta)**

La biblioteca de la facultad presta libros a los lectores (estudiantes y docentes). De cada obra hay uno o más ejemplares físicos. Un lector pide prestado un ejemplar y debe devolverlo antes de una fecha. Mientras está prestado, nadie más puede llevárselo.

**Pasos**

1. Escribe las reglas como frases verificables:
   - **RN-01:** Un ejemplar prestado no puede volver a prestarse hasta que se devuelva.
   - **RN-02:** La devolución de un ejemplar lo deja disponible.
2. Subraya los sustantivos: Lector, Obra, Ejemplar, Préstamo, Devolución. Ese es el borrador del lenguaje ubicuo.
3. Pregunta: "¿Obra y Ejemplar son lo mismo?". No lo son: se presta el ejemplar, no la obra. Esta distinción es justo lo que debe recoger el glosario del Confluence.
4. Deja escrita la prueba esperada en palabras: "si presto un ejemplar ya prestado, la operación falla".

**Qué debe quedar claro:** la regla se escribe antes del código y sin mezclarla con funcionalidades como "el botón Prestar" (página 6 del Confluence, Reglas e invariantes).

## Bloque 2 — Estructura monolítica (12–18 min)

Muestra la estructura ya creada en la vista Project:

```text
src/main/java/ec/edu/uce/biblioteca/
├── prestamos/              ← contexto core
│   ├── domain/             ← Ejemplar, Prestamo, eventos (sin Spring)
│   ├── application/        ← caso de uso PrestarEjemplar
│   └── infrastructure/     ← controlador REST, repositorio JPA
├── catalogo/               ← contexto de soporte: Obra, autores, búsqueda
│   ├── domain/
│   ├── application/
│   └── infrastructure/
├── lectores/               ← contexto de soporte: Lector, carnet
│   └── ...
└── shared/                 ← solo tipos base y eventos comunes
```

**En IntelliJ**

1. Expande los paquetes para que se vea la forma.
2. Doble Shift: salta entre capas para mostrar que se navega rápido.
3. Explica que **Analyze → Dependencies** sirve para comprobar que `domain` no importa nada de `infrastructure` (se muestra al final del bloque 4, cuando ya hay código).

**Reglas en voz alta**

- Préstamos no usa la clase `Obra` de Catálogo; guarda solo su identificador (`ObraId`).
- La dependencia va hacia adentro: infraestructura → aplicación → dominio.
- Un contexto podría convertirse en servicio más adelante, pero hoy es un paquete.

## Bloque 3 — DDD: del Confluence al diseño (18–27 min)

Recorre las páginas del Confluence y decide el diseño en la pizarra, sin escribir código.

| Paso | Concepto | Página | Decisión para la biblioteca |
| --- | --- | --- | --- |
| 1 | Subdominios | 7 | Préstamos = core; Catálogo y Lectores = soporte |
| 2 | Contextos y mapa | 8 | Tres paquetes raíz; Préstamos consume `ObraId` y `LectorId` |
| 3 | Lenguaje ubicuo | 5 | Lector, Obra, Ejemplar, Préstamo, Devolución |
| 4 | Reglas e invariantes | 6 | RN-01 y RN-02 |
| 5 | Modelo táctico | 9 | Agregado `Ejemplar`; objetos de valor `EjemplarId`, `LectorId`, `PeriodoPrestamo` |
| 6 | Eventos de dominio | 10 | `EjemplarPrestado`, `EjemplarDevuelto` |

**Pregunta clave al grupo:** "¿Quién protege RN-01?". La respuesta es el agregado `Ejemplar`, con `prestar(lectorId, periodo)` lanzando `EjemplarNoDisponible` si ya está prestado y con `devolver()` cubriendo RN-02.

**Error típico a nombrar:** validar la disponibilidad en el controlador o en el servicio. Si la regla vive fuera del agregado, otro caso de uso se la puede saltar.

## Bloque 4 — Claude Code en IntelliJ (27–42 min)

Ciclo: dar contexto, pedir un plan, aprobarlo, revisar el diff y ejecutar la prueba. Claude propone y el estudiante decide.

**Preparar la sesión (ya hecho antes de clase; en clase solo se muestra)**

1. Terminal integrada (Alt+F12), en la raíz del repo, ejecutar `claude` (o el plugin de JetBrains, para ver el diff en el visor del IDE).
2. Enseñar el `CLAUDE.md`.
3. Activar el modo plan (Shift+Tab hasta que aparezca "plan mode").

**Contenido de `CLAUDE.md`**

- Resumen en tres líneas del sistema de préstamos de la biblioteca y enlace al Confluence.
- Estructura de paquetes del bloque 2 y la regla de dependencia hacia adentro.
- Convención: los nombres salen del lenguaje ubicuo y las reglas viven en el agregado.
- Java 25, Spring Boot, Maven. Pruebas: `mvn test`. Las pruebas de `domain` son JUnit 5 sin Spring.

**Prompt de la demo (sin contexto externo)**

```text
En el contexto prestamos, crea el agregado Ejemplar en prestamos/domain
y el caso de uso PrestarEjemplar en prestamos/application.
Regla RN-01: un ejemplar prestado no puede volver a prestarse hasta su devolución.
No uses Spring en domain. Escribe primero la prueba unitaria de RN-01 con JUnit 5.
Muéstrame el plan antes de editar archivos.
```

**Qué se comenta**

- El plan: ¿respeta las capas? ¿los nombres coinciden con el glosario?
- El diff: se revisa archivo por archivo antes de aceptar.
- La prueba: botón verde de IntelliJ; primero se ve fallar y luego pasar.
- Analyze → Dependencies: `domain` no depende de nada.

**Cierre del bloque:** sin contexto, Claude suele inventar nombres razonables pero genéricos (`Book`, `Loan`, `isAvailable`). El bloque 5 los corrige.

## Bloque 5 — Confluence y Figma como contexto (42–54 min)

Mismo pedido, ahora con Claude leyendo las fuentes reales. La diferencia entre las dos salidas es la demostración.

**Conexión (preparada antes de clase)**

1. `claude mcp add` para Atlassian y para Figma; copia las URL y el método de autenticación de la documentación oficial de cada proveedor el día de la preparación.
2. Ejecutar `/mcp` e iniciar sesión en el navegador.
3. Prueba corta: "lista las páginas hijas del espacio del proyecto en Confluence".

**Qué lee Claude**

| Fuente | Elemento | Para qué sirve |
| --- | --- | --- |
| Confluence | 5 · Lenguaje ubicuo | Nombres exactos: `Ejemplar`, `Lector`, `prestar`, `devolver` |
| Confluence | 6 · Reglas e invariantes | RN-01 y RN-02, cada una con su prueba |
| Confluence | 8 · Contextos y mapa | Paquete de cada clase; uso de `ObraId` en vez de `Obra` |
| Confluence | 10 · Eventos de dominio | `EjemplarPrestado`, `EjemplarDevuelto` |
| Figma | Pantalla "Prestar ejemplar" | Campos del formulario → entrada de `PrestarEjemplar` |

**Prompt enriquecido**

```text
Lee en Confluence las páginas Lenguaje ubicuo, Reglas e invariantes y Eventos de dominio.
Lee en Figma la pantalla Prestar ejemplar.
Con eso, completa el agregado Ejemplar y el caso de uso PrestarEjemplar del contexto prestamos,
usando exactamente los términos del glosario, cubriendo RN-01 y RN-02 con pruebas
y emitiendo los eventos de dominio. Muéstrame el plan antes de editar.
```

**Qué se compara:** nombres genéricos frente a los del glosario, reglas cubiertas por pruebas, eventos que antes faltaban y campos de entrada que ahora coinciden con el diseño.

**Si se acaba el tiempo:** no ejecutes el prompt en vivo. Cambia a la rama `demo/respaldo` y muestra el diff entre las dos versiones.

**Si el MCP no conecta:** usa las páginas exportadas a Markdown en `docs/` (enlazadas desde `CLAUDE.md`) y capturas de Figma.

## Plan B y problemas frecuentes

| Si pasa esto | Haz esto |
| --- | --- |
| No hay internet o Claude no responde | Rama `demo/respaldo`, recorrer el diff comentando cada decisión |
| El MCP no autentica | Páginas en `docs/` y capturas de Figma |
| Claude pone la regla en el controlador | Pedirle que la mueva al agregado y explicar por qué |
| La prueba no compila | Ejecutarla desde IntelliJ para ver el error completo y corregir en vivo |
| La licencia de IntelliJ no está lista | Edición gratuita o prueba de IntelliJ; nada de la demo depende de Ultimate |
| Preguntas fuera de tema | Anotarlas y responder al cierre |

## Cierre y criterios de éxito (54–60 min)

- [ ] El contexto `prestamos` tiene `domain`, `application` e `infrastructure`.
- [ ] RN-01 vive en el agregado `Ejemplar` y su prueba pasa.
- [ ] Los nombres del código coinciden con el glosario del Confluence.
- [ ] Se vio la diferencia entre el resultado sin contexto y con contexto.
- [ ] Cada estudiante sabe qué revisó el humano y qué generó Claude.

**Mensajes finales**

1. El Confluence es la fuente; el código se deriva de él.
2. Claude Code propone; el estudiante revisa y decide.
3. Una regla de negocio se protege en el agregado, con una prueba.

**Tarea práctica (propuesta; ajustar a la rúbrica):** agregar **RN-03, "Un lector no puede tener más de tres préstamos activos"**. Primero se documenta en el Confluence del equipo y luego se implementa con Claude Code. La pregunta de diseño que deben responder por escrito: ¿qué agregado protege RN-03, si no puede ser `Ejemplar` porque la regla abarca varios ejemplares?
