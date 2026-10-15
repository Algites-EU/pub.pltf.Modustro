# Modustro CFR — Cross-Product Standard (Working Outline)

**Status:** Draft architecture boundary; normative algorithm and storage format not yet finalized.

## Purpose

Modustro CFR provides a VCS-compatible, deterministic representation and merge protocol for independently edited structured data. It is **not owned by Modeler or Operator**. Modeler and Operator provide domain-specific profiles over common CFR primitives.

## Common contract (proposed)

- Stable entity and value identities; immutable provenance and append-only value/history records where applicable.
- Deterministic canonical serialization and reconstruction from merged VCS files.
- Explicit operation semantics for create/update/remove/undelete/relocate/reference and ordered collections.
- Concurrent-history reconciliation, conflict reporting and idempotent materialization; never silently discard information.
- Causal ordering / clocks and author metadata, with clock-skew and malicious-input considerations.
- Referential-integrity validation hooks, schema versioning and migration contracts.
- Storage profiles (including XML) must be separated from logical merge semantics.

## Domain profile extension points

A profile defines element types, domain-level invariants, conflict severity, validation, authorization and when a merged state is acceptable for deployment or execution. See `modeler/specs/Modustro-Modeler-CFR-Profile.md` and `operator/specs/Modustro-Operator-CFR-Profile.md`.

## Outstanding decisions

1. Exact merge algebra and whether convergence/commutativity/associativity/idempotence are formally guaranteed for each operation.
2. Representation of competing moves, tombstones, parent/ancestor cycles and orphan references.
3. Canonical serialization, namespace/prefix rules, stable diff and content identity.
4. Trust model for authors, audit seals, signatures and ordering; replay protection.
5. Conformance tests on reordered, duplicated and concurrently merged histories.

This document sets the ownership boundary only. It does **not** replace the more detailed private historical draft.
