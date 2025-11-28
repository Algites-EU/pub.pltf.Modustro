[[CONFIRMED]]

# Modustro Roadmap v1.0–3.0  
*Strategic Product & Technology Roadmap (2026–2029)*
*Includes AI Integration Plan*

---

# 1. Vision Statement

Modustro is a **model-driven engineering platform** built on JetBrains MPS, designed to provide:

- deterministic structural models  
- support for database and interchange entity modelling
- rich domain validation  
- multi-target code generation  
- plugin-based extensibility  
- future integration with AI-assisted modeling  

Its long-term vision is to serve as the **structural backbone** of system design in an AI-driven engineering world — where AI generates text, and Modustro ensures correctness, validation, and deterministic outputs.

## 1.1 Basic terms

- **AbstractSchemaDSL Core (abstract metamodel)** (DD)  
  The foundational DSL describing generic entities, attributes, constraints and relations shared by both downstream DSLs.
- **DatabaseSchemaDSL Metamodel**  (DDD)
  Implementation of the persistence DSL used for database modelling (tables, columns, PK/FK/UK, constraints, SQL data types).
- **InterchangeSchemaDSL Metamodel**  (IDD)
  Implementation of the interchange DSL used for modelling transferable/document based/non-database data structures (XSD, JSON Schema, DTO, API payloads, Avro/Proto, messaging formats).

---

# 2. Product Pillars

1. **Modeling Core**  
   DSLs, type system, relationships, constraints, validation.

2. **Generators**  
   SQL, Java/TypeScript, REST schemas, Docs, and extensible templates.

3. **Diagramming**  
   Visual model representation, layout, editable diagrams.

4. **Studio IDE**  
   Branded standalone application: Modustro Studio.

5. **AI Integration Layer**  
   AI-assisted modeling, generator creation, and model interpretation.

6. **Reference Projects**  
   - Open Source: *Algites Media Library*  
   - Closed Source: *Free Carry Trading Engine*

---

# 3. Roadmap Overview (2026–2029)

## Release Waves  
- **Wave 0: Modustro Platform Foundations (2026 Q1-Q2)**
- **Wave 1: Modustro 1.0 (2026 Q3-Q4)**
- **Wave 2: Modustro 2.0 (2027)**
- **Wave 3: Modustro 3.0 (2027–2028)**  

Each wave builds on the previous, with increasing modeling power, extensibility, usability.

---

# 4. Wave 0 — Modustro Platform Foundations (2026 Q1-Q3)

## 4.1 Core Goals
- Establish repository structure
- Implement base DSL for Entity modeling  
- Implement type system basics
- Implement model validation core
- Implement minimal diagram representation
- Implement basic DSL for Database and Interchange Entity modeling
- Create first generators (PostgreSQL, XSD) 
- Domain Support
  - Domain usage in AbstractSchemaDSL fields
  - SCM integration: domain-level compatibility checking
  - Domain summary preview
  - Domain-based first mapping in SQL & XSD
  - Domain substitution system (fallback)

## 4.2 Deliverables
- **Modustro DomainModelDSL v0.1**  
  The foundational DSL describing domains of the data types.
  - Domain type abstraction
  - Mapping rules defined per profile
  - Basic constraints (length, precision, scale, pattern, nullable)
  - Integrated with AbstractSchemaDSL & InterchangeDSL
  - Editor for domain creation
  - Reusable domain definitions across projects
- **Modustro AbstractSchemaDSL v0.1**  
  The foundational DSL describing generic entities, fields, (?enums?,) constraints and relations shared by both downstream DSLs.
- **Modustro Semantic Compatibility Matrix Support v0.1**  
  The foundational DSL describing semantic compatibility and allowing the replacement of the unsupported features on various levels of DSL inheritance.
  - SCM defines capability tables per target (DB, XSD, JSON Schema)
  - Enables strong abstract DSL while supporting weaker targets
  - Strategies: ignore, emulate, downgrade, transform
  - Validation severity: Error, Warning, Info
  - Ensures deterministic, safe generator output
  - Drives UI warnings and profile compatibility checks
  - Foundation for adding new profiles without DSL changes
- **AbstractSchemaDSL Validator Core v0.1**  
  Detection of duplicate names, invalid types
- **Modustro DatabaseSchemaDSL v0.1**  
  Implementation of the persistence DSL used for database modelling (tables, columns, PK/FK/UK, constraints, SQL data types, indexes, sequences).
- **DatabaseSchemaDSL Validator Core v0.1**  
  Detection of missing PKs, invalid types
- **Modustro InterchangeSchemaDSL v0.1**  
  Implementation of the interchange DSL used for modelling transferable/external data structures (XSD, JSON Schema, DTO, API payloads, Avro/Proto, messaging formats
  using structural elements like root element, namespaces, serialization rules, wrapped types, field -> element with attributes etc.)
- **InterchangeSchemaDSL Validator Core v0.1**  
  Detection of:
    - Invalid root type
    - Non-serializable constructs
    - Duplicate element names
    - Unsupported combinations (attribute + complex type)
    - Unbound namespaces
- **Modustro PgDatabaseSchemaDSL v0.1**  
  PostgreSQL specific data
- **Modustro XsdInterchangeSchemaDSL v0.1**  
  XSD specific data
- **Java ORM Generator for DatabaseSchemaDSL v0.1**  
  Entity objects including JPA/Hibernate Annotations
- **SQL Generator for PgDatabaseSchemaDSL v0.1**  
  PostgreSQL CREATE TABLE + FKs  
- **XSD Generator for XsdInterchangeSchemaDSL v0.1**  
  XSD Files - types with elements and attributes, namespaces, etc.
- **Modustro Studio v0.1**  
  First branded binaries  
- **Reference Project: Media Library v0.1**

## 4.3 Success Metrics
- DSL can define 10–20 entities  
- Java ORM generator produces consistent Entity objects
- SQL generator produces consistent DB schema DDL script  
- XSD generator produces consistent XSD schema
- Minimal diagrams render nodes and edges  
- ? First public GitHub release ? - possibly only just first functional release locally  

## 4.4. Remarks
- Note on Persistence Strategy (Wave 0 vs Wave 1)
  Wave 0 intentionally relies on native JetBrains MPS persistence formats (.mps, .mpl, .msd, and model folders).
  This allows the platform to focus on DSL design, editor behaviour, and core architecture without the overhead of designing a stable external project format.
  Native MPS persistence provides:
  - full AST-based storage with correct node identities
  - compatibility with the MPS merge engine
  - zero implementation cost
  - predictable behaviour during early DSL evolution
  A custom Modustro Project Format (MPF) will be introduced in Wave 1, once:
  - DSL concepts stabilise
  - multiple profiles exist
  - persistence needs to support versioning and migration
  - projects must be portable outside MPS
  - partial loading and partial validation become necessary
  This guarantees a clean separation:
  - Wave 0 = fast prototyping with native MPS storage
  - Wave 1 = stable, versioned, extensible Modustro project format

---

[[/CONFIRMED]]

[[UNCONFIRMED]]
# 5. Wave 1 — Modustro 1.0 (2026 Q4 - 2027 Q1)

## 5.1 Core Goals
- Deliver a complete, usable modeling environment  
- Provide stable generators  
- Provide stable Studio application  
- Improvement of the licence control

## 5.2 Deliverables

### 5.2.1 DomainModelDSL 1.0 Features
- Profile-specific domain mappings (Oracle, Postgres, MySQL…)
- Domain inspectors / validation UI
- Domain-level editing & documentation
- Domain libraries (import/export from files)

### 5.2.2 DatabaseSchemaDSL 1.0 Features  
- Inheritance  
- Type constraints  
- Composite keys  
- Index support  
- Soft deletes  
- Computed fields  

### 5.2.3 DatabaseSchemaDSL Validator 1.0  
- Relationship integrity checks  
- Circular dependency detection  
- Type mismatch detection  
- Normalization rules  
- Migration diff preview (read-only at this stage)

### 5.2.4 DatabaseSchemaDSL Generators 1.0  
- PostgreSQL  
- MySQL  
- SQLite  
- JSON Schema (MVP)  
- TypeScript interfaces (MVP)

### 5.2.5 InterchangeSchemaDSL 1.0 Features
- Core IDD Domain Model type system (primitive, list, struct)
- Namespaces
- Documentation fields
- Cardinality & optionality
- Ordering semantics
- Field metadata
- No inheritance/polymorphism

### 5.2.6 InterchangeSchemaDSL Validator 1.0
- Unique type names
- Type existence checks
- Cardinality validation
- Namespace validation
- Constraint validation
- Cycle detection

### 5.2.7 InterchangeSchemaDSL Generators 1.0
- XSD preview generator
- JSON Schema preview generator
- Model summary generator
- Basic diagram preview

### 5.2.8 Diagram Editor 1.0  
- Drag & drop  
- Auto-layout  
- Relationship labels  
- Field-level visibility options  

### 5.2.9 Modustro Studio 1.0  
- Project Persistence v1 (Basic XML/JSON Save/Load)
  - Introduction of MMF v1 (XML/JSON-based project structure)
  - Basic loader and saver (manual File → Load/Save)
  - No migrations, no container format yet
  - Exploded-project mode only
  - Minimal validation of structural consistency
  - Error handling for corrupted XML/JSON
- Theming  
- Project templates  
- DSL + Diagram sync  
- Command palette  
- Extension installation mechanism  
- Licence duplicity in local network validation

### 5.2.10 Reference Project Upgrades  
- Media Library v1.0  
- Basic documentation + tutorial

## 5.3 Success Metrics  
- Fully usable ER modeling tool  
- 100% deterministic SQL generation  
- Community adoption via GitHub  
- Tutorial and YouTube walkthrough  

---

# 6. Wave 2 — Modustro 2.0 (2027 Q2–2027 Q4)

## 6.1 Core Goals
- Expand modeling capabilities (beyond Wave 1)  
- Release full AI integration layer  
- Begin preparing runtime integration (future OmniNeto)

---

## 6.2 Deliverables

### 6.2.1 Modeling Extensions  
- Aggregates  
- Value objects  
- Lifecycles  
- Domain events  
- Entity references with navigation policies  
- Advanced constraints (XOR, implication, forbidden relations)

### 6.2.2 Generator Extensions  
- Java Entities (JPA Lite)  
- NestJS or Spring schema generation  
- GraphQL schema generation  
- OpenAPI / REST contract generator  
- Migration script generator (automatic)

### 6.2.3 Diagram Editor 2.0  
- UML-like modes  
- Layers (Data, Domain, Application)  
- Style themes  
- Export as SVG/PNG/PDF  

### 6.2.4 Project Persistence v2 (Container Format & Migration Engine)

- Introduction of `.modproj` project container (ZIP-based)
- MMF v2 with schemaVersion field
- Metadata file `metadata.json`
- Project preview artifacts included in container
- Automatic migrations (v1 → v2)
- Migration engine framework for future versions
- Recovery & rollback mechanisms
- Enhanced validation (schema integrity, referential rules)
- UI support: Open/Save As Container, Export/Import

---

## 6.3 **AI Integration Layer 1.0**

### 6.3.1 AI-assisted DSL authoring  
- Natural language → Modustro DSL  
- “Explain this model”  
- “Improve this context model”  
- “Generate missing relationships”

### 6.3.2 AI-assisted validation  
- AI identifies anti-patterns  
- AI suggests missing constraints  
- AI normalizes inconsistent naming  

### 6.3.3 AI-assisted generator creation  
- AI proposes generator templates  
- Auto-creation of boilerplate  

### 6.3.4 AI-driven diagram layout  
- Natural language → diagram styling & grouping  

---

## 6.4 Reference Projects  
- Media Library v2.0 (full domain modeling)  
- Free Carry Trading Engine v1.0 (closed-source internal)

## 6.5 Success Metrics  
- AI-assisted modeling speeds up workflows by 50–70%  
- Extended modeling supports full application stacks  
- Migration generator used in real projects  

---

# 7. Wave 3 — Modustro 3.0 (2028–2029)

## 7.1 Core Goals
- Expand into multi-domain modeling  
- Introduce runtime integration APIs  
- Prepare for OmniNeto data synchronization  
- Deliver full DSD/ISD modeling suite  

---

## 7.2 Deliverables

### 7.2.1 Modeling  
- Full application architecture DSL  
- Service modeling  
- UI flow modeling  
- State machines  
- End-to-end architecture graphs  

### 7.2.2 Generators  
- End-to-end app skeletons  
- Integrations with cloud environments  
- Runtime verification constraints  

### 7.2.3 Studio 3.0  
- Plugin marketplace  
- Remote modeling collaboration  
- Embedded AI CoPilot  

---

## 7.3 AI Integration Layer 2.0

### 7.3.1 Conversational Modeling  
- “Design an order management domain with full lifecycle rules.”  
- “Refactor this model to DSD-compliant aggregates.”  
- “Generate a modular architecture for this business description.”  

### 7.3.2 LLM Grounding in Modustro Models  
AI does not guess — AI works *inside* Modustro’s formal structures.

### 7.3.3 AI-Verified Constraints  
AI proposes new invariants but Modustro validates them.

---

## 7.4 Integration with OmniNeto (optional milestone)
- Runtime model → sync model  
- Data spaces generated from domain models  
- Schema evolution synchronized across distributed nodes  

---

# 8. Long-Term Vision (Beyond 2029)

1. Modustro becomes the **central modeling engine** of the Algites ecosystem  
2. AI-driven modeling becomes interaction-first, but structure remains Modustro’s domain  
3. OmniNeto becomes the runtime mirror of Modustro’s design-time truth  
4. Modustro Studio becomes a full **Model-Driven Application Builder**  

---

# 9. Summary

**Modustro rises exactly where AI falls short:**  
- deterministic modeling  
- validation  
- schema evolution  
- domain correctness  
- code generation  
- architecture consistency  

AI accelerates modeling; Modustro stabilizes it.  
Together they create the engineering workflow of the next decade.

---

# 10. Download  
This document describes the complete Modustro roadmap including AI integration and modeling expansion.

[[/UNCONFIRMED]]