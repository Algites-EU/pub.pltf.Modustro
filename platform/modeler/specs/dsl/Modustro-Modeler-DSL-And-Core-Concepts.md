# Modustro Modeler DSL Concept – Version 1.2

## 1. Introduction
Modustro introduces a comprehensive ecosystem of interconnected domain-specific languages (DSLs) designed to model data, schemas, behavior, constraints, and generator profiles in a unified, extensible way.  
Rather than creating a monolithic language, Modustro decomposes responsibilities into layered DSLs—each dedicated to a specific abstraction level.  
This modular structure enables:

- clear separation of concerns,
- easier evolution of the modeling stack,
- maintainable generators,
- extensibility for future profiles, database engines, and interchange formats,
- easier onboarding for users (learn only what they need).

Version **1.2** refines terminology and formalizes relations between the different DSL families.

---

## 2. Overview of DSL Architecture
Modustro DSLs form a **five‑layer architecture**:

1. **Type Layer** – reusable type domains (TD)
2. **Schema Layer** – abstract schema + specializations (AS, DS, IS)
3. **Behavior Layer** – validation, invariants, rules (EB, SB, BB, MB)
4. **Profile Layer** – output targets: SQL engines, XSD, JSON, etc. (AP)
5. **Meta Layer** – cross‑DSL compatibility, lowering rules, capability declarations (MB)

Every layer builds on top of the previous one, ensuring consistency across the whole modeling pipeline.

---

### 2.1 TypeDomain DSL (TD)
**Purpose:** Define reusable, named *type domains* that can be referenced from schema-level fields.

TD models include:

TD is the “type factory” of the whole Modustro ecosystem.

---

### 2.2 AbstractSchema DSL (AS)

---

### 2.3 DatabaseSchema DSL (DS)
**Purpose:** Specialize AS for relational and NoSQL database engines.  
DS brings structural elements needed for SQL and DB-specific generators:

- **Tables / Collections**
- **Columns** with concrete DB types
- **Primary and Foreign Keys**
- **Unique constraints**
- **Indices**
- **Sequences / identity strategies**
- **Column definitions using abstract DB types** (database-neutral),
  DB-specific physical type mapping is handled exclusively by profiles (AP descendants)
- @TODO: Clarify move to AS at least not null or other simple constraint - **Check constraints**

This DSL does **not** perform generation; it only defines the model from which SQL generators operate.

---

### 2.4 InterchangeSchema DSL (IS)
**Purpose:** Model structures intended for data transfer formats such as XSD, JSON Schema, Avro, OpenAPI.

IS supports:
@TODO: Clarify - **InterchangeEntity**
@TODO: Add description - **InterchangeField** (**Elements / Properties**) - ?????
@TODO: Clarify - **InterchangeType**
@TODO: Clarify - **InterchangeConstraint**
@TODO: Clarify move to AS - **Ordering rules** (XSD-specific)
@TODO: Clarify move to AS, not here - **Optionality / nullability**
@TODO: Clarify move to AS - **Nillable vs absent**
- **Choice / group constructs**
- **Message envelopes**
@TODO: Clarify move to AS at least simple constraints - **Value constraints (enum, pattern, ranges)**

IS is deliberately technology-agnostic; XSD constraints are handled by profiles.

---

### 2.5 EssentialBehavior DSL (EB)
**Purpose:** Define minimal constraint metadata that bridges TD and AS.  
EB provides:

- **Field-level invariants** – min/max/range
- **Type-level invariants** – e.g., regex for email
- **Simple relationship constraints**
- **Basic value rules** (must be positive, must not be empty)

EB forms the baseline for consistency checking across all DSLs.

---

### 2.6 SchemaBehavior DSL (SB)
**Purpose:** Provide schema‑specific advanced behavior:

- DS-specific constraints (e.g., FK must match PK type)
- IS-specific constraints (XSD nillable rules, JSON patterns)
- Complex invariants across multiple entities
- Precondition logic for generators

SB is built on AS + EB.

---

### 2.7 BusinessBehavior DSL (BB)
**Purpose:** Provide DDD-style behavior modeling (future extension):

- Aggregates & invariants
- Commands & events
- Domain policies
- Message-based patterns

BB does **not** participate in core schema generation; it is optional.

---


### 2.8 MetaBehavior DSL (MB)
**Purpose:** Centralize cross‑DSL and cross‑profile meta‑behavior that cannot be expressed inside a single schema or behavior DSL.  
MB operates **on top of** TD, AS, EB, SB, BB and the profile DSLs.

Typical responsibilities:

- **Compatibility Matrix**
  - declares which constraints and features are *fully supported*, *partially supported* (with degradation), or *unsupported* for each target profile (e.g., XSD, JSON Schema, Oracle, PostgreSQL, Protobuf…)
  - distinguishes between *compile‑time enforceable* constraints and those that require *runtime validation* in generated DTOs/services
- **Semantic Lowering Rules**
  - specifies how rich abstract concepts (e.g., `decimal(10,2)`, `pattern`, complex multiplicities) are downgraded to simpler constructs in limited targets (e.g., Protobuf, older SQL dialects)
  - decides when to introduce *helper artifacts* (e.g., join tables, wrapper elements) to preserve semantics
- **Cross‑DSL Consistency Rules**
  - ensures that IS multiplicity does not contradict AS multiplicity
  - validates that DS/IS realizations are semantically compatible with TD/EB constraints
- **Profile Capability Declarations**
  - each profile can declare which constraint categories it can handle natively
  - MB uses this to drive warnings / errors / informational messages in Modustro Studio
- **Error & Warning Policies**
  - governs how Modustro Studio reports incompatibilities to the user (error vs. warning vs. informational hint)
  - enables configuration of *strict* vs *permissive* modes per project

MB does **not** introduce new user‑visible business concepts; instead, it acts as a **meta‑layer** that interprets and validates models produced by the other DSLs against concrete technology profiles.

---
### 2.9 AbstractProfile DSL (AP)
**Purpose:** Define attachable profiles that adapt AS-derived schemas to specific output targets.

Profiles include:
- **Activation rules** (profile enabled for entity or entire model)
- **Override features** (e.g., override DB type, XSD order, JSON field name)
- **Profile context configuration** (e.g., indentation, versioning)
- **Validation logic** (profile compatibility checks)

@TODO - clarify what should be mentioned here from the old model, having following points:
Meta-language for defining behavior and structure of profiles:
- Profile metadata
- Capability matrices
- UI profile tabs
- Validation and support checks
- Mapping configuration hooks

Concrete profiles (e.g. OracleDbProfile DSL) inherit from AP.

---

### 2.10 Concrete Profile DSLs
Derived by combining AP with a Schema DSL:
- **XsdIcProfile DSL** (AP + IS)
- **JsonIcSchemaProfile DSL** (AP + IS)
- **AvroIcProfile DSL** (AP + IS)
- **PostgresDbProfile DSL** (AP + DS)
- **OracleDbProfile DSL** (AP + DS)
- **JavaOrmProfile DSL** (AP + AS)
- **TypeScriptProfile DSL** (AP + AS)

---

## 3. Relationships Between DSLs

The Modustro DSL ecosystem follows a structured and predictable dependency hierarchy.

---

### 3.1. Examples of Hierarchy vs Dependency (B)

#### 3.1.1 Hierarchy Example
- TD is the base for all types.
- EB adds generic behavioral rules (nullability, multiplicity…).
- AS builds structural entities and fields.
- SB models relationships and constraints between schema objects.
- BB adds business-level domain logic.
- AP and SDbPD specialize schemas for profiles.
- MB supervises meta-consistency.

#### 3.1.2 Dependency Example
- SB depends on AS (needs entities) and EB (needs behavior).
- BB depends on SB (needs relationships) and AS (structures).
- Profiles depend on either DS or IS.
- MB depends on everything below because it enforces compatibility.

---

#### 3.1.3 Unified Hierarchy + Dependency Diagram (C)

```mermaid
flowchart AP
    Description:
    A1 ==> |"AINHERITANCE-LIKE RELATIONSHIPS\n(conceptual design, thick arrows)"|B
    A2 --> |"TECHNICAL RELATIONSHIPS — IMPORT / USE\n(MPS import/use, thin arrows)"|C
    A3 --x |"TECHNICAL RELATIONSHIPS — EMBEDDING\n(MPS runtime dependency, x instead arrow)"|D
    A4 --> |"A4 is used and re-exported by E\n(MPS exported languages, o instead arrow)"|E
    A5 -.-x |"A5 influences F meta-behavior\n(MPS import/use without re-export, dashed arrows)"|F
```

```mermaid
flowchart LR
    subgraph Layer1[Type DSL Layer]
        TD["TypeDomain DSL\n(TDL)"]
    end

    subgraph Layer2[Schema DSL Layer]
        AS["AbstractSchema DSL\n(AS)"]
        DS["DatabaseSchema DSL\n(DS)"]
        IS["InterchangeSchema DSL\n(IS)"]
    end

    subgraph Layer3[Behavior DSL Layer]
  %%      subgraph Layer3_1[Field & Value Behavior]
            EB["EssentialBehavior DSL\n(EB)"]
    %%        end

    %%        subgraph Layer3_2[Schema Behavior]
            SB["SchemaBehavior DSL\n(SB)"]
    %%        end

    %%        subgraph Layer3_3[Business Logic]
            BB["BusinessBehavior DSL\n(BB)"]
    %%        end
    end

    subgraph Layer4[Profiles DSL Layer]
        AP["AbstractProfile DSL\n(AP)"]
        SDbPD["Example:\nSpecificDbProfile/v1"]
        SIcPD["Example:\nSpecificIcProfile/v1"]
    end

    subgraph Layer5[Meta DSL Layer]
        MB["MetaBehavior DSL\n(MB)"]
    end


%% ================================================================
    %% LOGICAL RELATIONSHIPS (conceptual design, thick arrows)
    %% ================================================================
    DS ==>|inherits structures from| AS
    IS ==>|inherits structures from| AS

    SB ==>|inherits behavior from| EB
    
    BB ==>|inherits schema behavior from| SB

    SDbPD ==>|inherits structures from| AP
    SIcPD ==>|inherits structures from| AP

    SDbPD ==>|inherits DB structures from| DS
    SIcPD ==>|inherits IC structures from| IS


    %% ================================================================
    %% TECHNICAL RELATIONSHIPS — IMPORT / USE (thin arrows)
    %% ================================================================
    EB -->|uses types from| TD
    AS -->|uses types from| TD
    DS -->|uses types from| TD
    IS -->|uses types from| TD
    
    SB ---->|uses structures from| AS
    BB -->|uses structures from| AS
    AP -->|uses business\nmodel from| BB


    %% ================================================================
    %% TECHNICAL RELATIONSHIPS — EMBEDDING (runtime dependency)
    %% These represent “child node allowed” or “runtime needed” dependencies
    %% ================================================================
    AS --x|embeds domain types| TD
    DS --x|embeds domain types| TD
    IS --x|embeds domain types| TD

    SB --x|embeds constraints| EB

    AP --x|embeds behavior| BB
    SDbPD --x|embeds DB schema| DS
    SIcPD --x|embeds IC schema| IS

    %% ================================================================
    %% TECHNICAL RELATIONSHIPS — USAGE+RE-EXPORTS (exported languages)
    %% ================================================================
    TD --o|re-exports types| AS
    EB --o|re-exports core constraints| SB
    BB --o|re-exports business logic| AP 


    %% ================================================================
    %% META BEHAVIOR — ANALYSIS & RULES (dashed arrows)
    %% ================================================================
    MB -.-x|MB analyzes\n& constrains TD| TD
    MB -.-x|MB analyzes\n& constrains EB| EB
    MB -.-x|MB analyzes\n& constrains AS| AS
    MB -.-x|MB analyzes\n& constrains DS| DS
    MB -.-x|MB analyzes\n& constrains IS| IS
    MB -.-x|MB analyzes\n& constrains SB| SB
    MB -.-x|MB analyzes\n& constrains BB| BB
    MB -.-x|MB analyzes\n& constrains AP| AP
    MB -.-x|MB analyzes\n& constrains SDbPD| SDbPD
    MB -.-x|MB analyzes\n& constrains SIcPD| SIcPD
    
```





[//]: # (```mermaid)

[//]: # (graph AP)

[//]: # (    %% Hierarchy layer blocks)

[//]: # (    subgraph Layer1[Type DSL Layer])

[//]: # (        TD["TypeDomain DSL &#40;TDL&#41;"])

[//]: # (    end)

[//]: # ()
[//]: # (    subgraph Layer2[Schema DSL Layer])

[//]: # (        AS[AbstractSchema DSL])

[//]: # (        DS[DatabaseSchema DSL])

[//]: # (        IS[InterchangeSchema DSL])

[//]: # (    end)

[//]: # ()
[//]: # (    subgraph Layer3[Behavior DSL Layer])

[//]: # (        subgraph Layer3_1[Field & Value Behavior])

[//]: # (            EB[EssentialBehavior DSL])

[//]: # (        end)

[//]: # (    )
[//]: # (        subgraph Layer3_2[Schema Behavior])

[//]: # (            SB["SchemaBehavior DSL &#40;SB&#41;"])

[//]: # (        end)

[//]: # (    )
[//]: # (        subgraph Layer3_3[Business Logic])

[//]: # (            BB["BusinessBehavior DSL &#40;BB&#41;"])

[//]: # (        end)

[//]: # (    end)

[//]: # ()
[//]: # (    subgraph Layer4[Profiles DSL Layer])

[//]: # (        AP["AbstractProfile DSL &#40;AP&#41;"])

[//]: # (        SDbPD[SpecificDbProfile DSL])

[//]: # (        SIcPD[SpecificIcProfile DSL])

[//]: # (    end)

[//]: # ()
[//]: # (    subgraph Layer5[Meta DSL Layer])

[//]: # (        MB["MetaBehavior DSL &#40;MB&#41;"])

[//]: # (    end)

[//]: # ()
[//]: # (  %% Dependency edges)

[//]: # (    %% Type + basic dependencies)

[//]: # (    EB -->|uses types from| TD)

[//]: # (    AS -->|uses types from| TD)

[//]: # (    DS -->|uses types from| TD)

[//]: # (    IS -->|uses types from| TD)

[//]: # ()
[//]: # (    %% Schema specializations)

[//]: # (    DS ==>|extends| AS)

[//]: # (    IS ==>|extends| AS)

[//]: # ()
[//]: # (    %% Schema behavior)

[//]: # (    SB -->|uses structures from| AS)

[//]: # (    SB ==>|inherits behavior from| EB)

[//]: # ()
[//]: # (    %% Business behavior)

[//]: # (    BB ==>|inherits schema behavior from| SB)

[//]: # (    BB -->|uses structures from| AS)

[//]: # ()
[//]: # (    %% Profiles)

[//]: # (    AP -->|uses business\nmodel from| BB)

[//]: # (    SDbPD ==>|inherits structures from| AP)

[//]: # (    SDbPD ==>|inherits DB structures from| DS)

[//]: # (    SIcPD ==>|inherits structures from| AP)

[//]: # (    SIcPD ==>|inherits IC structures from| IS)

[//]: # ()
[//]: # (    %% Meta behavior – reads models & defines rules that affect them)

[//]: # (    MB -.->|analyzes & constrains| TD)

[//]: # (    MB -.->|analyzes & constrains| EB)

[//]: # (    MB -.->|analyzes & constrains| AS)

[//]: # (    MB -.->|analyzes & constrains| DS)

[//]: # (    MB -.->|analyzes & constrains| IS)

[//]: # (    MB -.->|analyzes & constrains| SB)

[//]: # (    MB -.->|analyzes & constrains| BB)

[//]: # (    MB -.->|analyzes & constrains| AP)

[//]: # (    MB -.->|analyzes & constrains| SDbPD)

[//]: # (    MB -.->|analyzes & constrains| SIcPD)

[//]: # (```)

---

#### 3.1.4 Hierarchical Diagram

```mermaid
graph AP
    TD[TypeDomain DSL] --> EB[EssentialBehavior DSL]
    EB --> AS[AbstractSchema DSL]
    AS --> SB[SchemaBehavior DSL]
    SB --> BB[BusinessBehavior DSL]
    BB --> AP[AbstractProfile DSL]
    AP --> SDbPD[ConcreteProfile DSL]

    EB -. influenced by .-> MB[MetaBehavior DSL]
    SB -. influenced by .-> MB
    BB -. influenced by .-> MB
    SDbPD -. influenced by .-> MB
```
---

#### 3.1.5 Dependency Diagram

```mermaid
graph LR
    TD --> EB
    TD --> AS

    EB --> AS

    AS --> DS
    AS --> IS

    EB --> SB
    AS --> SB

    SB --> BB
    BB --> AP

    AP --> SDbPD
    DS --> SDbPD
    IS --> SDbPD

    EB --> MB
    SB --> MB
    BB --> MB
    AP --> MB
    SDbPD --> MB
```

---



### 3.1 Hierarchical Mermaid Diagram

```mermaid
graph AP
    TD[TypeDomain DSL] --> AS[AbstractSchema DSL]

    AS --> DS[DatabaseSchema DSL]
    AS --> IS[InterchangeSchema DSL]

    TD --> EB[EssentialBehavior DSL]
    AS --> EB
    EB --> SB[SchemaBehavior DSL]
    SB --> BB[BusinessBehavior DSL]

    MB[MetaBehavior DSL] --> EB
    MB --> SB
    MB --> BB
    MB --> AP[AbstractProfile DSL]

    AS --> AP
    DS --> AP
    IS --> AP
```

---

### 3.2 Dependency Mermaid Diagram

```mermaid
graph LR
    TD[TypeDomain DSL] --> AS[AbstractSchema DSL]

    AS --> DS[DatabaseSchema DSL]
    AS --> IS[InterchangeSchema DSL]

    TD --> EB[EssentialBehavior DSL]
    AS --> EB
    EB --> SB[SchemaBehavior DSL]
    SB --> BB[BusinessBehavior DSL]

    MB[MetaBehavior DSL] --> EB
    MB --> SB
    MB --> BB
    MB --> AP[AbstractProfile DSL]

    AS --> AP
    AP --> Generators
```

---

### 3.3 Generator / Profile Pipeline Diagram

```mermaid
flowchart LR
    TD --> AS

    AS -->|specialization| DS
    AS -->|specialization| IS

    DS -->|uses profile| SQLProfiles
    IS -->|uses profile| XSDProfiles

    SQLProfiles --> SQLGen[SQL Generator]
    XSDProfiles --> XSDGen[XSD Generator]
```

---

## 4. Example Syntax for Each DSL

### 4.1 TypeDomain DSL (TD) – Example

```td
domain Email {
    base String;
    pattern "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}";
}

domain Price {
    base Decimal;
    min 0;
    scale 2;
}
```

---

### 4.2 AbstractSchema DSL (AS) – Example

```as
entity Customer {
    field id       : UUID required;
    field name     : String100 required;
    field email    : Email optional;
    field address  : Address optional;
}

relation CustomerAddress {
    from Customer.address
    to   Address.id
    type one_to_one optional;
}
```

---

### 4.3 DatabaseSchema DSL (DS) – Example

```ds
table Customer as entity Customer {
    column id    uuid primary;
    column name  varchar(100) not null;
    column email varchar(255);
}

foreign_key fk_customer_address {
    table Customer;
    column address_id;
    references Address(id);
}
```

---

### 4.4 InterchangeSchema DSL (IS) – Example

```is
message CustomerMessage {
    element Customer {
        id        : Uuid;
        name      : String100;
        email     : Email nillable;
        address   : Address optional;
    }
}
```

---

### 4.5 Behavior DSL Examples (EssentialBehavior DSL / SchemaBehavior DSL / BusinessBehavior DSL)

EssentialBehavior DSL (EB)
```eb
const REGEXP_FOR_EMAIL_BASIC = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$" 
const REGEXP_FOR_EMAIL_STRICT = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
const REGEXP_FOR_EMAIL_RFC_FULL = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."[ +
                                  "a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|" +
                                  "\"(?:[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f]|" +
                                  "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@" +
                                  "(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+" +
                                  "[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|" +
                                  "\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
                                  "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|" +
                                  "[a-z0-9-]*[a-z0-9]:" +
                                  "(?:[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])" 
constraint Customer.email {
    require(Customer.email length max 100);
    require(Customer.email fulfills regexp REGEXP_FOR_EMAIL_RFC);
}
```

SchemaBehavior DSL (SB)
```sbd
schema_rule CustomerTable {
    require(Customer.id primary_key);
    require(Customer.name not_null);
}
```

BusinessBehavior DSL (BB)
```bbd
invariant Customer {
    rule "Email must be present when Address is used" {
        require(email exists OR address not exists);
    }
}
```


---

### 4.6 Profile DSL Examples (AP + Concrete Profiles)

```ap
profile XsdIcProfile for CustomerMessage {
    namespace "http://example.org/customer/v1";
    ordering  lexicographical;
    element_form qualified;
}
```

```ap
profile OracleDbProfile for CustomerTable {
    map String100 to VARCHAR2(100 CHAR);
    map UUID       to RAW(16);
}
```

---
[[CONFIRMED]]
## 5. DSL Flow: From Domain Types → Schema → Profiles → Generators

### 5.1 The Modustro modeling process

The Modustro modeling pipeline proceeds in following major stages:

1. **Specify general profile settings for profiles used on project** (PostgresDbProfile, JavaOrmDbProfile or XsdIcProfile common settings)
2. **Define Entity groups** in AS (Database schema entities, subgroups of them, Interchange Schema entities, subgroups of them, )
3. **Define Entities and their relations** in AS (Customer, Product…)
4. **Group entities** into:
    - DS group/groups → for SQL generation
    - IS group/groups → for XSD/JSON/OpenAPI generation
    - every AS can be in one, second or both groups
5. **Attach profiles to groups and or individual entities** (PostgresDbProfile, JavaOrmDbProfile to entities supporting DB, XsdProfile to entities supporting XSD…)
6. **Define types** in TD (Email, Price, UUID…)
7. **Use types in AS entity fields** (Customer.Email, Product.Price…)
8. **Complete entities** by specifying custom properties for entity profiles
9. **Run generators** to produce concrete outputs

The "Schema DSL" layer is practically used only in background by the "Profile/v1" layers, which then steers the vizualization to the user 
and control of the structure definitions, provided in background by the schemaDSL capabilities.

The global profile settings must be able to be also saved and loaded separately from the project, to allow the simple synchronization 
of the profiles across multiple projects

### 5.2 Diagram of Schema-Profile DSL Relations

```mermaid
flowchart AP
    TD["TypeDomain DSL\n(TD)"] -.-> |AS imports and re-exports TD|AS["AbstractSchema DSL (AS)"] 
    AS ==> |DS extends AS| DS["DatabaseSchema DSL\n(DS)"]
    AS ==> |IS extends AS| IS["InterchangeSchema DSL\n(IS)"]
    DS -.->|DPD imports and re-exports DS|DPD["DatabaseProfile DSL\n(DPD)"] 
    IS -.->|IPD imports and re-exports IS|IPD["InterchangeProfile DSL\n(IPD)"] 
    AP["AbstractProfile DSL\n(AP)"] -->|DPD extends AP| DPD
    TD -.-> |AP imports and re-exports TD| AP 
    AP ==>|IPD extends AP| IPD
    IPD ==>|XsdIcProfile DSL extends IPD| XsdIcPD["Example: XsdIcProfile DSL\n(is optional for any Db kind)"]
    DPD ==>|PostgresDbProfile DSL extends DPD| PgDbPD["Example:\nPostgresDbProfile DSL\n(is optional for any Db kind)"]
    IS ==>|XsdIcSchema DSL extends IS| XsdIcSD["Example:\nXsdIcSchema DSL\n(is optional for any Ic kind)"]
    DS ==>|PostgresDbSchema DSL extends DS| PgDbSD["Example:\nPostgresDbSchema DSL\n(is optional for any Db kind)"]
    XsdIcSD -.->|XsdIcProfile DSL imports\nand re-exports XsdIcSchema DSL|XsdIcPD
    PgDbSD -.->|PostgresDbProfile DSL imports\nand re-exports PostgresDbSchema DSL|PgDbPD
    XsdIcGenerator["XSD Generator\n(If general Interchange generators\nare not enough)"] -->|XsdIcProfile DSL imports\nand starts XsdIcGenerator|XsdIcPD
    PgDbGenerator["Postgres Generator\n(If general Database generators\nare not enough)"] -->|PostgresDbProfile DSL imports\nand starts PgDbGenerator|PgDbPD
```

[[/CONFIRMED]]

## 5.3 Potential Drawbacks and Mitigations

This chapter summarizes potential limitations of the Modustro profile-driven modeling approach and provides mitigation strategies.  
Although the architecture is powerful, scalable, and highly flexible, several practical risks need to be taken into account.

---

### 5.3.1 Hidden Intermediate Schema DSL Layers (Debugging Complexity)

**Problem**  
Because users work only with abstract AS entities and profiles, the intermediate Schema DSL layers (DS, IS, or profile-specific schema DSLs) remain hidden.  
This may complicate debugging when:

- a SQL column name is unexpectedly different,
- XSD generation creates unexpected structure,
- ORM annotation strategy differs from user assumptions,
- profile-driven default values override user expectations.

Users see the final outputs but not the intermediate “effective schema” transformations.

**Mitigations**

- Provide an optional **“Show Effective Schema DSL”** view to inspect generated DS/IS structures.
- Add **Profile Preview Panels**, such as:
    - *Effective SQL Entity View*
    - *Effective XSD View*
    - *Effective ORM View*
- Display inline explanations of which properties originate from which profile (e.g. “Property added by XsdIcProfile”).
- Add validation warnings for questionable auto-generated defaults.

---

### 5.3.2 Over-Automation and Unexpected Defaults

**Problem**  
Profile-driven modeling automatically fills many properties.  
If automation is too aggressive, users may struggle with:

- implicit rules they didn’t expect,
- default type mappings conflicting with existing legacy systems,
- naming conventions hidden behind profile logic,
- automatic constraints (not-null, sequence handling, maxLength…) being applied without explicit intent.

This may lead to “fighting the system” — the tool decides too much on behalf of the user.

**Mitigations**

- Every profile should support **explicit overrides**, where user-defined values take precedence.
- Introduce a dual-layer settings model:
    1. **Default profile values**
    2. **User-set explicit overrides** (always winning)
- Add “strict mode” / “lenient mode” toggles for profiles.
- Provide clear documentation of all profile defaults.

---

### 5.3.3 Conflicts When Multiple Profiles Are Applied to One Entity

**Problem**  
One abstract entity may have multiple profiles attached:

- PostgresDbProfile
- OracleDbProfile
- JavaOrmDbProfile
- XsdIcProfile

Each has its own rules for type mapping, constraints, naming, and structure.  
Conflicts may arise, for example:

- different maxLength constraints for the same field,
- incompatible enum strategies,
- different naming conventions for columns/elements,
- ORM vs XSD vs SQL semantics diverge.

Without a clear priority/override strategy, the “effective schema” may become inconsistent.

**Mitigations**

- Introduce a **profile priority system**:
    - base profiles (lowest priority),
    - tech-specific profiles,
    - explicit user overrides (highest priority).
- Add conflict detection:
    - “Warning: XSD and SQL profiles define conflicting maxLength for Email.”
- Provide conflict resolution UI with explanation of winning rules.
- Document a deterministic merge strategy for conflicts.

---

### 5.3.4 Pros and Cons of the Profile-Driven Approach

| Aspect | Pros | Cons |
|-------|------|------|
| **Unified abstract entity model** | One definition used across DB, XSD, JSON, ORM, API, etc. | Harder to debug without exposing intermediate schema layers. |
| **Automation & defaults** | Fast modeling, minimal boilerplate. | Unexpected defaults may cause confusion. |
| **Multiple profiles per entity** | Multi-platform output from one model; highly flexible. | Possible conflicts between profiles (naming, constraints, types). |
| **Hidden Schema DSL layers** | Clean UX; users stay in AS + Profiles only. | Harder introspection; needs “Effective View” modes. |
| **Plugin architecture** | Easy extension with new profiles/generators. | Higher complexity on Modustro implementation side. |
| **CO ↔ EO mapping possibilities** | Can generate mapping layers automatically. | May cause higher coupling if used unintentionally (DDD concerns). |

---

### 5.3.5 Summary

The Modustro profile-driven approach is powerful, practical, and architecturally elegant.  
Its main risks relate to transparency, debugging, and profile conflicts — all of which can be mitigated through:

- effective inspection tooling,
- override mechanisms,
- profile priority rules,
- conflict detection,
- documentation and preview features.

These mitigations ensure that Modustro remains both highly automated and fully predictable for the user.

## 5.4 Profile Reuse, Versioning and Corporate Standards

While Profiles in Modustro may be configured ad-hoc inside a project, large-scale usage in real organizations requires a structured and reusable approach.  
This chapter introduces reusable Profile artifacts, Profile Groups, versioning rules, and governance strategies that enable consistent modeling across teams, products, and entire enterprises.

---

### 5.4.1 Motivation for Reusable Profiles

In enterprise environments (banking, insurance, automotive, telco, government), organizations typically maintain their own:

- SQL naming conventions
- XML/XSD schema rules
- ORM strategies
- JSON and API naming rules
- Integration policies
- Security annotations
- Default field constraints
- Corporate data types (Email, Money, CountryCode, …)

Without reusable profile artifacts, each project would need to re-create these standards manually, leading to:

- inconsistency across teams
- duplicated work
- divergent schemas
- difficult governance
- integration friction

Reusable Profiles eliminate this fragmentation.

---

### 5.4.2 Profiles as Standalone Artifacts

Every Profile in Modustro can exist as an **independent stored unit**, not only as part of a project.  
Profiles can be saved, exported, versioned, reused, distributed across teams, or published into a shared repository.

A profile file typically contains:

- Profile name
- Profile version
- Profile type (Db, ORM, XSD, JSON, API, Mapping…)
- Custom property defaults
- Auto-generation rules
- Naming conventions
- Required Modustro version
- Optional organization metadata
- Optional digital signature

Example filenames:

```
Profile.PostgresDb.Default.v3.0.pfl.mdstr
Profile.JavaOrm.Hibernate.v2.1.pfl.mdstr
Profile.XsdIc.CorporateStandard.v1.9.pfl.mdstr
```

Profiles are thus portable, shareable, and composable.

---

### 5.4.3 Profile Groups

A **Profile Group** is a reusable collection of Profiles that work together as a unified modeling standard.

Examples:

**ProfileGroup “VW Backend Standard 2025”**
- PostgresDbProfile v3.0
- JavaOrmDbProfile v2.1
- XsdIcProfile v1.8
- MappingProfile v1.0
- JsonApiProfile v1.2

**ProfileGroup “Minimal Defaults”**
- PostgresDbProfile (default)
- JsonProfile (default)

Using a Profile Group:

- dramatically accelerates project startup
- enforces corporate consistency
- reduces configuration mistakes
- ensures schema compatibility across products
- provides predictable generator outputs

Profile Groups themselves are versioned:

```
ProfileGroup.BackendStandard.v2.0.pfg.mdstr
```

---

### 5.4.4 Workflow: Using Reusable Profiles in a Project

1. **Create new project**  
   Modustro asks:  
   *“Select a Profile Group or start with empty profiles.”*

2. **Load selected Profile Group**  
   All contained profiles become active for the project.

3. **Project entities use these profiles automatically**  
   Profiles populate:
    - naming rules
    - XSD/JSON/API conventions
    - SQL mappings
    - default property values

4. **User may add additional profiles**  
   e.g. KafkaAvroProfile, ProtobufProfile, MongoDbProfile.

5. **Optional updates**  
   Project can check for newer profile versions:
    - minor upgrades (safe)
    - major upgrades (with migration tool)

6. **Profiles guarantee deterministic generation**  
   No matter from which machine or team the project is built,
   the same profile inputs always lead to the same schema outputs.

---

### 5.4.5 Versioning Strategy

Modustro supports semantic versioning for Profiles and Profile Groups:

- **MAJOR** – breaking changes (naming conventions changed, generator logic changed)
- **MINOR** – additive changes (new settings, new defaults)
- **PATCH** – bugfixes (typo corrections, minor mapping fixes)

Projects store:

```
profile-group: VWBackendStandard v2.0
profile-group-hash: ab3f49e...
```

so they remain reproducible even after years.

---

### 5.4.6 Governance and Distribution

To support enterprise usage:

- Profiles can be published to a **central repository**, similar to Maven:
  ```
  modustro-profiles/org/acme/db/default/1.2.0/profile.pfl.mdstr
  ```
- Access control can restrict who may create/modify corporate profiles.
- Profiles may be **digitally signed**, enabling trust validation.
- Conflicting or outdated profile versions can be automatically detected.

This creates a governed, standardized modeling environment.

---

### 5.4.7 Example Diagram: Profile Library and Project Binding

```mermaid
flowchart LR
    subgraph Library["Corporate Profile Library"]
        A["PostgresDbProfile v3.0"]
        B["JavaOrmDbProfile v2.1"]
        C["XsdIcProfile v1.8"]
        D["MappingProfile v1.0"]
        E["JsonApiProfile v1.2"]
    end

    subgraph Group["ProfileGroup: BackendStandard v2.0"]
        GA --> A
        GA --> B
        GA --> C
        GA --> D
        GA --> E
    end

    Group --> Project["Modustro Project
(Entities + Types + Groups)
uses ProfileGroup"]

    Project --> Outputs["SQL / ORM / XSD / JSON / API / CO-EO Mappings"]
```

---

### 5.4.8 Potential Risks and Mitigations

**Risk:** Too many profiles → complexity for new users  
**Mitigation:** Use Profile Groups; provide curated “recommended sets”

**Risk:** Divergence of corporate standards  
**Mitigation:** Use central repository + access control + signatures

**Risk:** Version conflicts  
**Mitigation:** Semantic versioning + upgrade assistant + diff view

**Risk:** Changing profiles may break existing generators  
**Mitigation:** Use MAJOR/MINOR/PATCH semantics and migration tooling

---

### 5.4.9 Summary

Reusable Profiles and Profile Groups enable:

- consistent modeling across projects
- extremely fast project initialization
- shared corporate rules
- deterministic code and schema generation
- easy governance and long-term maintainability

This approach elevates Modustro from a modeling tool to a full-scale, enterprise-ready **modeling ecosystem**.

---

## 6. Integration With Roadmap

This DSL architecture maps directly to the roadmap:

- **Wave 0.1**
  - Core DSL layer definitions (TD + AS)
  - Minimal behavior layer (EB)
  - First generator prototype (plain SQL)

- **Wave 0.2**
  - DatabaseSchema DSL + InterchangeSchema DSL baseline
  - DatabaseProfile DSL + InterchangeProfile DSL baseline

- **Wave 0.3**
  - Simple specific profiles (Postgres, XSD base)
  - Profile groups loading from file and storing to file

- **Wave 1**
  - Extended schema behavior (SB)
  - Multi-database support
  - Rich validation
  - XSD, JSON Schema generation

- **Wave 2**
  - Profile ecosystem expansion
  - Advanced constraints
  - GUI configuration of DSL elements

- **Wave 3**
  - BusinessBehavior DSL (DDD concepts)
  - AI-assisted modeling
  - Large-scale multi-module support

---

## 7. Glossary

**DomainType**  
Reusable named type such as Email, UUID, Price.

**Entity (AS - AbstractSchema DSL)**  
A schema-level structural object.

**Profile (AP)**  
Output-specific configuration (e.g., XSD, Oracle).

**Invariant (EB/SB)**  
Rule that must always hold.

**Generator**  
A component that transforms DSL models into output artifacts (SQL, XSD, JSON Schema…).

**Schema**  
A defined structural collection of entities, fields, and relationships.

