# Runbook — Clase demostrativa de IntelliJ (Arquitectura de Software)

Nov 23, 2026 · @JoeyOut · versión 3 (alineada con el repo y con el Confluence en inglés)

## Decisiones tomadas

| Decisión | Valor |
| --- | --- |
| Lenguaje y framework | Java 25 (igual que el proyecto del equipo) + Spring Boot 4 |
| Build | Gradle, Groovy DSL (`./gradlew test`) |
| Frontend | React + TypeScript con Vite (`npm run dev`) |
| Base de datos | H2 en memoria (no hay que instalar nada) |
| Duración | 60 minutos |
| Dominio del ejercicio | Préstamo de libros en la biblioteca universitaria |
| Paquete raíz | `ec.edu.uce.library` |
| Idioma del dominio | Inglés, igual que el Confluence: Copy, Loan, Member, BR-01… |
| Repositorio de la demo | `JosephP2001/arqPj`; la clase arranca en la rama `develop` |

**Por qué este dominio:** el grupo ya vio DDD y Confluence, así que el ejemplo tiene que poder recorrer las páginas 5 a 10 de la guía sin forzarlas. La biblioteca da tres contextos claros (Lending, Catalog, Members), un core evidente (Lending), un agregado con invariantes fuertes (`Copy`), eventos naturales (`CopyLent`, `CopyReturned`) y una regla que cruza agregados para la tarea (BR-03). Además, todos los estudiantes de la UCE lo conocen, así que no hay que explicar el negocio.

## Decisiones aún pendientes

- [ ] **Tarea práctica:** se define después de la rúbrica (propuesta al final).
- [ ] **Figma:** hoy la referencia es la página "Figma · Pantalla Lend copy" del Confluence. Falta el wireframe real y el conector MCP de Figma.
- [ ] **Accesos MCP:** confirmar el acceso a Atlassian desde la computadora de la clase.

## Objetivo

Al terminar la clase, los estudiantes habrán visto en vivo cómo se arranca un proyecto monolítico con DDD en IntelliJ, usando Claude Code alimentado con el Confluence del proyecto.

**Resultado esperado:** el contexto `lending` del monolito, con el agregado `Copy` y su regla BR-01 protegida por una prueba, generado en vivo con nombres tomados del lenguaje ubicuo del Confluence.

## El repositorio de la demo

| Rama / tag | Contenido | Uso en clase |
| --- | --- | --- |
| `develop` = tag `etapa-1-estructura` | Paquetes de los tres contextos con sus capas vacías, H2, proxy `/api` de Vite y `CLAUDE.md` | **Punto de partida**: aquí se programa en vivo |
| `demo/respaldo` | Solución completa, construida por etapas | Plan B si algo falla o se acaba el tiempo |
| tag `etapa-2-dominio` | `Copy`, `CopyId`, `MemberId`, `Loan`, `LoanPeriod`, `CopyNotAvailableException`, `CopyRepository`, `CopyLent`, `CopyReturned`, caso de uso `LendCopy`; pruebas de BR-01 y BR-02 sin Spring | Saltar al final del bloque 4 / 5 |
| tag `etapa-3-api` | Adaptador JPA, `POST /api/loans` (409 "The copy is not available" al violar BR-01) y copias de ejemplo | Mostrar la regla viajando hasta la API |
| tag `etapa-4-react` | Pantalla **Lend copy** llamando al endpoint | Mostrar el formulario del Figma funcionando |

**BR-03 no está implementada en ninguna rama:** es la tarea de los estudiantes.

**Moverse entre etapas** (antes, guarda o descarta lo hecho en vivo con `git stash`):

```bash
git switch develop                   # punto de partida
git switch --detach etapa-2-dominio  # saltar a una etapa
git switch demo/respaldo             # solución completa
```

**Arrancar la solución completa** (en `demo/respaldo`):

```bash
# Backend, desde backend/library (necesita JDK 25)
$env:JAVA_HOME="$HOME\.jdks\openjdk-25"   # PowerShell; en bash: export JAVA_HOME=...
./gradlew test
./gradlew bootRun                          # http://localhost:8080

# Frontend, desde frontend
npm install
npm run dev                                # http://localhost:5173 (proxy de /api al 8080)
```

**Ejemplares de ejemplo** (se cargan al arrancar, iguales a Figma): `EJ-00123` y `EJ-00124` están Disponibles; `EJ-00456` está prestado al lector `1700000004` hasta el 10/12/2026 (si esa fecha ya pasó, hasta hoy + 15 días).

**Provocar el 409 de BR-01:**

```bash
curl -i -X POST http://localhost:8080/api/loans -H "Content-Type: application/json" -d '{"copyCode":"EJ-00456","memberId":"1712345678","dueDate":"2026-12-08"}'
# HTTP/1.1 409   {"message":"The copy is not available","dueDate":"2026-12-10"}
```

## Preparación previa (el día anterior)

Prueba todo en la misma computadora y en la misma red que usarás en clase.

- [ ] **Licencia de IntelliJ.** Pide el GitHub Student Developer Pack (education.github.com) con el correo `@uce.edu.ec` o con el certificado de matrícula, y luego vincúlalo en jetbrains.com/student con la opción GitHub. Si no llega a tiempo, usa la prueba gratuita o la edición gratuita de IntelliJ: alcanza para esta demo (Gradle, JUnit, terminal y el plugin de Claude Code).
- [ ] Repo clonado, en la rama `develop`, abierto en IntelliJ con el JDK 25 y el proyecto Gradle importado (`backend/library`).
- [ ] `./gradlew test` en verde desde la terminal integrada (con `JAVA_HOME` apuntando al JDK 25).
- [ ] `npm install` y `npm run build` en verde en `frontend`.
- [ ] Ensayo del plan B: `git switch demo/respaldo`, arrancar backend y frontend, prestar `EJ-00123` desde la pantalla y provocar el error con `EJ-00456`. Volver a `develop`.
- [ ] Claude Code instalado y con la sesión iniciada; `claude` responde en la terminal integrada de IntelliJ.
- [ ] Plugin de Claude Code para JetBrains (opcional; si falla, basta la terminal integrada).
- [ ] Páginas 5 a 10 y "Figma · Pantalla Lend copy" en el espacio BIB del Confluence.
- [ ] Conector MCP de Atlassian configurado y probado con una consulta real.
- [ ] Puertos 8080 y 5173 libres.
- [ ] Capturas del antes y después del bloque 5, por si falla internet.

## Agenda (60 min)

| Min | Bloque | Qué se muestra | Qué queda al final |
| --- | --- | --- | --- |
| 0–5 | Apertura | Objetivo y mapa de la clase | Repo abierto en IntelliJ, rama `develop` |
| 5–12 | 1. Ejercicio | Reglas del préstamo en lenguaje del negocio | BR-01 y BR-02 escritas |
| 12–18 | 2. Estructura monolítica | Árbol de paquetes ya creado | Todos ubican dónde irá cada clase |
| 18–27 | 3. DDD | Del Confluence al diseño (sin escribir código) | Agregado `Copy` decidido |
| 27–42 | 4. Claude Code | Prompt, plan, diff, prueba | Agregado y prueba generados y revisados |
| 42–54 | 5. Confluence como contexto | El mismo pedido con contexto vía MCP | Comparación antes y después |
| 54–60 | Cierre | Criterios de éxito, mensajes y tarea | Tarea entregada |

En el bloque 3 no se escribe código a mano. Se diseña en voz alta y el código lo genera Claude en el bloque 4. Así el agregado se construye una sola vez.

## Bloque 1 — Ejercicio de ejemplo (5–12 min)

**Planteamiento (lo que lees en voz alta)**

La biblioteca de la facultad presta libros a sus miembros (estudiantes y docentes). De cada libro hay una o más copias físicas. Un miembro se lleva una copia y debe devolverla antes de una fecha. Mientras está prestada, nadie más puede llevársela.

**Pasos**

1. Escribe las reglas como frases verificables:
   - **BR-01:** Una Copy prestada no puede volver a prestarse hasta que se devuelva.
   - **BR-02:** La devolución de una Copy la deja Available.
2. Subraya los sustantivos: Member, Book, Copy, Loan. Ese es el borrador del lenguaje ubicuo.
3. Pregunta: "¿Book y Copy son lo mismo?". No lo son: se presta la Copy, no el Book. Esta distinción es justo lo que recoge el glosario del Confluence ("Book" para la copia física es un término prohibido).
4. Deja escrita la prueba esperada en palabras: "si presto una Copy ya prestada, la operación falla".

**Qué debe quedar claro:** la regla se escribe antes del código y sin mezclarla con funcionalidades como "el botón Lend se deshabilita" (página 6 del Confluence, Reglas e invariantes).

## Bloque 2 — Estructura monolítica (12–18 min)

Muestra la estructura ya creada en la vista Project (`backend/library`):

```text
src/main/java/ec/edu/uce/library/
├── lending/                ← contexto core
│   ├── domain/             ← Copy, Loan, eventos, puerto CopyRepository (sin Spring)
│   ├── application/        ← caso de uso LendCopy
│   └── infrastructure/     ← controlador REST, adaptador JPA, configuración
├── catalog/                ← contexto de soporte: Book
│   ├── domain/
│   ├── application/
│   └── infrastructure/
└── members/                ← contexto genérico: Member
    └── ...
```

**En IntelliJ**

1. Expande los paquetes para que se vea la forma (cada capa tiene un `package-info.java` que explica qué va ahí).
2. Doble Shift: salta entre capas para mostrar que se navega rápido.
3. Explica que **Analyze → Dependencies** sirve para comprobar que `domain` no importa nada de `infrastructure` (se muestra al final del bloque 4, cuando ya hay código).

**Reglas en voz alta**

- Lending no usa la clase `Book` de Catalog; guarda solo su identificador (`BookId`). Igual con `MemberId`.
- La dependencia va hacia adentro: infrastructure → application → domain.
- Un contexto podría convertirse en servicio más adelante, pero hoy es un paquete.

## Bloque 3 — DDD: del Confluence al diseño (18–27 min)

Recorre las páginas del espacio BIB y decide el diseño en la pizarra, sin escribir código.

| Paso | Concepto | Página | Decisión para la biblioteca |
| --- | --- | --- | --- |
| 1 | Subdominios | 7 | Lending = core; Catalog = soporte; Members = genérico/soporte |
| 2 | Contextos y mapa | 8 | Tres paquetes raíz; Lending consume `BookId` y `MemberId` |
| 3 | Lenguaje ubicuo | 5 | Member, Book, Copy, Loan, Loan period, Lend, Return, Available |
| 4 | Reglas e invariantes | 6 | BR-01 y BR-02 (BR-03 queda para la tarea) |
| 5 | Modelo táctico | 9 | Agregado `Copy`; objetos de valor `CopyId`, `LoanPeriod`; entidad interna `Loan` |
| 6 | Eventos de dominio | 10 | `CopyLent`, `CopyReturned` |

**Pregunta clave al grupo:** "¿Quién protege BR-01?". La respuesta es el agregado `Copy`, con `lend(memberId, period)` lanzando `CopyNotAvailableException` si ya está prestada y con `returnCopy()` cubriendo BR-02 (`return` es palabra reservada en Java).

**Error típico a nombrar:** validar la disponibilidad en el controlador o en el servicio. Si la regla vive fuera del agregado, otro caso de uso se la puede saltar.

## Bloque 4 — Claude Code en IntelliJ (27–42 min)

Ciclo: dar contexto, pedir un plan, aprobarlo, revisar el diff y ejecutar la prueba. Claude propone y el estudiante decide.

**Preparar la sesión (ya hecho antes de clase; en clase solo se muestra)**

1. Terminal integrada (Alt+F12), en la raíz del repo, ejecutar `claude` (o el plugin de JetBrains, para ver el diff en el visor del IDE).
2. Enseñar el `CLAUDE.md`.
3. Activar el modo plan (Shift+Tab hasta que aparezca "plan mode").

**Qué contiene el `CLAUDE.md` (ya está en `develop`)**

- Resumen en tres líneas del sistema y enlace al espacio BIB.
- Estructura de paquetes del bloque 2 y la regla de dependencias hacia adentro (`domain` sin Spring).
- Convenciones: los nombres salen del lenguaje ubicuo del Confluence (no se copia el glosario, se manda a leerlo), las reglas viven en el agregado y cada regla tiene su prueba.
- Comandos: `./gradlew test`, `./gradlew bootRun`, `npm run dev`, `npm run build`.

**Prompt de la demo (sin contexto externo)**

El prompt no nombra las clases a propósito: así se ve qué nombres elige Claude por su cuenta.

```text
En el contexto lending, crea el agregado que representa la copia física de un libro
y el caso de uso para prestarla a un estudiante.
Regla: una copia prestada no puede volver a prestarse hasta su devolución.
No uses Spring en domain. Escribe primero la prueba unitaria de la regla con JUnit 5.
No consultes el Confluence. Muéstrame el plan antes de editar archivos.
```

**Qué se comenta**

- El plan: ¿respeta las capas? ¿los nombres coinciden con el glosario?
- El diff: se revisa archivo por archivo antes de aceptar.
- La prueba: botón verde de IntelliJ (o `./gradlew test`); primero se ve fallar y luego pasar.
- Analyze → Dependencies: `domain` no depende de nada.

**Cierre del bloque:** sin el Confluence, Claude suele inventar nombres razonables pero que no son los del negocio: `Book` para la copia física, `User` en vez de Member, `borrow` en vez de `lend`, una excepción genérica en vez de `CopyNotAvailableException`, y sin eventos. El bloque 5 los corrige.

## Bloque 5 — Confluence como contexto (42–54 min)

Mismo pedido, ahora con Claude leyendo las fuentes reales. La diferencia entre las dos salidas es la demostración.

**Conexión (preparada antes de clase)**

1. `claude mcp add` para Atlassian (ver `docs/runbook-preparacion-entorno.md`, paso 7).
2. Ejecutar `/mcp` e iniciar sesión en el navegador.
3. Prueba corta: "lista las páginas del espacio BIB en Confluence".

**Qué lee Claude**

| Fuente | Elemento | Para qué sirve |
| --- | --- | --- |
| Confluence | 5 · Lenguaje ubicuo | Nombres exactos: `Copy`, `Member`, `lend`, `returnCopy`, `isAvailable` |
| Confluence | 6 · Reglas e invariantes | BR-01 y BR-02, cada una con su prueba |
| Confluence | 8 · Contextos y mapa | Paquete de cada clase; uso de `BookId` y `MemberId` en vez de las entidades |
| Confluence | 9 · Modelo táctico | `CopyId`, `Loan`, `LoanPeriod`, `CopyNotAvailableException`, `LendCopy`, `CopyRepository` |
| Confluence | 10 · Eventos de dominio | `CopyLent`, `CopyReturned` |
| Confluence | Figma · Pantalla "Lend copy" | Campos del formulario → entrada de `LendCopy` |

**Prompt enriquecido**

```text
Lee en el Confluence (espacio BIB) las páginas 5 a 10 y "Figma · Pantalla Lend copy".
Con eso, rehaz el agregado y el caso de uso del contexto lending
usando exactamente los términos del glosario, cubriendo BR-01 y BR-02 con pruebas
y emitiendo los eventos de dominio. No implementes BR-03. Muéstrame el plan antes de editar.
```

**Qué se compara:** nombres genéricos frente a los del glosario, reglas cubiertas por pruebas con su código (BR-01, BR-02), eventos que antes faltaban y campos de entrada que ahora coinciden con la pantalla Lend copy.

**Si se acaba el tiempo:** no ejecutes el prompt en vivo. Muestra el diff contra la solución: `git diff develop etapa-2-dominio -- backend` (o cambia a `demo/respaldo`).

**Si el MCP no conecta:** pide a Claude que lea `docs/confluence-demo-biblioteca.md`, que es la copia en Markdown de esas páginas.

**Cierre opcional (si sobra tiempo):** en `demo/respaldo`, arranca backend y frontend, presta `EJ-00123` desde la pantalla Prestar ejemplar y luego intenta `EJ-00456`: aparece "Este ejemplar no está disponible: está prestado hasta el 10/12/2026." (la API responde en inglés con `dueDate` y la pantalla lo muestra en español). La regla del agregado llega intacta hasta la interfaz.

## Plan B y problemas frecuentes

| Si pasa esto | Haz esto |
| --- | --- |
| No hay internet o Claude no responde | `git switch demo/respaldo`, recorrer los tags `etapa-2` a `etapa-4` comentando cada decisión |
| El MCP no autentica | `docs/confluence-demo-biblioteca.md` y capturas |
| Claude pone la regla en el controlador | Pedirle que la mueva al agregado y explicar por qué |
| La prueba no compila | Ejecutarla desde IntelliJ para ver el error completo y corregir en vivo |
| `./gradlew` usa Java 21 y falla | `JAVA_HOME` al JDK 25 (en IntelliJ: Settings → Build Tools → Gradle → Gradle JVM = 25) |
| No se puede cambiar de rama | Hay cambios del vivo sin guardar: `git stash` y luego `git switch` |
| El puerto 8080 o 5173 está ocupado | Cerrar la otra instancia (un `bootRun` o `npm run dev` anterior) |
| La licencia de IntelliJ no está lista | Edición gratuita o prueba de IntelliJ; nada de la demo depende de Ultimate |
| Preguntas fuera de tema | Anotarlas y responder al cierre |

## Cierre y criterios de éxito (54–60 min)

- [ ] El contexto `lending` tiene `domain`, `application` e `infrastructure`.
- [ ] BR-01 vive en el agregado `Copy` y su prueba pasa.
- [ ] Los nombres del código coinciden con el glosario del Confluence.
- [ ] Se vio la diferencia entre el resultado sin contexto y con contexto.
- [ ] Cada estudiante sabe qué revisó el humano y qué generó Claude.

**Mensajes finales**

1. El Confluence es la fuente; el código se deriva de él.
2. Claude Code propone; el estudiante revisa y decide.
3. Una regla de negocio se protege en el agregado, con una prueba.

**Tarea práctica (propuesta; ajustar a la rúbrica):** implementar **BR-03, "Un Member no puede tener más de tres Loans activos"**, partiendo de `demo/respaldo`. Primero se documenta en el Confluence del equipo (quién la protege y su prueba) y luego se implementa con Claude Code. La pregunta de diseño que deben responder por escrito: ¿qué agregado protege BR-03, si no puede ser `Copy` porque la regla abarca varias copias?
