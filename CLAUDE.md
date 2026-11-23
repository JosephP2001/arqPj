# UCE Library

Library lending system for Universidad Central del Ecuador, built as a Domain-Driven Design demo.
Spring Boot backend (`backend/library`, Gradle, Java 25) and React + Vite frontend (`frontend`).
The domain source of truth is the Confluence space **BIB**: https://josephp2001.atlassian.net/wiki/spaces/BIB

## Package structure

Base package `ec.edu.uce.library`. One package per bounded context, each with three layers:

```
ec.edu.uce.library
├── lending          ← core subdomain
│   ├── domain           aggregates, value objects, domain events, ports (repository interfaces)
│   ├── application      use cases
│   └── infrastructure   REST controllers, JPA adapters, configuration
├── catalog          ← supporting subdomain (same three layers)
└── members          ← generic subdomain (same three layers)
```

**Dependencies point inward:** `infrastructure → application → domain`.

- `domain` has no Spring, JPA or any framework import. Plain Java only.
- `application` depends only on `domain`.
- No context shares entity classes with another. A context only keeps the other contexts' identifiers (e.g. `MemberId`, `BookId`).

## Conventions

- **Names come from the ubiquitous language in Confluence.** Before naming a class, method or field, read the pages
  "5 · Lenguaje ubicuo", "6 · Reglas e invariantes", "8 · Contextos y mapa", "9 · Modelo táctico" and
  "10 · Eventos de dominio" of the BIB space (use the Atlassian MCP). Use those exact terms. Do not invent synonyms;
  if a term is missing, ask instead of making one up.
- **Business rules live in the aggregate**, not in controllers, services or the UI. The use case loads the aggregate,
  calls its method, saves it and publishes the events.
- **Every rule has its test.** Each `BR-xx` rule gets a unit test in `domain` (JUnit 5, no Spring) whose name mentions the rule code.
- Code, identifiers and commit messages in English (conventional commits).

## Commands

The backend needs JDK 25 (`JAVA_HOME` pointing to it).

Backend, from `backend/library`:

```bash
./gradlew build      # compile + test
./gradlew test       # tests only
./gradlew bootRun    # start on http://localhost:8080 (H2 in memory)
```

Frontend, from `frontend`:

```bash
npm install
npm run dev          # http://localhost:5173, proxies /api to http://localhost:8080
npm run build        # type-check + production build
npm run lint
```
