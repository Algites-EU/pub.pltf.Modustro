[[UNCONFIRMED]]

# Modustro Platform

Modustro Platform is the foundational layer of the Modustro ecosystem.  
It contains the core DSL, modeling infrastructure, runtime engine, generators, 
and all architectural modules that define Modustro as a model‑driven engineering platform.

---

## 1. Purpose

This repository delivers:
- The **Modustro platform artifacts** (Languages and Solutions based on JetBrains MPS)
- Modeling languages (Database Schema, Interchange Schema, ...)
- Structural editors & diagram projections
- The Modustro **runtime engine API**
- Model analyzers & validators
- All built‑in common code generators (SQL, API, application scaffolding)
- Reference implementations of the platform like Modustro Prometheus
- Custom product public APIs of Products, based on Modustro Platform

The platform is **technology‑centric**, not user‑centric. 
Products—such as Algites Modustro Studio consume this platform.

---

## 2. Repository Structure (proposed)

```
platform.Modustro/
  ├── platform/             # Platform specific files (DSL)
  │       ├── languages/             # MPS language definitions (DSL)
  │       │    ├── base/
  │       │    ├── er/
  │       │    ├── domain/
  │       │    ├── api/
  │       │    └── diagrams/
  │       │
  │       ├── runtime/               # Runtime components for model execution
  │       │    ├── loader/
  │       │    ├── validator/
  │       │    └── engine/
  │       │
  │       ├── generators/            # Built-in code generators
  │       │    ├── sql/
  │       │    ├── api/
  │       │    └── app/
  │       │
  │       ├── plugins/               # Optional MPS plugins
  │       ├── docs/
  │       ├── examples/
  │       └── build/
  │
  └── product/             # Public product resources, available to developers for development for specific vendor products
          └── {vendor}             # specific resources of the given vendor, like Algites (should start with capital letter)
                  └── {product family name}   # specific code of the product family, like Modustro (should start with capital letter)
                            └── {product name}   # specific code of the product, like Studio or Prometheus (should start with capital letter)
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
- Language namespaces - Example: `eu.modustro.plat.*`
- Generators - Example: `modustro.gen.*`

### Versioning
- Platform follows semantic versioning: **MAJOR.MINOR.PATCH**
- Products may use independent versioning

---

## 5. Usage

This repo is **not** a product except the reference implementation.  
It provides the engine and languages used by applications such as:

- `product.Modustro Studio`
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
