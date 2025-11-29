[[CONFIRMED]]
# Modustro Platform Ordering & Identity Standard  
### (Mandatory GUID + Lexicographic OrderKey for All SchemaDSL Elements)

## 1. Purpose and Scope

This document defines the **core platform-level standard** for ordering, identity, and deterministic serialization of all elements across the entire Modustro Platform:

- **Elements of all DSLs**
- **Generator mappings**
- **Model metadata**

The standard ensures:

- stable AST identity  
- deterministic and canonical serialization  
- consistent ordering semantics  
- safe version control system (like GIT) use  
- reduced merge conflicts  146042
- allowance of non-destructive automatic conflict resolution
- predictable and replicable generator results  
- compatibility with future AST-level merging  

All artifacts/products using Modustro platform must **implement** these mandatory rules.

---

# 2. Identity and Ordering Requirements

## 2.1 Each model element MUST have a GUID

Every element in every Modustro DSL must carry:

```
mdstr.mdl:elementGuid : GUID
```

Properties:

- generated on creation  
- never changed  
- never reused  
- used for reference resolution  
- used for deterministic XML ordering  
- serves as the **true AST identity** of the element  

This applies to:

- entity definitions  
- fields  
- relations  
- types  
- constraints  
- profiles  
- profile rules  
- schema nodes  
- mapping nodes  

### Why mandatory?

- identity must survive rename, move, reorder, refactoring  
- references must remain stable  
- schemas must remain comparable  
- Git merging must be safe  
- AST-based merging must work in all tooling  
- generators must be deterministic  

---

## 2.2 Each model element MUST have a lexicographic OrderKey

```
mdstr.mdl:elementOrder : String  (variable-length lexicographically sortable key)
```

This is **logical ordering**, not physical ordering in XML.

Used for:

- UI ordering  
- entity field sequence  
- XSD <sequence> / JSON schema property sequencing  
- database column sequence  
- profile rule precedence  
- predictable code generation  
- user-controllable ordering  

This is NOT used for:

- physical ordering in XML serialization  
- grouping or identity resolution  

---

# 3. Deterministic XML Serialization

## 3.1 Physical ordering in XML MUST be by GUID

The XML representation of any Modustro model:

- MUST sort all elements by their `mdstr.mdl:elementGuid` (GUID)
- MUST NOT sort by `mdstr.mdl:elementOrder`
- MUST NOT preserve insertion order  
- MUST NOT use name or type for ordering  
- MUST be 100% deterministic across
  - machines
  - versions
  - editors
  - build servers

### Why?

- eliminates reorder diffs  
- guarantees stable file structure  
- simplifies Git merge  
- ensures canonical model representation  
- allows safe validation  
- prevents accidental drift  

---

# 4. OrderKey Algorithm (Platform Standard)

Modustro uses a simplified form of **lexicographic dense identifier generation**, inspired by:

- LogootSplit CRDT  
- Peritext  
- Figma’s OT variants  
- JetBrains MPS ordering principles  
- multi-level lexicographic insertion systems  

## 4.1 Alphabet

```
a b c ... z
```

This alphabet yields 26 discrete positions.

## 4.2 Insert Between Algorithm

Given two keys:

```
left = "d"
right = "k"
```

Compute a midpoint lexicographically:

- `"h"`

If `"h"` already exists, refine:

- `"hf"`
- `"hff"`
- `"hfff"`
- etc.

Guaranteed infinite space.

## 4.3 Insert at Edges

When inserting before the first or after the last element, start from:

```
aaa...
or
zzz...
```

## 4.4 Reordering existing elements

Moving an element generates a *new OrderKey* using the same algorithm.

Its GUID stays the same.

## 4.5 Collision resolution

If two keys collide (parallel insert):

- Studio/platform detects the collision upon model load
- a new OrderKey is computed for one or both nodes
- GUID identity remains unchanged
- operation is nondestructive

---

# 5. SchemaDSL Extensions (Mandatory Attributes)

Every element in SchemaDSL (ASD/DSD/ISD), ProfileDSL, and TDD must include:

```
attribute mdstr.mdl:elementGuid   : GUID     (mandatory)
attribute mdstr.mdl:elementOrder: String   (mandatory, lexicographic)
```

### 5.1 Example: Field definition in SchemaDSL

```
<mdstr.dsl.asd:Field
    mdstr.mdl:elementGuid="7b42edbb-ca92-45a7-a934-eafb7737fc2f"
    mdstr.mdl:elementOrder="hmf"
    name="email"
    type="Email"
    nullable="false"
/>
```

### 5.2 Example: Entity

```
<mdstr.dsl.asd:Entity
    mdstr.mdl:elementGuid="c8330b68-eacf-4f97-8c95-2deb8358f86f"
    mdstr.mdl:elementOrder="m"
    name="Customer">

    <mdstr.dsl.asd:Fields>
        <mdstr.dsl.asd:Field ... />
        <mdstr.dsl.asd:Field ... />
    </mdstr.dsl.asd:Fields>
</mdstr.dsl.asd:Entity>
```

### 5.3 Example: Profile Rule

```
<mdstr.dsl.apd:ProfileRule
    mdstr.mdl:elementGuid="f0a2e772-c11f-4cc3-97a4-cafdc25a7b94"
    mdstr.mdl:elementOrder="mq"
    appliesTo="Entity"
    rule="AddAutoId"
/>
```

### 5.4 Example: TDD Type

```
<mdstr.dsl.tdd:Type
    mdstr.mdl:elementGuid="59fdbf05-b9c8-4f32-bb7c-88d2a83f5a13"
    mdstr.mdl:elementOrder="x"
    name="Email"
    base="string"
/>
```

---

# 6. Validation Rules (Platform Runtime)

Upon project load, ANY tool using the Modustro Platform (Studio, CLI, generators) MUST:

1. Validate XML against schema  
2. Validate **GUID uniqueness**  
3. Validate **OrderKey uniqueness inside siblings**  
4. Validate well-formed lexicographic ordering  
5. Regenerate OrderKey on conflict  
6. Sort AST nodes logically by OrderKey  
7. Persist XML sorted by GUID  
8. Fail loudly if model is irrecoverable  

The platform—not Studio—is responsible for correctness.

---

# 7. Generator Interaction

Generators will:

- read AST sorted by logical `mdstr.mdl:elementOrder`
- ignore XML node order entirely
- rely on GUID for stable linking
- detect reorder changes through OrderKey only
- ensure deterministic output for source code

For example:

- XSD `<sequence>` uses OrderKey  
- ORM code uses OrderKey for fields  
- SQL CREATE TABLE formats columns by OrderKey  
- API schema outputs preserve OrderKey ordering  

Generators MUST NOT rely on XML element ordering.

---

# 8. Justification & Industrial Precedent

## 8.1 JetBrains MPS

- node identity = GUID  
- serialization sorted deterministically  
- UI order independent of XML  

## 8.2 Eclipse EMF / XMI

- structural identity with `xmi:id`  
- deterministic persistence  
- XSD-based validation  
- ordering via semantic attributes  

## 8.3 LogootSplit CRDT

- lexicographic dense identifiers  
- infinite insertion granularity  
- extremely safe for parallel edits  

## 8.4 Figma document model

- stable node identity  
- deterministic file persistence  
- ordering independent from storage  

---

# 9. Summary (Platform Requirements)

**Every element in every Modustro DSL MUST have:**

1. **GUID** – immutable identity  
2. **OrderKey** – lexicographic logical ordering key  
3. **Deterministic XML serialization sorted by GUID**  

**Platform MUST:**

- enforce uniqueness  
- resolve conflicts  
- validate upon load  
- preserve order semantics  
- not rely on XML ordering for generators  
- provide stable file layouts for Git & CI  

**Studio is only a client of these platform guarantees.**

---

# 10. Appendix: Minimal XML Structure Template

```
<mdstr.meta.prj:Project
    xmlns="https://modustro.eu/xmldefs/metadata/project"
    xmlns:mdstr="https://modustro.eu/xmldefs/platform"
    xmlns:mdstr.mdl="https://modustro.eu/xmldefs/model"
    xmlns:mdstr.dsl.tdd="https://modustro.eu/xmldefs/dsl/TypeDomainDSL"
    xmlns:mdstr.dsl.asd="https://modustro.eu/xmldefs/dsl/AbstractSchemaDSL"
    xmlns:mdstr.dsl.isd="https://modustro.eu/xmldefs/dsl/InterchangeSchemaDSL"
    xmlns:mdstr.dsl.dsd="https://modustro.eu/xmldefs/dsl/DatabaseSchemaDSL"
    xmlns:mdstr.meta.prj="https://modustro.eu/xmldefs/metadata/project"
    xmlns:mdstr.meta.prof="https://modustro.eu/xmldefs/metadata/profile"
    xmlns:mdstr.meta.gen="https://modustro.eu/xmldefs/metadata/generator"
    mdstr.meta.prj:platformVersion="1.0"
    mdstr.meta.prj:modelVersion="1.0">
    
    <mdstr.dsl.asd:Entities>
        <mdstr.dsl.asd:Entity 
            mdstr.mdl:elementGuid="..." 
            mdstr.mdl:elementOrder="...">
            
            <mdstr.dsl.asd:Fields>
                <mdstr.dsl.asd:Field 
                    mdstr.mdl:elementGuid="..." 
                    mdstr.mdl:elementOrder="..." />
                
                <mdstr.dsl.asd:Field 
                    mdstr.mdl:elementGuid="..." 
                    mdstr.mdl:elementOrder="..." />
            </mdstr.dsl.asd:Fields>

        </mdstr.dsl.asd:Entity>
    </mdstr.dsl.asd:Entities>

    <mdstr.dsl.tdd:Types>
        <mdstr.dsl.tdd:Type 
            mdstr.mdl:elementGuid="..." 
            mdstr.mdl:elementOrder="..." />
    </mdstr.dsl.tdd:Types>

    <mdstr.meta.prof:Profiles>
        <mdstr.meta.prof:Profile 
            use="PostgresDbProfile" />
    </mdstr.meta.prof:Profiles>

</mdstr.meta.prj:Project>
```

[[CONFIRMED]]
