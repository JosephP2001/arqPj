# Demo UCE Library — contenido del Confluence

Copia en Markdown de las páginas de la demo en el espacio **Biblioteca UCE** (clave `BIB`) de `josephp2001.atlassian.net`, bajo la página padre **"Demo UCE Library"**. Terminología del dominio en inglés.

**La fuente de verdad es el Confluence.** Este archivo es el **plan B** del bloque 5: si el conector MCP no funciona, se le pide a Claude que lea este archivo. Si cambias una página en el Confluence, actualiza también este archivo.

---

## 5 · Lenguaje ubicuo

| Término | Definición | En el código |
| --- | --- | --- |
| Member | Estudiante o docente registrado que puede llevarse una Copy | `MemberId` (en Lending solo se usa el identificador) |
| Book | El libro como contenido: título, autor, edición | `BookId` (la clase `Book` vive en Catalog) |
| Copy | Copia física de un Book, con código de inventario | Agregado `Copy`, identificador `CopyId` |
| Loan | Entrega de una Copy a un Member por un período | `Loan` (parte del agregado `Copy`) |
| Loan period | Fecha de entrega y fecha límite de devolución | Objeto de valor `LoanPeriod` |
| Lend | Acción de entregar una Copy disponible a un Member | Método `Copy.lend(memberId, period)` |
| Return | Acción de recibir una Copy prestada | Método `Copy.returnCopy()` (`return` es palabra reservada en Java) |
| Available | Estado de una Copy que puede prestarse | `Copy.isAvailable()` |

**Términos que no se usan:** "User" (se dice Member), "Item", "Book" para referirse a la copia física (eso es Copy), y los nombres en español del modelo anterior (Lector, Obra, Ejemplar, Préstamo).

---

## 6 · Reglas e invariantes

| Código | Regla | Quién la protege | Prueba esperada |
| --- | --- | --- | --- |
| BR-01 | Una Copy prestada no puede volver a prestarse hasta que se devuelva | Agregado `Copy` | Prestar una Copy ya prestada lanza `CopyNotAvailableException` |
| BR-02 | La devolución de una Copy la deja Available | Agregado `Copy` | Después de `returnCopy()`, `isAvailable()` es verdadero |
| BR-03 | Un Member no puede tener más de tres Loans activos | Por definir (tarea) | El cuarto Loan falla |

Las reglas describen el negocio, no la interfaz: "el botón Lend se deshabilita" no es una regla, es una funcionalidad.

---

## 7 · Subdominios

| Subdominio | Tipo | Por qué |
| --- | --- | --- |
| Lending | Core | Es lo que diferencia el servicio de la biblioteca; aquí están las reglas |
| Catalog | Soporte | Necesario, pero es un registro de Books sin reglas complejas |
| Members | Genérico/soporte | Datos del Member; podría venir del sistema académico de la UCE |

---

## 8 · Contextos y mapa

| Contexto | Paquete | Expone | Consume |
| --- | --- | --- | --- |
| Lending | `ec.edu.uce.library.lending` | Eventos `CopyLent`, `CopyReturned` | `BookId`, `MemberId` |
| Catalog | `ec.edu.uce.library.catalog` | `BookId` y datos del Book | — |
| Members | `ec.edu.uce.library.members` | `MemberId` y estado del Member | — |

**Relaciones:** Lending es cliente de Catalog y de Members, pero solo guarda sus identificadores. Ningún contexto comparte clases de entidad con otro.

**Capas dentro de cada contexto:** `domain` (sin Spring), `application` (casos de uso) e `infrastructure` (REST, JPA). Las dependencias apuntan hacia adentro.

---

## 9 · Modelo táctico (contexto Lending)

| Elemento | Tipo | Responsabilidad |
| --- | --- | --- |
| `Copy` | Agregado (raíz) | Protege BR-01 y BR-02 |
| `CopyId` | Objeto de valor | Identidad de la Copy (código de inventario) |
| `Loan` | Entidad interna | Member y período del Loan activo |
| `LoanPeriod` | Objeto de valor | Fecha de entrega y fecha límite; la límite debe ser posterior a la de entrega |
| `CopyNotAvailableException` | Excepción de dominio | Se lanza al violar BR-01 |
| `LendCopy` | Caso de uso (application) | Carga la Copy, llama a `lend`, guarda y publica el evento |
| `CopyRepository` | Puerto (interfaz en domain) | Implementado en infrastructure |

---

## 10 · Eventos de dominio

| Evento | Cuándo ocurre | Datos | Quién podría escucharlo |
| --- | --- | --- | --- |
| `CopyLent` | Al prestar con éxito | `CopyId`, `MemberId`, `LoanPeriod` | Notificaciones (recordatorio de devolución) |
| `CopyReturned` | Al devolver | `CopyId`, `MemberId`, fecha | Catalog (estadísticas de uso) |

---

## Figma · Pantalla "Lend copy" (referencia para el wireframe)

Campos del formulario, que serán la entrada del caso de uso `LendCopy`:

1. **Copy code** (texto, obligatorio)
2. **Member ID**: cédula o código del Member (texto, obligatorio)
3. **Due date**: fecha límite de devolución (fecha; por defecto, hoy + 15 días)
4. Botón **Lend**
5. Mensaje de error: "The copy is not available" (BR-01)
