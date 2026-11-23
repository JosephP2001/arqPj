# Runbook — Preparación del entorno (IntelliJ + Claude Code + Confluence)

Nov 22, 2026 · Registro de lo que se hizo para preparar la clase demostrativa, ordenado para poder repetirlo, o mostrarlo, en clase.

> **Repositorio público:** este archivo no incluye correos, el ID de licencia, tokens ni códigos de activación. Donde aparece `<tu-sitio>`, va el nombre del sitio de Atlassian.

## Resumen del entorno final

| Pieza | Valor |
| --- | --- |
| Sistema operativo | Windows 11 |
| IDE | IntelliJ IDEA Ultimate, con la licencia JetBrains Educational Pack |
| JDK | Eclipse Temurin 25 (la misma versión que el proyecto del equipo) |
| Git | 2.51 para Windows |
| Claude Code | 2.1.330 (instalador nativo), con la sesión de la suscripción Claude Pro |
| Repositorio | `JosephP2001/arqPj`, rama `main`, clonado en `D:\Arquitectura\Git\arqPj` |
| Confluence | Sitio Atlassian Cloud Free nuevo, espacio **Biblioteca UCE** (clave `BIB`) |
| Navegador predeterminado | Chrome (todos los inicios de sesión se hacen ahí) |

## Orden recomendado

1. Navegador predeterminado
2. Licencia de JetBrains
3. Git y clonar el repo
4. Claude Code
5. IntelliJ: abrir el proyecto y configurar el JDK 25
6. Confluence: crear el sitio y el espacio
7. Conectar Claude Code con Confluence (MCP)
8. Crear las páginas de DDD con Claude
9. Figma (pendiente)

---

## 1. Navegador predeterminado

**Problema que se encontró:** IntelliJ y Claude Code abren el navegador **predeterminado** para iniciar sesión. Ese navegador era Edge, con otra cuenta de GitHub, así que el inicio de sesión se hacía con la cuenta equivocada.

**Solución:** Configuración de Windows → Aplicaciones → Aplicaciones predeterminadas → Google Chrome → **Establecer como predeterminado**.

**Si se abre el navegador equivocado:** no inicies sesión ahí. Copia toda la dirección de la barra (por ejemplo `https://account.jetbrains.com/login?authSessionId=...`), pégala en Chrome y autoriza desde Chrome.

## 2. Licencia de JetBrains (estudiante UCE)

1. En jetbrains.com/student, solicita la licencia educativa. Si el correo `@uce.edu.ec` no se reconoce automáticamente, usa el GitHub Student Developer Pack, una tarjeta ISIC o un documento oficial.
2. Comprueba en **account.jetbrains.com/licenses** que aparece **JetBrains Educational Pack** (válido por un año e incluye IntelliJ IDEA Ultimate).
3. **No hay que descargar el "Educational Pack":** es una licencia, no un programa. Basta con IntelliJ instalado (se recomienda JetBrains Toolbox).
4. En IntelliJ, el aviso "Ultimate subscription trial has started" → **Activate subscription…** (o el botón **Trial**, o Help → Manage Subscriptions) → **JetBrains Account** → **Log In to JetBrains Account**.
5. Autoriza en Chrome (ver el punto 1 si se abre Edge).

**Alternativa sin navegador:** en account.jetbrains.com/licenses → **Download activation code for offline usage**. En IntelliJ: **Activation code** → pega el código. El código es personal: no se sube al repositorio.

**Cómo verificar:** desaparece el indicador "Trial", y en Help → About aparece "Licensed to …" con la fecha de vencimiento de la licencia educativa.

## 3. Git y clonar el repositorio

En CMD:

```bat
git --version
cd /d D:\Arquitectura\Git
git clone https://github.com/JosephP2001/arqPj.git
```

**Error que se cometió:** `git clonehttps://...`. Debe haber un espacio entre `clone` y la dirección.

**Rama principal:** el repo estaba vacío y la primera rama subida quedó como principal. Se renombró a `main`:

```bat
git branch -m main
git push -u origin main
```

Luego, en GitHub → Settings → General → Default branch → **main**.

## 4. Claude Code

### Instalación en Windows

Los comandos dependen de la consola:

| Consola | Comando |
| --- | --- |
| **CMD** (`C:\...>`) | `curl -fsSL https://claude.ai/install.cmd -o install.cmd && install.cmd && del install.cmd` |
| **PowerShell** (`PS C:\...>`) | `irm https://claude.ai/install.ps1 \| iex` |

**Error que se encontró:** `'irm' is not recognized`. Ocurre al ejecutar el comando de PowerShell en CMD.

### Agregar al PATH

El instalador avisa: `C:\Users\<usuario>\.local\bin is not in your PATH`.

- **Con ventanas:** Tecla Windows → "variables de entorno" → **Editar las variables de entorno de tu cuenta** → Path → **Editar** → **Nuevo** → `C:\Users\<usuario>\.local\bin` → Aceptar.
- **Con un comando** (desde CMD):

```bat
powershell -NoProfile -Command "[Environment]::SetEnvironmentVariable('Path', [Environment]::GetEnvironmentVariable('Path','User') + ';C:\Users\<usuario>\.local\bin', 'User')"
```

No uses `setx PATH`, porque puede recortar el PATH. **Cierra y vuelve a abrir la terminal**, y también IntelliJ, para que tome el cambio.

```bat
claude --version
```

### Inicio de sesión

```bat
cd /d D:\Arquitectura\Git\arqPj
claude
```

1. Tema: **Auto**.
2. Método de inicio de sesión: **1. Claude account with subscription**. **No elijas la opción 2 (Anthropic Console)**: esa cobra por uso de la API.
3. Autoriza en Chrome.
4. Prueba: `hola, ¿qué archivos hay en este repositorio?`

**Notas para la clase:**

- Claude Code arranca en **auto mode**. Para la demo se usa el **modo plan**: pulsa **Shift+Tab** hasta que aparezca "plan mode".
- El plan Pro tiene límites de uso: ensaya la demo una sola vez y no gastes cupo en pruebas el día de la clase.

## 5. IntelliJ: proyecto y JDK

1. **Open** → `D:\Arquitectura\Git\arqPj` → **Trust Project**.
2. Menú ☰ → File → **Project Structure** (Ctrl+Alt+Shift+S).
3. **SDK** → **Download JDK…** → Version **25**, Vendor **Eclipse Temurin** → Download.
   - No uses la versión que IntelliJ propone por defecto (era la 27, que no es LTS) ni el JDK de Oracle descargado desde la web.
4. **Language level:** **25 - Compact source files, module imports, etc.** (sin "Preview") → **OK**.

**Claude dentro de IntelliJ, de dos formas:**

| Forma | Cómo | Uso en clase |
| --- | --- | --- |
| Terminal integrada | Alt+F12 → `claude` → `/ide` para conectar con el IDE | **Principal**: es lo que los estudiantes van a repetir |
| Panel Air (agentes de JetBrains) | Pantalla central → "Describe the task for the agent" → Claude | Alternativa visual para revisar los cambios |

## 6. Confluence: sitio y espacio

1. En Chrome: atlassian.com/software/confluence/free → **Get it free** → crea el sitio `<tu-sitio>.atlassian.net` (plan Free, hasta 10 usuarios).
2. "¿Qué quieres crear primero?" → **Documentos**.
3. Plantillas sugeridas (Plan de proyecto, Notas…): desmarca todas y haz clic en **Continuar**. Si obliga a elegir una, elige **Notas** y bórrala después.
4. Espacio: nombre **Biblioteca UCE**, clave **BIB**.
5. No crees páginas a mano: las crea Claude en el paso 8.

> Este Confluence es **nuevo y exclusivo** de este proyecto. No uses el sitio de otros proyectos.

## 7. Conectar Claude Code con Confluence (MCP)

Desde la terminal de IntelliJ, en la raíz del repo:

```bat
git pull origin main
claude mcp add --transport http atlassian https://mcp.atlassian.com/v1/mcp
claude
```

Dentro de Claude:

1. `/mcp` → **atlassian** → **Authenticate**.
2. En Chrome, inicia sesión con la cuenta del sitio **nuevo** y autoriza `<tu-sitio>.atlassian.net`. Si te ofrece varios sitios, elige solo este.
3. Prueba:

```text
¿Qué espacios hay en mi Confluence <tu-sitio>.atlassian.net?
```

Debe aparecer **Biblioteca UCE**.

**Comandos útiles:**

| Comando | Para qué |
| --- | --- |
| `claude mcp list` | Ver los conectores configurados y su estado |
| `claude mcp remove atlassian` | Quitar el conector (para repetir la conexión desde cero) |
| `/mcp` (dentro de Claude) | Autenticar, reconectar o ver las herramientas del conector |

**Si la URL falla:** Atlassian la cambia de vez en cuando. Busca "Atlassian Remote MCP Server" en su documentación y reemplaza solo la URL del comando.

## 8. Crear las páginas de DDD con Claude

El contenido está en `docs/confluence-demo-biblioteca.md`. En Claude, con el **modo plan** activo:

```text
Lee docs/confluence-demo-biblioteca.md. En el espacio "Biblioteca UCE" (clave BIB)
del sitio <tu-sitio>.atlassian.net, crea una página padre "Demo Biblioteca"
y, dentro, una página hija por cada sección "##" del archivo, con el mismo
título y su contenido. Muéstrame el plan antes de crear nada.
```

Revisa el plan, apruébalo y comprueba en Confluence que existen las páginas 5 a 10 y la de referencia de Figma.

## 9. Figma (pendiente)

- Cuenta con el plan Education (figma.com/education).
- Wireframe de la pantalla **Prestar ejemplar**, con los campos que figuran al final de `docs/confluence-demo-biblioteca.md`.
- Conector MCP de Figma: por configurar y probar. Si no funciona, usa capturas de la pantalla (plan B).

---

## Guion para mostrar la conexión en clase (unos 3 min)

Hazlo **en vivo solo si el conector ya funcionó el día anterior**. Si no, muestra capturas.

1. Terminal de IntelliJ (Alt+F12): `claude mcp list` → "Así sabe Claude a qué fuentes externas puede acceder".
2. Explica el comando que lo agregó: `claude mcp add --transport http atlassian <url>`. Una línea basta para conectar una fuente de contexto.
3. `claude` → `/mcp` → muestra **atlassian** conectado y su lista de herramientas.
4. Prueba en vivo: "lista las páginas hijas de Demo Biblioteca".
5. Mensaje: "Claude no adivina el lenguaje ubicuo: lo lee de nuestra documentación".

**Para repetir la conexión completa frente al grupo:** `claude mcp remove atlassian`, luego los pasos 7.1 a 7.3. El inicio de sesión en Chrome será rápido si ya tienes la sesión de Atlassian abierta.

## Problemas encontrados y su solución

| Síntoma | Causa | Solución |
| --- | --- | --- |
| El inicio de sesión abre Edge con otra cuenta | Edge era el navegador predeterminado | Copiar la URL a Chrome; poner Chrome como predeterminado |
| IntelliJ sigue en "Trial" | La licencia no está vinculada | Activate subscription → JetBrains Account, o código de activación |
| `git: 'clonehttps://…' is not a git command` | Falta un espacio | `git clone https://…` |
| `'irm' is not recognized` | Comando de PowerShell en CMD | Usar el instalador `.cmd` o abrir PowerShell |
| `'claude' is not recognized` | `.local\bin` no está en el PATH | Agregarlo al PATH y abrir una terminal nueva |
| `claude` no se reconoce **en la terminal de IntelliJ**, pero sí en CMD | IntelliJ estaba abierto cuando se cambió el PATH | Cerrar IntelliJ por completo (File → Exit) y abrirlo de nuevo; o, en PowerShell: `$env:Path = [Environment]::GetEnvironmentVariable('Path','Machine') + ';' + [Environment]::GetEnvironmentVariable('Path','User')` |
| IntelliJ propone el JDK 27 | Es la versión más nueva, pero no es LTS | Elegir Temurin 25 |
| El Confluence existente era de otro proyecto | — | Sitio y espacio nuevos y exclusivos |
