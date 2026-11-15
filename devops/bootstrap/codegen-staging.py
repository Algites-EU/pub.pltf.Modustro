#!/usr/bin/env python3
"""Stage native Builder schema generation without rewriting product schema sources.

The published standalone Defs CLI derives its Java/Python package from the
schema's parent directory. The older yamldefs artifact stores its schemas flat
and declares their package through GlobalPublicationPathId sidecars instead.
This tool creates disposable normalized views, combines dependent definition
inputs for one cross-artifact canonical pass, then partitions generated files
back to their owning artifacts. No .gen directory is replaced until every
Defs/SDO command for both languages has succeeded.
"""

from __future__ import annotations

import argparse
import os
from pathlib import Path, PurePosixPath
import re
import shutil


SCHEMA_SUFFIXES = {
    "yamldefs": ".yamldef.schema.json",
    "jsondefs": ".jsondef.schema.json",
    "xmldefs": ".xsd",
}
GENERATED_ROOTS = ("java.gen", "python.gen")
PUBLICATION_ID = re.compile(r"(?m)^GlobalPublicationPathId:\s*([^\s#]+)\s*$")
PACKAGE_SEGMENT = re.compile(r"^[A-Za-z_][A-Za-z0-9_]*$")


def _publication_package(source: Path, kind: str) -> Path:
    """Recover a flat schema's package from its canonical publication sidecar."""
    metadata = source.with_name(source.name + ".meta.yml")
    if not metadata.is_file():
        raise ValueError(f"Flat schema has no publication metadata: {metadata}")
    match = PUBLICATION_ID.search(metadata.read_text(encoding="utf-8"))
    if match is None:
        raise ValueError(f"Flat schema has no GlobalPublicationPathId: {metadata}")
    publication = PurePosixPath(match.group(1))
    parts = publication.parts
    if (publication.is_absolute() or ".." in parts or len(parts) < 4
            or parts[-2] != kind or parts[-1] != source.name):
        raise ValueError(f"Invalid publication path for {source}: {publication}")
    package = parts[:-2]
    if not all(PACKAGE_SEGMENT.fullmatch(part) for part in package):
        raise ValueError(f"Invalid package declared by {metadata}: {package}")
    return Path(*package)


def prepare(artifact: Path, stage: Path) -> None:
    """Copy schemas into a temporary CLI-compatible tree, retaining source bytes."""
    if stage.exists():
        raise ValueError(f"Staging directory already exists: {stage}")
    for kind, suffix in SCHEMA_SUFFIXES.items():
        source_root = artifact / "src" / "product" / kind
        if not source_root.is_dir():
            continue
        destination_root = stage / "src" / "product" / kind
        flat_mappings: dict[str, Path] = {}
        for source in sorted(source_root.iterdir()):
            if source.is_file() and source.name.endswith(suffix):
                package = _publication_package(source, kind)
                flat_mappings[source.name] = package / source.name
                flat_mappings[source.name + ".meta.yml"] = package / (source.name + ".meta.yml")
        for source in sorted(source_root.rglob("*")):
            if not source.is_file():
                continue
            if source.is_symlink():
                raise ValueError(f"Unexpected schema symlink: {source}")
            relative = source.relative_to(source_root)
            relative = flat_mappings.get(source.name, relative) if relative.parent == Path(".") else relative
            destination = destination_root / relative
            if destination.exists():
                raise ValueError(f"Schema staging collision: {destination}")
            destination.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(source, destination)
    print(f"Schema source view prepared: {artifact} -> {stage}", flush=True)


def combine(stages: list[Path], shared: Path) -> None:
    """Combine schema inputs from all dependent artifacts for one canonical pass.

    Defs Codegen resolves cross-file allOf references from the full batch
    index. A reference-only input must therefore take part in parsing, but its
    generated contract remains owned by the source artifact after splitting.
    """
    if shared.exists():
        raise ValueError(f"Combined source view already exists: {shared}")
    seen: dict[Path, Path] = {}
    for stage in stages:
        for kind in SCHEMA_SUFFIXES:
            root = stage / "src" / "product" / kind
            if not root.is_dir():
                continue
            for source in sorted(root.rglob("*")):
                if not source.is_file():
                    continue
                if source.is_symlink():
                    raise ValueError(f"Unexpected schema symlink: {source}")
                relative = source.relative_to(stage)
                if relative in seen:
                    raise ValueError(f"Duplicate schema input {relative} from {seen[relative]} and {source}")
                seen[relative] = source
                destination = shared / relative
                destination.parent.mkdir(parents=True, exist_ok=True)
                shutil.copy2(source, destination)
    if not seen:
        raise ValueError("Combined source view is empty")
    print(f"Combined {len(seen)} definition files from {len(stages)} artifacts: {shared}", flush=True)


def _owners(stages: list[Path]) -> dict[tuple[str, ...], int]:
    """Derive generated package ownership from the staged definition paths."""
    owners: dict[tuple[str, ...], int] = {}
    for index, stage in enumerate(stages):
        for kind, suffix in SCHEMA_SUFFIXES.items():
            root = stage / "src" / "product" / kind
            if not root.is_dir():
                continue
            for source in root.rglob("*" + suffix):
                relative = source.relative_to(root)
                package = relative.parent.parts
                if not package or not all(PACKAGE_SEGMENT.fullmatch(item) for item in package):
                    raise ValueError(f"Schema has no legal package: {source}")
                previous = owners.setdefault(package, index)
                if previous != index:
                    raise ValueError(f"Multiple artifacts own generated package {package}: {stages[previous]} / {stage}")
    if not owners:
        raise ValueError("No definition packages found to determine output ownership")
    return owners


def partition(shared: Path, stages: list[Path]) -> None:
    """Return each generated file to its original artifact without copying dependencies.

    Generated SDOs may use nested impl packages; ownership follows the most
    specific input schema package. Unknown output packages fail closed.
    """
    owners = _owners(stages)
    counts = {(index, root): 0 for index in range(len(stages)) for root in GENERATED_ROOTS}
    for root in GENERATED_ROOTS:
        generated = shared / "src" / "product" / root
        if not generated.is_dir():
            raise ValueError(f"Generator did not create output tree: {generated}")
        expected_suffix = ".java" if root == "java.gen" else ".py"
        for source in sorted(generated.rglob("*")):
            if not source.is_file():
                continue
            relative = source.relative_to(generated)
            if source.is_symlink() or source.suffix != expected_suffix:
                raise ValueError(f"Unexpected generated file: {source}")
            matches = [(len(package), index) for package, index in owners.items()
                       if relative.parts[:len(package)] == package]
            if not matches:
                raise ValueError(f"Generated file has no source artifact owner: {source}")
            strongest = max(length for length, _ in matches)
            matches = {index for length, index in matches if length == strongest}
            if len(matches) != 1:
                raise ValueError(f"Ambiguous generated file ownership: {source} / {matches}")
            index = next(iter(matches))
            destination = stages[index] / "src" / "product" / root / relative
            if destination.exists():
                raise ValueError(f"Partition collision: {destination}")
            destination.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(source, destination)
            counts[index, root] += 1
    for (index, root), count in sorted(counts.items()):
        if count == 0:
            raise ValueError(f"No generated {root} sources for {stages[index]}")
        print(f"Partitioned {count} {root} files into {stages[index]}", flush=True)


def commit(pairs: list[tuple[Path, Path]]) -> None:
    """Atomically swap all generated roots, rolling back on any filesystem error."""
    operations: list[tuple[Path, Path, Path]] = []
    for stage, artifact in pairs:
        for root in GENERATED_ROOTS:
            generated = stage / "src" / "product" / root
            if not generated.is_dir():
                raise ValueError(f"No generated output directory: {generated}")
            suffix = ".java" if root == "java.gen" else ".py"
            prefix = "AIig" if root == "java.gen" else "aiig"
            if not any(path.name.startswith(prefix) for path in generated.rglob("*" + suffix)):
                raise ValueError(f"No generated read contracts: {generated}")
            destination = artifact / "src" / "product" / root
            backup = stage / ("backup-" + root)
            if backup.exists():
                raise ValueError(f"Unexpected backup directory: {backup}")
            operations.append((generated, destination, backup))
    backups: list[tuple[Path, Path]] = []
    installed: list[Path] = []
    try:
        for generated, destination, backup in operations:
            destination.parent.mkdir(parents=True, exist_ok=True)
            if destination.exists():
                os.replace(destination, backup)
                backups.append((backup, destination))
            os.replace(generated, destination)
            installed.append(destination)
    except Exception:
        for destination in reversed(installed):
            if destination.is_dir():
                shutil.rmtree(destination)
        for backup, destination in reversed(backups):
            os.replace(backup, destination)
        raise
    for backup, _ in backups:
        shutil.rmtree(backup)
    for _, artifact in pairs:
        print(f"Committed Java/Python generated sources: {artifact}", flush=True)


def main() -> None:
    """Provide an explicit prepare or all-artifact commit operation for bootstrap."""
    parser = argparse.ArgumentParser(description=__doc__)
    commands = parser.add_subparsers(dest="command", required=True)
    preparation = commands.add_parser("prepare")
    preparation.add_argument("artifact", type=Path)
    preparation.add_argument("stage", type=Path)
    combination = commands.add_parser("combine")
    combination.add_argument("shared", type=Path)
    combination.add_argument("stages", type=Path, nargs="+")
    division = commands.add_parser("partition")
    division.add_argument("shared", type=Path)
    division.add_argument("stages", type=Path, nargs="+")
    publishing = commands.add_parser("commit")
    publishing.add_argument("pairs", nargs="+", help="STAGE=ARTIFACT pairs")
    arguments = parser.parse_args()
    if arguments.command == "prepare":
        prepare(arguments.artifact.resolve(), arguments.stage.resolve())
    elif arguments.command == "combine":
        combine([item.resolve() for item in arguments.stages], arguments.shared.resolve())
    elif arguments.command == "partition":
        partition(arguments.shared.resolve(), [item.resolve() for item in arguments.stages])
    else:
        pairs = []
        for entry in arguments.pairs:
            stage, marker, artifact = entry.partition("=")
            if not marker or not stage or not artifact:
                parser.error(f"Expected STAGE=ARTIFACT, got {entry!r}")
            pairs.append((Path(stage).resolve(), Path(artifact).resolve()))
        commit(pairs)


if __name__ == "__main__":
    main()
