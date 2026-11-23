# Demo Biblioteca UCE — contenido del Confluence

Copia en Markdown de las páginas de la demo en el espacio **Biblioteca UCE** (clave `BIB`) de `josephp2001.atlassian.net`, bajo la página padre **"Demo Biblioteca UCE"**. Incluye completas las páginas 5 a 10 y extractos de las páginas 4, 11, 12 y 13. Sincronizada con el Confluence el 23/11/2026. Los diagramas Mermaid de Confluence no se copian: aquí quedan las tablas que los acompañan.

**Convención de idioma (ADR-007):** el negocio, la interfaz y la documentación van en español (Ejemplar, Lector, Prestar); el código y los términos técnicos van en inglés (`Copy`, `Member`, `lend`, `BR-01`). La equivalencia está en la columna "Nombre en el código" del glosario (página 5).

**La fuente de verdad es el Confluence.** Este archivo es el **plan B** del bloque 5: si el conector MCP no funciona, se le pide a Claude que lea este archivo. Si cambias una página en el Confluence, actualiza también este archivo.

---

## 5 · Lenguaje ubicuo

**Convención de idioma (ADR-007).** El negocio y la interfaz hablan español (lo que ve y dice el personal de la UCE, igual que en Figma). El código y los términos técnicos van en inglés. Cada concepto tiene un solo nombre en cada idioma, definido en esta tabla; no se usan sinónimos.

### Glosario

| Término del negocio (pantalla) | Definición | Nombre en el código |
| --- | --- | --- |
| Lector | Estudiante o docente registrado que puede llevarse un ejemplar (ej.: María Andrade, 1712345678) | `Member`; en Lending solo se usa `MemberId` |
| Obra | El libro como contenido: título, autor, edición | `Book` / `BookId` (la clase `Book` vive en Catalog) |
| Ejemplar | Copia física de una obra, con código de inventario (ej.: EJ-00123, UCE-0001) | Agregado `Copy`, identificador `CopyId` |
| Préstamo | Entrega de un ejemplar a un lector por un período | `Loan` (entidad interna del agregado `Copy`) |
| Período de préstamo | Fecha de entrega y fecha límite de devolución | Objeto de valor `LoanPeriod(lentOn, dueOn)` |
| **Prestar** | Entregar un ejemplar disponible a un lector | `Copy.lend(memberId, period)`; caso de uso `LendCopy` |
| **Devolver** | Recibir un ejemplar prestado | `Copy.returnCopy(returnedOn)`; caso de uso `ReturnCopy` |
| **Disponible** | Estado de un ejemplar que puede prestarse | `Copy.isAvailable()` |
| Ejemplar no disponible | Error al intentar prestar un ejemplar ya prestado | `CopyNotAvailableException` |
| Ejemplar no prestado | Error al intentar devolver un ejemplar que no tiene un préstamo activo | `CopyNotLentException` |
| Uso del ejemplar | Cuántas veces se devolvió un ejemplar; lo lleva Catálogo para sus estadísticas de uso a partir del evento `CopyReturned` | `CopyUsage` (Catalog) con `recordReturn()` y `timesReturned()`; caso de uso `RecordCopyReturn` |
| Regla de negocio | Invariante que el dominio siempre debe cumplir | Código `BR-xx` (Business Rule), igual en Confluence, pruebas y código |

### Contextos delimitados

| Nombre del negocio | Contexto en el código | Paquete |
| --- | --- | --- |
| Préstamos | Lending | `ec.edu.uce.library.lending` |
| Catálogo | Catalog | `ec.edu.uce.library.catalog` |
| Lectores | Members | `ec.edu.uce.library.members` |

### Nombres en el código (resumen)

| Tipo | Nombres |
| --- | --- |
| Agregado y entidad | `Copy`, `Loan` (Lending); `CopyUsage` (Catalog) |
| Objetos de valor | `CopyId`, `MemberId`, `BookId` (propuesto), `LoanPeriod`. Catalog tiene su propio `CopyId`: guarda solo el identificador, no la clase de Lending. |
| Excepción de dominio | `CopyNotAvailableException`, `CopyNotLentException` |
| Casos de uso | `LendCopy`, `ReturnCopy` (Lending); `RecordCopyReturn` (Catalog); `ListActiveLoans`, `ListMemberLoans` (propuestos) |
| Eventos | `CopyLent`, `CopyReturned` |
| Puertos | `CopyRepository`, `DomainEventPublisher` (Lending); `CopyUsageRepository` (Catalog) |

### Términos que no se usan

| No usar | Usar en su lugar | Por qué |
| --- | --- | --- |
| libro | Obra (`Book`) o Ejemplar (`Copy`) | Es ambiguo: mezcla el contenido con la copia física |
| usuario, `User` | Lector (`Member`) o bibliotecario/a, según el rol | Es genérico y no distingue roles |
| `Item`, `Book` para la copia física | `Copy` | Demasiado genérico o incorrecto |
| `Borrow`, `Patron`, `Reader` | `lend`, `Member` | Sinónimos en inglés que no están en el glosario |
| Nombres de clase en español (`Ejemplar`, `Lector`, `Prestamo`) | `Copy`, `Member`, `Loan` | El código va en inglés |
| Términos en inglés en pantalla ("Copy", "Member", "Lend") | Ejemplar, Lector, Prestar | La interfaz va en español, como en Figma |
| RN-01, RN-02… | `BR-01`, `BR-02`… | Un solo código de regla en todos los artefactos |

### Regla de idioma (ADR-007)

Cada concepto tiene un nombre de negocio en español y un nombre en el código en inglés (columna **Nombre en el código** del glosario). Esta tabla dice cuál se usa en cada lugar.

| Dónde | Idioma | Ejemplo |
| --- | --- | --- |
| **Código**: paquetes, clases, métodos, campos, eventos, excepciones, nombres de pruebas, campos y mensajes de la API, commits | Inglés, tomado solo de la columna **Nombre en el código**. Nunca identificadores en español (`EjemplarPrestado`, `codigoEjemplar`). | `Copy.lend()`, `CopyLent`, `copyCode`, `br01_lendingALentCopyThrowsCopyNotAvailableException` |
| **Negocio, pantalla y documentación** (texto corrido, reglas, mensajes) | Español, con el término del negocio del glosario | "Un ejemplar prestado no puede volver a prestarse hasta que se devuelva." |
| **La documentación nombra un elemento del código** | El nombre en inglés, en formato de código | "La regla la protege el agregado `Copy` en `lend`." |
| **Figma** | Textos de pantalla y nombres de capas en español; cuando una pantalla muestra una regla o un evento, usa el código: `BR-01`, `CopyLent` | Capa `codigoEjemplar` → campo `copyCode` (trazabilidad en la página 13) |

#### Términos que no se traducen

Se escriben igual en el código, la documentación y la conversación:

| Tipo | Términos |
| --- | --- |
| Códigos de regla | `BR-xx` (no RN-xx) |
| Patrones DDD sin traducción habitual | Core, Context Map, ACL, Handoff, DDD |
| Términos técnicos | API, endpoint, frontend, backend, commit, branch, merge, pull request, tag, prompt, MCP, runbook |
| Productos y tecnologías | Spring Boot, Java, Gradle, React, TypeScript, Vite, MUI, H2, JPA, Figma, Confluence, Claude Code, IntelliJ |

Los conceptos DDD que ya tienen nombre en español se escriben en español: agregado, objeto de valor, entidad, evento de dominio, caso de uso, puerto, subdominio, contexto delimitado.

---

## 6 · Reglas e invariantes

Las reglas se redactan en español y con palabras del negocio; el código `BR-xx` es el mismo en Confluence, en el nombre de las pruebas y en el código. Las reglas no hablan de botones ni pantallas.

| Código | Regla | Quién la protege | Prueba en el código | Mensaje en pantalla |
| --- | --- | --- | --- | --- |
| `BR-01` | Un ejemplar prestado no puede volver a prestarse hasta que se devuelva | Agregado `Copy` (método `lend`) | `br01_lendingALentCopyThrowsCopyNotAvailableException` (lanza `CopyNotAvailableException`) | "Este ejemplar no está disponible: está prestado hasta el 10/12/2026." |
| `BR-02` | La devolución de un ejemplar lo deja **Disponible** | Agregado `Copy` (método `returnCopy`) | `br02_returningACopyLeavesItAvailable` (después de `returnCopy`, `isAvailable()` es verdadero); en el caso de uso, `br02_returnsTheCopySavesItAvailableAndPublishesCopyReturned` | "Ejemplar devuelto. Ya está disponible." |
| `BR-03` | Un lector no puede tener más de tres préstamos activos | Por definir (tarea de la clase) | Sugerida: `br03_lendingAFourthCopyToAMemberFails` | Por definir |

### Invariante del objeto de valor

| Elemento | Invariante | Quién la protege | Prueba en el código |
| --- | --- | --- | --- |
| `LoanPeriod` (período de préstamo) | La fecha límite (`dueOn`) debe ser posterior a la de entrega (`lentOn`) | El propio objeto de valor (constructor del `record`) | `LoanPeriodTest` |

---

## 7 · Subdominios

| Subdominio (negocio) | Contexto en el código | Tipo | Por qué |
| --- | --- | --- | --- |
| Préstamos | Lending (`lending`) | Core | Es lo que diferencia el servicio de la biblioteca; aquí están las reglas (`BR-01` a `BR-03`) |
| Catálogo | Catalog (`catalog`) | Soporte | Necesario, pero es un registro de obras sin reglas complejas |
| Lectores | Members (`members`) | Genérico / soporte | Datos del lector; podrían venir del Sistema Académico UCE |

| Clasificación | Qué implica | Subdominio |
| --- | --- | --- |
| Core | Mayor inversión de modelado y pruebas | Préstamos (Lending) |
| Soporte | Implementación simple | Catálogo (Catalog) |
| Genérico / soporte | Comprar o reutilizar si es posible | Lectores (Members) |

---

## 8 · Contextos y mapa

**Decisión tomada:** el paquete raíz es `ec.edu.uce.library` y los contextos se nombran en inglés en el código (ADR-007). En pantalla y en el negocio se siguen llamando Préstamos, Catálogo y Lectores.

| Contexto (negocio) | Paquete | Expone | Consume |
| --- | --- | --- | --- |
| Préstamos (Lending) | `ec.edu.uce.library.lending` | Eventos `CopyLent`, `CopyReturned` | `BookId`, `MemberId` |
| Catálogo (Catalog) | `ec.edu.uce.library.catalog` | `BookId` y datos de la obra | — |
| Lectores (Members) | `ec.edu.uce.library.members` | `MemberId` y estado del lector | Datos del Sistema Académico UCE (vía ACL) |

**Relaciones:** Lending es cliente de Catalog y de Members, pero solo guarda sus identificadores. Ningún contexto comparte clases de entidad con otro. Entre contextos solo viaja el lenguaje publicado de Lending: sus eventos y los identificadores que llevan (ADR-008). `ArchitectureTest` lo comprueba en cada build.

**Capas dentro de cada contexto:** `domain` (sin Spring), `application` (casos de uso) e `infrastructure` (REST, JPA). Las dependencias apuntan hacia adentro.

### Patrones DDD por relación

| Relación | Patrón | Explicación |
| --- | --- | --- |
| Lending y Catalog | Cliente / proveedor | Lending (cliente) consulta a Catalog (proveedor) y solo guarda `BookId` |
| Lending y Catalog | Conformista | Lending acepta `BookId` tal como lo define Catalog, sin traducirlo |
| Lending y Members | Cliente / proveedor | Lending (cliente) solo usa `MemberId`; Members es el proveedor |
| Members y Sistema Académico UCE | ACL (capa anticorrupción) | Members traduce los datos del sistema externo a su propio modelo (supuesto por validar) |
| Lending hacia Catalog y Notificaciones | Eventos de dominio | Lending publica `CopyLent` y `CopyReturned`; quien escucha reacciona sin que Lending lo conozca |

### Context Map

| Desde | Hacia | Patrón | Qué viaja |
| --- | --- | --- | --- |
| Sistema Académico UCE | Members | ACL | Datos del lector traducidos |
| Members | Lending | Cliente / proveedor | `MemberId` |
| Catalog | Lending | Cliente / proveedor y Conformista | `BookId` |
| Lending | Catalog | Eventos de dominio | `CopyReturned` (estadísticas de uso: `CopyUsage`) |
| Lending | Notificaciones | Eventos de dominio | `CopyLent` (recordatorio de devolución) |

---

## 9 · Modelo táctico (contexto Préstamos / Lending)

Paquete: `ec.edu.uce.library.lending.domain` (Java puro, sin Spring). Implementado en la rama `demo/respaldo`.

| Elemento (código) | Término del negocio | Tipo | Responsabilidad |
| --- | --- | --- | --- |
| `Copy` | Ejemplar | Agregado (raíz) | Protege `BR-01` (`lend`) y `BR-02` (`returnCopy`) |
| `CopyId` | Código de ejemplar | Objeto de valor (`record`) | Identidad del ejemplar (código de inventario, no vacío) |
| `MemberId` | Lector (identificador) | Objeto de valor | Identifica al lector dentro de Lending (cédula o código) |
| `Loan` | Préstamo | Entidad interna | `MemberId` y `LoanPeriod` del préstamo activo |
| `LoanPeriod` | Período de préstamo | Objeto de valor (`record`) | `lentOn` y `dueOn`; `dueOn` debe ser posterior a `lentOn` |
| `CopyNotAvailableException` | Ejemplar no disponible | Excepción de dominio | Se lanza al violar `BR-01`; lleva la fecha límite (`dueOn`) del préstamo activo para que la pantalla diga hasta cuándo está prestado |
| `CopyNotLentException` | Ejemplar no prestado | Excepción de dominio | La lanza `returnCopy` si el ejemplar no tiene un préstamo activo |
| `CopyLent`, `CopyReturned` | Ejemplar prestado / devuelto | Eventos de dominio | Los devuelven `lend` y `returnCopy` (página 10) |
| `CopyRepository` | — | Puerto (interfaz en `domain`) | `findById(CopyId)` y `save(Copy)`; implementado con JPA en `infrastructure` |
| `DomainEventPublisher` | — | Puerto (interfaz en `application`) | `publish(event)`; lo usa el caso de uso después de guardar. Implementado en `infrastructure` con el `ApplicationEventPublisher` de Spring |
| `LendCopy` | Prestar ejemplar | Caso de uso (`application`) | Carga el `Copy`, llama a `lend`, guarda y publica `CopyLent` |
| `ReturnCopy` | Devolver ejemplar | Caso de uso (`application`) | Carga el `Copy`, llama a `returnCopy`, guarda y publica `CopyReturned` |
| `BookId` | Obra (identificador) | Objeto de valor | **Propuesto:** todavía no está en `Copy` |

### Agregado `Copy`

| Relación | Significado |
| --- | --- |
| `Copy` contiene `Loan` (0..1) | Un ejemplar tiene como máximo un préstamo activo (base de `BR-01`) |
| `Loan` usa `MemberId` y `LoanPeriod` | No guarda el lector completo, solo su identificador |
| `Copy` lanza `CopyNotAvailableException` | Al prestar un ejemplar que no está **Disponible** |
| `CopyRepository` persiste `Copy` | Interfaz en `domain`, implementación JPA (`JpaCopyRepository`) en `infrastructure` |

Métodos de `Copy`: `lend(MemberId, LoanPeriod)` devuelve `CopyLent`; `returnCopy(LocalDate)` devuelve `CopyReturned`; `isAvailable()`; `rehydrate(CopyId, MemberId, LoanPeriod)` (estático, para reconstruirlo desde la base de datos).

---

## 10 · Eventos de dominio

| Evento (código) | En el negocio | Cuándo ocurre | Datos | Quién podría escucharlo |
| --- | --- | --- | --- | --- |
| `CopyLent` | Ejemplar prestado | Al prestar con éxito (pantalla D03) | `copyId`, `memberId`, `loanPeriod` | Notificaciones (recordatorio de devolución) |
| `CopyReturned` | Ejemplar devuelto | Al devolver (D04) | `copyId`, `memberId`, `returnedOn` | Catalog (estadísticas de uso) |

Los eventos se nombran en pasado. El agregado los **devuelve** (`lend` y `returnCopy`) y el caso de uso los **publica** con el puerto `DomainEventPublisher` después de guardar. Si se viola `BR-01` no se guarda nada ni se publica ningún evento.

**Estado en el código:** `CopyLent` se publica desde `LendCopy` y `CopyReturned` desde `ReturnCopy` (en Spring, con `ApplicationEventPublisher`). Catalog escucha `CopyReturned` (`CopyReturnedListener`) y llama a su caso de uso `RecordCopyReturn`, que suma una devolución al `CopyUsage` del ejemplar; se consulta en `GET /api/copy-usages/{copyCode}`. `CopyLent` todavía no tiene consumidor (Notificaciones es futuro).

| Origen | Resultado | Evento | Consumidor |
| --- | --- | --- | --- |
| `LendCopy` | Correcto | `CopyLent` | Notificaciones |
| `LendCopy` | Viola `BR-01` | Ninguno (`CopyNotAvailableException`) | — |
| `ReturnCopy` | Correcto (`BR-02`) | `CopyReturned` | Catalog (`RecordCopyReturn` → `CopyUsage`) |
| `ReturnCopy` | El ejemplar no estaba prestado | Ninguno (`CopyNotLentException`) | — |

---

## 4 · Línea gráfica (extracto: Voz y tono)

La interfaz siempre está en español. La API responde mensajes en inglés y, para `BR-01`, la fecha límite en el campo `dueDate` (por ejemplo, `{"message": "The copy is not available", "dueDate": "2026-12-10"}`); el frontend arma el texto en español de esta tabla con esa fecha. Los valores de la paleta están en `frontend/src/theme.ts`.

| Situación | Evitar | Usar |
| --- | --- | --- |
| `BR-01` | Error 409 / "The copy is not available" | Este ejemplar no está disponible: está prestado hasta el 10/12/2026. |
| Éxito | Operación exitosa | Préstamo registrado. Devolver hasta el 08/12/2026. |
| `BR-02` | Actualizado | Ejemplar devuelto. Ya está disponible. |

---

## 11 · Casos de uso (extracto)

| Caso de uso (código) | En pantalla | Actor | Regla | Evento | Pantallas | Implementado |
| --- | --- | --- | --- | --- | --- | --- |
| `LendCopy` | Prestar ejemplar | Bibliotecario/a | `BR-01` | `CopyLent` | D02, D03 | Sí, `POST /api/loans` |
| `ReturnCopy` | Devolver ejemplar | Bibliotecario/a | `BR-02` | `CopyReturned` | D04, D06 | Sí, `POST /api/loans/{copyCode}/return` |
| `ListActiveLoans` | Préstamos activos | Bibliotecario/a | — | — | D01, D05, D06, D08 | Fuera de alcance (nombre propuesto) |
| `ListMemberLoans` | Mis préstamos | Lector | `BR-03` (tarea) | — | M01, M02, M05 | Fuera de alcance (nombre propuesto) |

### LendCopy (Prestar ejemplar)

| Elemento | Detalle |
| --- | --- |
| Entrada | `LendCopyCommand(copyCode, memberId, dueDate)`, desde `POST /api/loans` |
| Precondiciones | El ejemplar existe (si no, `CopyNotFoundException` = 404) y el lector está identificado con su cédula o código. |
| Flujo principal | 1. El/la bibliotecario/a escanea o escribe el código del ejemplar; la pantalla muestra su estado. 2. Ingresa el lector. 3. Revisa la fecha límite (por defecto, hoy + 15 días). 4. Pulsa **Prestar**. 5. `LendCopy` carga el `Copy` con `CopyRepository`, crea el `LoanPeriod(hoy, dueDate)`, llama a `copy.lend(memberId, period)`, guarda y publica `CopyLent`. 6. La pantalla confirma: "Préstamo registrado. Devolver hasta el 08/12/2026." |
| Flujo alternativo (`BR-01`) | En el paso 5 el ejemplar ya está prestado: se lanza `CopyNotAvailableException` (HTTP 409) con la fecha límite del préstamo activo; no se guarda nada ni se publica evento. La respuesta es `{"message": "The copy is not available", "dueDate": "2026-12-10"}` y la pantalla explica en español: "Este ejemplar no está disponible: está prestado hasta el 10/12/2026." |
| Postcondiciones | El ejemplar deja de estar **Disponible**; existe un `Loan` activo con su `LoanPeriod`; se publicó `CopyLent`. |

### ReturnCopy (Devolver ejemplar)

| Elemento | Detalle |
| --- | --- |
| Entrada | `ReturnCopyCommand(copyCode)`, desde `POST /api/loans/{copyCode}/return` |
| Precondiciones | El ejemplar existe (si no, `CopyNotFoundException` = 404). |
| Flujo principal | 1. Escanea o escribe el código del ejemplar que el lector devuelve. 2. Pulsa **Devolver**. 3. `ReturnCopy` carga el `Copy`, llama a `copy.returnCopy(hoy)`, guarda y publica `CopyReturned`. 4. La pantalla confirma: "Ejemplar devuelto. Ya está disponible." 5. Catalog recibe `CopyReturned` y suma una devolución al `CopyUsage` del ejemplar. |
| Flujo alternativo | El ejemplar no estaba prestado: `returnCopy` lanza `CopyNotLentException` (HTTP 409, "The copy is not lent"); no se guarda nada ni se publica evento. La pantalla muestra "Ejemplar no prestado: Este ejemplar no tiene un préstamo activo." |
| Postcondiciones | `isAvailable()` es verdadero (`BR-02`); el `Loan` deja de estar activo; se publicó `CopyReturned`. |

---

## 12 · Arquitectura y decisiones (ADR) (extracto: ADR-007 y ADR-008)

### ADR-007 · Código en inglés, negocio e interfaz en español

| Contexto | Decisión | Consecuencias |
| --- | --- | --- |
| El personal de la UCE habla español, pero el código del equipo y sus convenciones (commits, identificadores) están en inglés. | Identificadores, paquetes, eventos y códigos de regla en inglés (`Copy`, `Member`, `Loan`, `CopyLent`, `BR-01`). Textos de pantalla, mensajes y documentación del negocio en español (Ejemplar, Lector, Prestar). La equivalencia está en el glosario de la página 5; la regla completa (qué idioma se usa en cada lugar y qué términos no se traducen) está en la sección **Regla de idioma** de esa página. | (+) El código sigue convenciones estándar y la interfaz habla el idioma del usuario. (−) El glosario debe mantenerse con ambos nombres; la API devuelve mensajes en inglés que el frontend debe mostrar en español. |

### ADR-008 · Entre contextos solo viajan los eventos de dominio y sus identificadores

| Contexto | Decisión | Consecuencias |
| --- | --- | --- |
| Catalog necesita saber cuándo se devuelve un ejemplar (estadísticas de uso, página 8), pero ningún contexto debe usar las clases de otro (ADR-001). | El lenguaje publicado de Lending son sus eventos (`CopyLent`, `CopyReturned`) y los identificadores que llevan (`CopyId`, `MemberId`). Solo un adaptador de `infrastructure` de otro contexto puede usarlos (`CopyReturnedListener` en Catalog), y traduce el identificador a su propio modelo (`CopyId` de Catalog, `CopyUsage`). | (+) Los contextos se comunican sin conocerse: Lending no sabe que Catalog escucha. (+) `ArchitectureTest` permite solo esta excepción; usar `Copy` o `Loan` desde otro contexto, o un evento desde un `domain` ajeno, rompe el build. (−) Cambiar un evento afecta a quien lo escucha. |

---

## 13 · Diseño de interfaz (Figma) (extracto)

Archivo de Figma: **Biblioteca UCE** (`https://www.figma.com/design/bFFvd1YJgBEK26FKwuUXtR/Biblioteca--UCE`). Los textos de pantalla y los nombres de las capas están en español. Plan B si el MCP de Figma no responde: `docs/figma/D02-prestar-ejemplar-no-disponible.png` y `docs/figma/D03-prestamo-confirmado.png`.

Figma sigue la regla de idioma: las reglas se nombran `BR-01` a `BR-03` y los eventos con su nombre del código (`CopyLent`, `CopyReturned`).

### Trazabilidad Handoff DDD (Figma, Confluence, código)

| Figma (español) | Confluence (página 5) | Código (inglés) | API / campo |
| --- | --- | --- | --- |
| Capa `codigoEjemplar` | Ejemplar | `CopyId` | `copyCode` |
| Capa `lectorId` | Lector | `MemberId` | `memberId` |
| Capa `fechaLimite` | Período de préstamo | `LoanPeriod.dueOn` | `dueDate` |
| Botón Prestar (`btnPrestar`) | Prestar | `LendCopy` y `Copy.lend()` | `POST /api/loans` |
| D02 – Ejemplar no disponible | `BR-01` (página 6) | `CopyNotAvailableException` | 409 `{"message": "The copy is not available", "dueDate": "2026-12-10"}`; la pantalla lo muestra en español con la fecha |
| D04 – Devuelto | `BR-02` (página 6) | `ReturnCopy` y `Copy.returnCopy()` | `POST /api/loans/{copyCode}/return` (200; 409 `CopyNotLentException`) |
| D03 Préstamo confirmado | Eventos (página 10) | `CopyLent` | 201 Created |
| `Organismo/Prestamos/*` | Contexto Préstamos (página 8) | Paquete `ec.edu.uce.library.lending` | — |

### Campos del formulario Prestar (pantalla D02, entrada de `LendCopy`)

| # | Campo en pantalla | Detalle | Campo de `LendCopyCommand` |
| --- | --- | --- | --- |
| 1 | Código de ejemplar | Texto, obligatorio ("Escanea o escribe el código") | `copyCode` |
| 2 | Lector (cédula o código) | Texto, obligatorio | `memberId` |
| 3 | Fecha límite de devolución | Fecha; por defecto, hoy + 15 días | `dueDate` |
| 4 | Botón **Prestar** | Un solo botón principal | — |
| 5 | Mensaje de error | "Este ejemplar no está disponible: está prestado hasta el 10/12/2026." (`BR-01`) | — |
