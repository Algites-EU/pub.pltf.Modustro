# Builder Phase 1 — source-copy checkpoint

This is a source transfer, NOT an independently buildable or published Builder.

Source baseline: pub.gov.Algites Phase 0 CI fix (2026-10-13), copied from `devops/build/modustro/builder/` into `platform/builder/`.

The existing governance Builder remains untouched. This transfer intentionally preserves descriptors and Gradle scripts as historical references: repository-relative native artifact coordinates, init settings bootstrap, dependency subscriptions, codegen and YAML definitions remain bound to governance paths. Do not run a release from this copied tree or treat it as self-hosted until Phase 2 establishes pinned bootstrap, platform-native coordinates, and integration tests.

The S3 adapter and schema S3 transport currently remain in the public copy because they are referenced by production registration code. Their source has additionally been staged in the private platform repository under `platform/builder/adapters/s3/migration-source/`. This is NOT a functioning separate plugin. The shared schema publication lifecycle and public SPI must be factored out before deleting the public copies and loading the private adapter from priv.gov.Algites.

The `modustrobuild` and `yamldefs` artifacts and the external Gradle scripts have not been moved. Their ownership and coordinate changes require a separate plan. No CI cutover or Maven/Python upload is part of this checkpoint.
