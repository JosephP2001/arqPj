# Demo Biblioteca UCE — contenido para Confluence

Cada sección `##` es una página de Confluence. Copia el contenido de cada una en una página con el mismo título, dentro de un espacio o página padre llamado **"Demo Biblioteca"**.

Este archivo también es el **plan B** del bloque 5: si el conector MCP no funciona, se le pide a Claude que lea este archivo.

---

## 5 · Lenguaje ubicuo

| Término | Definición | En el código |
| --- | --- | --- |
| Lector | Estudiante o docente registrado que puede llevarse ejemplares | `LectorId` (en Préstamos solo se usa el identificador) |
| Obra | Libro como contenido: título, autor, edición | `ObraId` (la clase `Obra` vive en Catálogo) |
| Ejemplar | Copia física de una obra, con código de inventario | Agregado `Ejemplar`, identificador `EjemplarId` |
| Préstamo | Entrega de un ejemplar a un lector por un período | `Prestamo` (parte del agregado `Ejemplar`) |
| Período de préstamo | Fecha de entrega y fecha límite de devolución | Objeto de valor `PeriodoPrestamo` |
| Prestar | Acción de entregar un ejemplar disponible a un lector | Método `Ejemplar.prestar(lectorId, periodo)` |
| Devolver | Acción de recibir un ejemplar prestado | Método `Ejemplar.devolver()` |
| Disponible | Estado de un ejemplar que puede prestarse | `Ejemplar.estaDisponible()` |

**Términos que no se usan:** "Libro" (es ambiguo entre Obra y Ejemplar), "Usuario" (se dice Lector), "Book", "Loan".

---

## 6 · Reglas e invariantes

| Código | Regla | Quién la protege | Prueba esperada |
| --- | --- | --- | --- |
| RN-01 | Un ejemplar prestado no puede volver a prestarse hasta que se devuelva | Agregado `Ejemplar` | Prestar un ejemplar ya prestado lanza `EjemplarNoDisponible` |
| RN-02 | La devolución de un ejemplar lo deja disponible | Agregado `Ejemplar` | Después de `devolver()`, `estaDisponible()` es verdadero |
| RN-03 | Un lector no puede tener más de tres préstamos activos | Por definir (tarea) | El cuarto préstamo falla |

Las reglas describen el negocio, no la interfaz: "el botón Prestar se deshabilita" no es una regla, es una funcionalidad.

---

## 7 · Subdominios

| Subdominio | Tipo | Por qué |
| --- | --- | --- |
| Préstamos | Core | Es lo que diferencia el servicio de la biblioteca; aquí están las reglas |
| Catálogo | Soporte | Necesario, pero es un registro de obras sin reglas complejas |
| Lectores | Genérico/soporte | Datos del lector; podría venir del sistema académico de la UCE |

---

## 8 · Contextos y mapa

| Contexto | Paquete | Expone | Consume |
| --- | --- | --- | --- |
| Préstamos | `ec.edu.uce.biblioteca.prestamos` | Eventos `EjemplarPrestado`, `EjemplarDevuelto` | `ObraId`, `LectorId` |
| Catálogo | `ec.edu.uce.biblioteca.catalogo` | `ObraId` y datos de la obra | — |
| Lectores | `ec.edu.uce.biblioteca.lectores` | `LectorId` y estado del lector | — |

**Relaciones:** Préstamos es cliente de Catálogo y de Lectores, pero solo guarda sus identificadores. Ningún contexto comparte clases de entidad con otro.

**Capas dentro de cada contexto:** `domain` (sin Spring), `application` (casos de uso) e `infrastructure` (REST, JPA). Las dependencias apuntan hacia adentro.

---

## 9 · Modelo táctico (contexto Préstamos)

| Elemento | Tipo | Responsabilidad |
| --- | --- | --- |
| `Ejemplar` | Agregado (raíz) | Protege RN-01 y RN-02 |
| `EjemplarId` | Objeto de valor | Identidad del ejemplar (código de inventario) |
| `Prestamo` | Entidad interna | Lector y período del préstamo activo |
| `PeriodoPrestamo` | Objeto de valor | Fecha de entrega y fecha límite; la límite debe ser posterior a la de entrega |
| `EjemplarNoDisponible` | Excepción de dominio | Se lanza al violar RN-01 |
| `PrestarEjemplar` | Caso de uso (application) | Carga el ejemplar, llama a `prestar`, guarda y publica el evento |
| `EjemplarRepositorio` | Puerto (interfaz en domain) | Implementado en infrastructure |

---

## 10 · Eventos de dominio

| Evento | Cuándo ocurre | Datos | Quién podría escucharlo |
| --- | --- | --- | --- |
| `EjemplarPrestado` | Al prestar con éxito | `EjemplarId`, `LectorId`, `PeriodoPrestamo` | Notificaciones (recordatorio de devolución) |
| `EjemplarDevuelto` | Al devolver | `EjemplarId`, `LectorId`, fecha | Catálogo (estadísticas de uso) |

---

## Figma · Pantalla "Prestar ejemplar" (referencia para el wireframe)

Campos del formulario, que serán la entrada del caso de uso `PrestarEjemplar`:

1. **Código de ejemplar** (texto, obligatorio)
2. **Cédula o código del lector** (texto, obligatorio)
3. **Fecha límite de devolución** (fecha; por defecto, hoy + 15 días)
4. Botón **Prestar**
5. Mensaje de error: "El ejemplar no está disponible" (RN-01)
