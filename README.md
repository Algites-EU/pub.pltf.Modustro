[[UNCONFIRMED]]

# Modustro Platform

Modustro Platform is the foundational layer of the Modustro ecosystem.  
It contains the core DSL, modeling infrastructure, runtime engine, generators, 
and all architectural modules that define Modustro as a model‑driven engineering platform.

---

## 1. Purpose

This repository delivers:
- The **Modustro DSL** (JetBrains MPS)
- Modeling languages (ER, Domain, API)
- Structural editors & diagram projections
- The Modustro **runtime engine**
- Model analyzers & validators
- All built‑in code generators (SQL, API, application scaffolding)
- Internal shared libraries for platform needs

The platform is **technology‑centric**, not user‑centric.  
Products—such as Modustro Studio—consume this platform.

---

## 2. Repository Structure (proposed)

```
platform.Modustro/
 ├── languages/             # MPS language definitions (DSL)
 │    ├── base/
 │    ├── er/
 │    ├── domain/
 │    ├── api/
 │    └── diagrams/
 │
 ├── runtime/               # Runtime components for model execution
 │    ├── loader/
 │    ├── validator/
 │    └── engine/
 │
 ├── generators/            # Built-in code generators
 │    ├── sql/
 │    ├── api/
 │    └── app/
 │
 ├── plugins/               # Optional MPS plugins
 ├── docs/
 ├── examples/
 └── build/
```

---

## 3. Build & Development

The platform uses **JetBrains MPS**.

### Requirements
- JetBrains MPS (latest recommended version)
- JDK 17+  
- Gradle / MPS build scripts (included)

### Build commands
(To be defined once build scripts exist.)

---

## 4. Conventions

### Naming
- All modules use prefix `modustro`
- Language namespaces: `com.modustro.platform.*`
- Generators: `modustro.generator.*`

### Versioning
- Platform follows semantic versioning: **MAJOR.MINOR.PATCH**
- Products may use independent versioning

---

## 5. Usage

This repo is **not** a product.  
It provides the engine and languages used by applications such as:

- `product.ModustroStudio`
- future cloud services
- CLI tools
- external integrations

---

## 6. Roadmap

for Roadmap see the document specs/
### Phase 1
- ER modeling DSL
- SQL generators
- MPS project structure
- Minimal runtime

### Phase 2
- Domain modeling
- API definitions
- Diagram editors

### Phase 3
- Application layer DSL
- App generators

### Phase 4
- Extensibility: plugin API
- Integration with Cloud & Studio

---

## 7. License
(To be defined.)

---

## 8. Contact
Author: Artur Linhart  
Project: Modustro — Model‑Driven Application Architecture  

[[/UNCONFIRMED]]
