# Modustro Platform Specification

## 0. Introduction

The Modustro Platform Specification defines the stable, public modeling surface of the Modustro ecosystem.  
It describes the **CoreDSL** and **Design DSLs** (Type, Schema, Profile, etc.), their relationships and namespaces, and the rules that every Modustro-compatible product must follow when creating, transforming, or analyzing models.

This document **does not** prescribe any particular runtime implementation, persistence format, UI, or IDE integration. Those aspects belong to product‑specific documentation and the Product Internal Specification.

The goals of this document are:

- To provide a precise and stable description of the domain modeling languages.
- To enable independent tools and products to operate on Modustro models.
- To separate domain concepts (design) from implementation details (runtime, storage).

### 0.1 Purpose of the Platform Specification

The Platform Specification defines:

- The **set of DSLs** that form the logical modeling layer (Core + Design).
- The **semantic contracts** of these DSLs (what a model means).
- The **namespaces** and naming rules for XML/XSD representations of models.
- The **invariants** that must hold in any valid Modustro model.

It intentionally does **not** define how a Modustro product:

- Stores models on disk in a conflict‑free way.
- Implements history, undo/redo, or collaborative editing.
- Integrates with version control systems.

### 0.2 What the Platform Defines (and what not)

The platform defines:

- Conceptual types and their relationships (entities, fields, types, profiles…).
- Abstract structure of model elements and references between them.
- Minimal identity: every element carries a globally unique `guid`.

The platform does **not** define:

- Hybrid logical clocks (HLC), ordering metadata, relocation markers.
- Audit trails, authorship, sessions, or system timestamps.
- Merge strategies or conflict resolution logic.

### 0.3 Target Audience

This document is intended for:

- Designers of Modustro‑compatible tools and generators.
- Library and plugin authors who want to build on the platform DSLs.
- Architects who need to understand how Modustro models are structured.

### 0.4 Terminology Overview

- **DSL** – domain specific language, a typed modeling language inside Modustro.
- **Node / Element** – an instance of some DSL concept (e.g. EntityDef, TypeDef).
- **guid** – a globally unique identifier for an element, stable across time.
- **Def / Ref** – *Def* denotes a defining element (e.g. EntityDef); *Ref* denotes a reference element pointing to a Def.
- **CoreDSL** – common super‑layer for all design DSLs; provides identity and generic abstraction.
- **DesignDSL** – user‑facing modeling DSLs: TDD, ASD, DSD, ISD, APD, etc.

---

## 1. Platform Architecture Overview

Modustro models are built on a layered approach:

- **CoreDSL (core.\*)**: generic abstractions such as ObjectDef, ObjectRef, common identity.
- **Design DSLs (dsgn.\*)**: concrete domain languages for types, schemas, and profiles.

Product‑specific runtime and storage DSLs are **not** part of this document.

### 1.1 Three DSL Layers (conceptual view)

Although this document only standardizes **CoreDSL** and **DesignDSL**, it is useful to keep the broader picture:

1. **CoreDSL (core.\*)** – abstract identities, object definitions and references; shared semantics.
2. **DesignDSL (dsgn.\*)** – domain modeling: types, schema entities, DB entities, IC entities, profiles.
3. **Runtime/PDS (run.\*, pds.\*)** – implementation‑specific layers, covered by the Product Internal Specification.

The platform defines the first two layers; the third is free for product implementations as long as they respect the semantic contracts of Core + Design.

### 1.2 Relation of Platform DSLs to Modustro Products

Modustro products (like modeling IDEs, code generators, CI integrations) must:

- Use **CoreDSL** concepts as the base for all higher‑level DSLs.
- Implement the **DesignDSL** semantics faithfully.
- Preserve `guid`s and structural relationships when performing refactorings or migrations.

Products are free to choose:

- Their own persistence model, as long as it can be mapped back to valid Core+Design models.
- Their own runtime metadata model, including history, ordering and audit.

### 1.3 Goals: Stability, Versioning, Interoperability

The platform is designed to:

- Evolve in a controlled manner with explicit **DSL versioning**.
- Support multiple products working on the same logical model.
- Allow future extensions via backward‑compatible additions.

### 1.4 Compatibility Rules Across DSL Versions

Each DSL will have:

- A **major version** indicating breaking changes.
- A **minor/micro version** for additive or strictly backward‑compatible changes.

Products must:

- Declare which platform versions they support.
- Provide migration paths when upgrading models to newer versions.

---

## 2. CoreDSL Specification (core.*)

### 2.1 Purpose of CoreDSL

CoreDSL provides:

- A **uniform identity model** (`guid`).
- A generic concept of **Def** and **Ref** objects.
- The basis for generic tooling (navigation, search, refactoring) independent of the specific DesignDSL.

It is deliberately minimal; most domain semantics live in the DesignDSLs.

### 2.2 Common Concepts

All concrete DSL elements that are part of platform models must satisfy:

- They have a globally unique `guid` (string, usually a UUID).
- They can be visited and introspected via reflection‑like APIs.

There are two principal categories:

- **ObjectDef** – defining element; owns properties that introduce or declare something.
- **ObjectRef** – referencing element; points to some ObjectDef via `defGuid` or an equivalent mechanism.

### 2.3 Core Object Types

#### 2.3.1 ObjectDef

A conceptual meta‑type representing any defining element (EntityDef, TypeDef, ProfileDef, etc.).

Common properties:

- `guid`: string – globally unique identifier.
- `name`: string (optional) – human‑readable name.

Rules:

- Each `guid` is unique within a project.
- A `guid` never changes for the lifetime of the element.

#### 2.3.2 ObjectRef

A conceptual meta‑type representing a reference to some ObjectDef.

Common properties:

- `guid`: string – identity of the reference itself.
- `defGuid`: string – `guid` of the target ObjectDef.

Rules:

- `defGuid` must point to an existing ObjectDef (referential integrity).
- Multiple ObjectRefs can point to the same ObjectDef.

#### 2.3.3 ObjectDefList

Represents an ordered collection of ObjectDef elements.

The precise ordering semantics (e.g. deterministic storage order) are defined in product‑side documents. The platform only requires that a list is a sequence and that order must be stable enough for tooling.

#### 2.3.4 ObjectRefList

Represents an ordered collection of ObjectRef elements.

Same remarks as for ObjectDefList apply.

### 2.4 Rules of Inheritance Between CoreDef and Specialized DSLs

Each DesignDSL introduces its own concrete Def and Ref types (EntityDef, DBEntityDef, ICEntityDef, etc.) by extending the CoreDSL concepts.

Key rules:

- Every concrete `*Def` type inherits the semantics of ObjectDef.
- Every concrete `*Ref` type inherits the semantics of ObjectRef.
- A concrete Ref must always be tied to a specific Def type (e.g. EntityRef → EntityDef).

### 2.5 Validation Model (Referential Integrity, Def/Ref Matching)

The platform requires:

- All `defGuid` references must resolve to an existing Def of the correct kind.
- Additional constraints (e.g. no cycles, uniqueness of names) are defined in each DesignDSL section.

---

## 3. DesignDSL Specification (dsgn.*)

### 3.1 Purpose of DesignDSL

DesignDSLs are the **user‑facing languages** of Modustro. They define:

- Domain types (TDD).
- Abstract schema entities and fields (ASD).
- Database‑specific schema concepts (DSD).
- Interchange (API/IC) schema concepts (ISD).
- Abstract and concrete profiles (APD, and profile DSLs).

DesignDSLs are composable: the same abstract EntityDef can be used in both DSD (for DB) and ISD (for API), so that a project can maintain a single source of truth.

### 3.2 General Principles

- All DesignDSL elements are ultimately based on CoreDSL.
- Identity (`guid`) is stable and shared across layers (ASD → DSD/ISD mappings).
- Definitions and references are clearly separated (`*Def` vs `*Ref`).
- Profiling and platform‑specific extensions are attached via Profile DSLs, not by mutating base definitions.

### 3.3 Abstract Schema DSL (dsgn.asd)

The Abstract Schema DSL defines high‑level domain entities and relations, independent of persistence or wire format.

Core concepts:

- `EntityDef` – abstract business entity (Customer, Product…).
- `FieldDef` – logical attribute of an entity (Customer.email, Product.price).
- Relations between entities (associations, compositions, etc.).

Key properties:

- `guid`: identity of each EntityDef/FieldDef.
- `name`: entity or field name.
- Optional metadata (documentation, tags, domain constraints).

ASD is the **single logical source** from which specialized schema DSLs (DSD, ISD) derive.

### 3.4 Type Domain DSL (dsgn.tdd)

TDD defines reusable domain types and their constraints:

- `TypeDef` – represents a logical type (Email, Price, UUID, Currency).
- Can be based on primitive types (string, integer) with additional constraints (regex, ranges, length).
- Used by ASD FieldDef definitions to specify field types.

Properties:

- `guid`
- `name`
- `baseType` (e.g. string, int)
- Optional validation rules and metadata.

### 3.5 Database Schema DSL (dsgn.dsd)

DSD specializes ASD entities for relational databases.

Typical elements:

- `DBEntityDef` – maps to a database table.
- `DBFieldDef` – maps to a database column.
- `DBIndexDef`, `DBKeyDef` – indices and keys.

Each `DBEntityDef` has a link to its ancestor `EntityDef` (from ASD), allowing generators to:

- Keep logical and physical schema in sync.
- Support multi‑profile usage (same EntityDef mapped to multiple DB platforms).

### 3.6 Interchange Schema DSL (dsgn.isd)

ISD defines objects used in external communication (XSD, JSON, OpenAPI…).

Elements:

- `ICEntityDef` – interchange representation (e.g. DTO).
- `ICFieldDef` – fields of the interchange object.
- Mappings to abstract EntityDef or specific views.

This enables:

- Generating XSD/JSON schemas from the same logical model.
- Generating mapping code (e.g. `loadFromCO` / `saveToCO` style methods if desired by a product).

### 3.7 Extensibility Model

DesignDSLs are intended to be extensible:

- New profile DSLs may extend APD and attach extra concerns (e.g. Java ORM, caching, REST annotations).
- New schema DSLs may be added on top of ASD if required by future platforms.

Any extension must preserve the basic identity and reference rules defined in CoreDSL.

---

## 4. Namespace Rules

### 4.1 Namespace Prefixes for DSL Groups

The exact prefixes are recommendations but should be consistent:

- `core.*` – for CoreDSL concepts.
- `dsgn.asd`, `dsgn.tdd`, `dsgn.dsd`, `dsgn.isd`, etc. – for DesignDSLs.

In XML/XSD representations:

- Short, readable prefixes are preferred (`asd`, `tdd`, `dsd`, `isd`).
- Fully qualified URIs identify each DSL version (e.g. `https://modustro.eu/xmldefs/dsgn/AbstractSchemaDSL/v1`).

### 4.2 Naming Conventions

- DSL names use `PascalCase` for concepts (`EntityDef`, `TypeDef`).
- XML element names mirror DSL concept names.
- Attribute names are in `camelCase` (`guid`, `name`, `defGuid`).

### 4.3 Short Prefixes and Minimal Verbosity

Because models may grow large, namespace prefixes should be:

- Short (`asd`, `dsd`, `tdd`) but unambiguous.
- Consistent across tools and examples.

### 4.4 Rules for Reuse and Collisions

- Each DSL owns its namespace; collisions are avoided by design.
- Shared concepts (like `guid`) are defined once in CoreDSL and reused.

### 4.5 Examples of Correct Namespace Usage

Illustrative XML snippets will be added in later revisions; for now the rules are normative even without canonical serialization.

---

## 5. Guidelines for Tooling & Integration

### 5.1 Generators (XSD, JSON Schema)

Tools may generate:

- XSD based on TDD + ISD.
- JSON Schema or OpenAPI based on ISD.
- SQL DDL based on DSD.

The platform only defines the **semantics** of the DSLs, not specific generator implementations.

### 5.2 Plugin Architecture Guidelines

Plugins should:

- Work against CoreDSL + DesignDSL APIs, not against storage formats.
- Communicate via stable interfaces and avoid depending on product internals.

### 5.3 Accessing DSL Semantic Information

CoreDSL and DesignDSL should provide:

- Programmatic access to model types and properties.
- Navigation from defs to refs and back.
- Introspection APIs for generic tools (visualizers, validators, analyzers).

### 5.4 Stability and Forward‑Compatibility Guidelines

New features in DSLs should:

- Prefer additive changes (new optional properties).
- Avoid breaking existing model semantics.

---

## 6. Versioning Model

### 6.1 Major DSL Versioning

Each DSL has a major version; when incremented, it may introduce incompatible changes. Products must explicitly support each major version they claim to handle.

### 6.2 Deprecation Mechanisms

Deprecated properties or concepts should be:

- Marked as deprecated in DSL descriptions.
- Supported for some time with clear migration paths.

### 6.3 Migration Rules Across DSL Evolutions

Platform documents and tools may provide:

- Schema transformations.
- Automated migration scripts.
- Guidelines for manual migration.

---

## 7. Security and Safety Considerations

### 7.1 What the Platform Guarantees

The platform guarantees:

- A consistent, typed model of domain definitions.
- Clear boundaries between model semantics and implementation.

### 7.2 API Safety, Immutability Where Needed

Implementations should:

- Use immutable structures where it improves safety.
- Provide transactional updates or model snapshots to avoid inconsistent states.

### 7.3 Separation of Product and Platform Code

The platform remains neutral:

- It can be implemented in different products.
- It does not impose a particular persistence or licensing model.

### 7.4 Minimal Exposure Principles

Products should not expose runtime or PDS internals as part of the platform API. Only DesignDSL and CoreDSL APIs are considered stable contracts.

---

## 8. Appendix

### 8.1 Draft Namespace List

- `core` – CoreDSL.
- `dsgn.asd` – Abstract Schema DSL.
- `dsgn.tdd` – Type Domain DSL.
- `dsgn.dsd` – Database Schema DSL.
- `dsgn.isd` – Interchange Schema DSL.
- Further DSLs may be added in later revisions.

### 8.2 Example Minimal Project (Conceptual)

A minimal project would define:

- A small set of TypeDefs (Email, Price).
- A few EntityDefs (Customer, Product).
- DSD/ISD specializations if the project targets DB or APIs.

Details of file formats and persistence belong to the Product Internal Specification.
