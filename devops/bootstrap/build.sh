#!/usr/bin/env bash
set -euo pipefail

# Emergency standalone bootstrap. No Modustro settings/project plugin is loaded.
# The legacy governance checkout is still required for the legacy yamldefs and modustrobuild modules.
locRepositoryDirectory="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd -P)"
locBuilderDirectory="$locRepositoryDirectory/platform/builder"
locSelection="${MODUSTRO_BOOTSTRAP_ROOT:-$locRepositoryDirectory}"
locSelectedModules=()
for locName in coreintf coreimpl gradle; do
    case "$locName" in
        coreintf) locLocation="$locBuilderDirectory/core/intf" ;;
        coreimpl) locLocation="$locBuilderDirectory/core/impl" ;;
        gradle) locLocation="$locBuilderDirectory/gradle" ;;
    esac
    if [[ "$locLocation" == "$locSelection" || "$locLocation" == "$locSelection/"* ]]; then
        locSelectedModules+=("$locName")
    fi
done
if (( ${#locSelectedModules[@]} == 0 )); then
    echo "No supported Builder artifact lies beneath --root=$locSelection" >&2
    exit 2
fi
locContains() { local locItem; for locItem in "${locSelectedModules[@]}"; do [[ "$locItem" == "$1" ]] && return 0; done; return 1; }
# A Gradle-only subtree is built through its existing isolated plugin adapter.
if (( ${#locSelectedModules[@]} == 1 )) && locContains gradle; then
    exec bash "$locRepositoryDirectory/devops/bootstrap/build-gradle.sh" "${1:---upload}"
fi
# Stage standalone builds below this repository's root build directory.
# All standalone bootstraps share the same dependency precedence policy.
# remote: published Cloudsmith snapshot first, Maven Local only as fallback;
# local: Maven Local first; source: source-prepared Maven Local first.
locAction="${1:---upload}"
case "$locAction" in
    --plan-only|--prepare-only|--upload) ;;
    --help|-h) echo 'Usage: builder-bootstrap.sh [--plan-only|--prepare-only|--upload]'; exit 0 ;;
    *) echo "Unsupported bootstrap action: $locAction" >&2; exit 2 ;;
esac
[[ $# -le 1 ]] || { echo 'Only one bootstrap action may be specified' >&2; exit 2; }
if [[ "$locAction" == --prepare-only ]]; then export MODUSTRO_BOOTSTRAP_PREPARE_ONLY=1; fi
if [[ "$locAction" == --plan-only ]]; then
    printf "Plan: Builder artifact scope %s; compile/test and stage selected modules only.\n" "${locSelectedModules[*]}"
    exit 0
fi
locLegacyGovernanceDirectory="${MODUSTRO_LEGACY_GOVERNANCE_DIR:-$(dirname -- "$locRepositoryDirectory")/pub.gov.Algites}"
for locLegacyModule in devops/build/yamldefs devops/build/modustrobuild; do
    if [[ ! -d "$locLegacyGovernanceDirectory/$locLegacyModule" ]]; then
        printf 'Legacy module %s not found in %s; set MODUSTRO_LEGACY_GOVERNANCE_DIR to your pub.gov.Algites checkout.\n' \
            "$locLegacyModule" "$locLegacyGovernanceDirectory" >&2
        exit 2
    fi
done
locDependencyMode="${MODUSTRO_BOOTSTRAP_DEPENDENCY_MODE:-remote}"
case "$locDependencyMode" in
    remote|local|source) ;;
    *) echo "Invalid MODUSTRO_BOOTSTRAP_DEPENDENCY_MODE='$locDependencyMode' (remote|local|source)" >&2; exit 2 ;;
esac
export MODUSTRO_BOOTSTRAP_DEPENDENCY_MODE="$locDependencyMode"
# Maven SNAPSHOT coordinates are mutable. The remote bootstrap revalidates
# published metadata so a cached build cannot silently use an older API.
# Child bootstraps inherit this choice; local/source can opt in explicitly.
locRefreshDependencies="${MODUSTRO_BOOTSTRAP_REFRESH_DEPENDENCIES:-}"
if [[ -z "$locRefreshDependencies" ]]; then
    if [[ "$locDependencyMode" == remote ]]; then
        locRefreshDependencies=1
    else
        locRefreshDependencies=0
    fi
fi
case "$locRefreshDependencies" in
    0|1) ;;
    *) echo "Invalid MODUSTRO_BOOTSTRAP_REFRESH_DEPENDENCIES='$locRefreshDependencies' (0|1)" >&2; exit 2 ;;
esac
export MODUSTRO_BOOTSTRAP_REFRESH_DEPENDENCIES="$locRefreshDependencies"
echo "Bootstrap dependency mode: $locDependencyMode; refresh published dependencies: $locRefreshDependencies"
locBuildRoot="$locRepositoryDirectory/build/run/builder-bootstrap"
mkdir -p "$locBuildRoot/work"
rm -rf -- "$locBuildRoot/maven-repository"
locTemporaryDirectory="$(mktemp -d "$locBuildRoot/work/bootstrap.XXXXXXXX")"
# Keep Python bytecode and pytest caches out of the checked-out source tree.
export PYTHONDONTWRITEBYTECODE=1
export PYTEST_ADDOPTS="${PYTEST_ADDOPTS:+${PYTEST_ADDOPTS} }-p no:cacheprovider"
trap 'rm -rf -- "$locTemporaryDirectory"' EXIT

# The source mode compiles current General/tool codegen prerequisites from sibling
# checkouts before this repository's already source-based Builder bootstrap.
if [[ "$locDependencyMode" == source && "${MODUSTRO_BOOTSTRAP_SOURCE_PREPARED:-0}" != 1 ]]; then
    locToolSource="${MODUSTRO_BOOTSTRAP_TOOL_SOURCE_DIR:-$(dirname -- "$locRepositoryDirectory")/pub.tool.General}"
    [[ -f "$locToolSource/generators/code/bootstrap.sh" ]] || {
        echo "Source mode needs pub.tool.General checkout (set MODUSTRO_BOOTSTRAP_TOOL_SOURCE_DIR): $locToolSource" >&2; exit 2;
    }
    echo "Bootstrapping source dependencies from $locToolSource"
    MODUSTRO_BOOTSTRAP_DEPENDENCY_MODE=source MODUSTRO_BOOTSTRAP_PREPARE_ONLY=1 \
        bash "$locToolSource/generators/code/bootstrap.sh"
fi

# The current Modustro Builder does not yet have a native build-source stage.
# Explicitly load the Algites profile for both Java and Python Defs/SDO generation.
# Universal devops/bootstrap/bootstrap.sh has already cleaned and generated sources.
[[ "${MODUSTRO_BOOTSTRAP_REPOSITORY:-}" == "$locRepositoryDirectory" ]] || {
    echo 'Use devops/bootstrap/bootstrap.sh to enforce clean-before-codegen' >&2; exit 2;
}

cat > "$locTemporaryDirectory/settings.gradle" <<'GRADLE'
pluginManagement { repositories { gradlePluginPortal(); mavenCentral() } }
rootProject.name = 'modustro-builder-emergency-bootstrap'
['yamldefs', 'modustrobuild', 'coreintf', 'coreimpl', 'gradle'].each { name ->
    include ':' + name
}
GRADLE

cat > "$locTemporaryDirectory/build.gradle" <<'GRADLE'
def locBootstrapOutput = new File(System.getenv('MODUSTRO_BOOTSTRAP_BUILD_ROOT'), 'gradle')
allprojects {
    layout.buildDirectory.set(new File(locBootstrapOutput,
            project == rootProject ? 'root' : 'modules/' + project.name))
    repositories {
        def bootstrapMode = System.getenv('MODUSTRO_BOOTSTRAP_DEPENDENCY_MODE') ?: 'remote'
        if (bootstrapMode != 'remote') maven { name = 'modustro-local'; url = uri(System.getenv('MODUSTRO_LOCAL_MAVEN_REPOSITORY') ?: new File(System.getenv('MODUSTRO_HOME') ?: new File(System.getProperty('user.home'), '.modustro').path, 'artifact-repos/java/maven')) }
        if (bootstrapMode == 'remote') maven {
            url = uri('https://dl.cloudsmith.io/public/algites/java-snapshots-pub/maven/')
            content { includeGroupByRegex('eu\\.algites\\..*') }
        }
        mavenCentral()

    }
}
subprojects {
    plugins.withId('maven-publish') {
        publishing.repositories.maven {
            name = 'Bootstrap'
            url = uri(new File(System.getenv('MODUSTRO_BOOTSTRAP_BUILD_ROOT'), 'maven-repository'))
        }
    }
    configurations.configureEach {
        resolutionStrategy.dependencySubstitution {
            if (System.getenv('MODUSTRO_BOOTSTRAP_INCLUDES_COREINTF') != '0') {
                substitute module('eu.algites.pltf.modustro.builder:pub.pltf.Modustro_platform.builder.core.intf') using project(':coreintf')
            }
            substitute module('eu.algites.pltf.modustro.builder:pub.gov.Algites_devops.build.modustro.builder.coreintf') using project(':coreintf')
            // Governance still requests the pre-1.1 Security artifact name.
            // Redirect only this legacy coordinate while Builder migration is in progress.
            substitute module('eu.algites.lib.security:pub.lib.Security_credentials.coreimpl') using module('eu.algites.lib.security:pub.lib.Security_credentials.core.impl:1.1-SNAPSHOT')
            substitute module('eu.algites.tool.build:pub.gov.Algites_devops.build.modustrobuild') using project(':modustrobuild')
        }
    }
}
GRADLE

locModules=(yamldefs modustrobuild coreintf coreimpl gradle)
locSources=(
    "$locLegacyGovernanceDirectory/devops/build/yamldefs"
    "$locLegacyGovernanceDirectory/devops/build/modustrobuild"
    "$locBuilderDirectory/core/intf"
    "$locBuilderDirectory/core/impl"
    "$locBuilderDirectory/gradle"
)
for locIndex in "${!locModules[@]}"; do
    locModule="${locModules[$locIndex]}"
    mkdir -p "$locTemporaryDirectory/$locModule"
    if [[ -d "${locSources[$locIndex]}/src" ]]; then
        ln -s "${locSources[$locIndex]}/src" "$locTemporaryDirectory/$locModule/src"
    fi
done
# The actual gradle build defines both Settings/Project plugin descriptors.
cp "$locBuilderDirectory/gradle/build.gradle.kts" "$locTemporaryDirectory/gradle/build.gradle.kts"
# The standalone temporary project must embed the same repository defaults.
python3 - "$locTemporaryDirectory/gradle/build.gradle.kts" "$locRepositoryDirectory/repository/defaults" <<'PYTHON'
from pathlib import Path
import sys
locFile = Path(sys.argv[1])
locText = locFile.read_text()
locText = locText.replace('../../../../../repository/defaults', sys.argv[2])
locFile.write_text(locText)
PYTHON

cat > "$locTemporaryDirectory/module.gradle" <<'GRADLE'
apply plugin: 'java-library'
apply plugin: 'maven-publish'
group = project.findProperty('bootstrapGroup')
version = project.findProperty('modustro.builder.bootstrapVersion') ?: '1.0-SNAPSHOT'
java {
    toolchain { languageVersion = JavaLanguageVersion.of(17) }
    withSourcesJar()
}
sourceSets.main {
    java.srcDirs = ['src/product/java', 'src/product/java.gen']
    resources.srcDirs = ['src/product/resources', 'src/product/yamldefs', 'src/product/xmldefs', 'src/product/jsondefs']
}
sourceSets.test {
    java.srcDirs = ['src/develop/java']
    resources.srcDirs = ['src/develop/resources']
}
tasks.withType(Test).configureEach { useTestNG() }
publishing {
    publications {
        mavenJava(MavenPublication) {
            from components.java
            artifactId = project.findProperty('bootstrapArtifactId')
        }
    }
}
GRADLE

for locModule in yamldefs modustrobuild coreintf coreimpl; do
    if [[ "$locModule" == yamldefs || "$locModule" == modustrobuild ]]; then
        locGroup='eu.algites.tool.build'
    else
        locGroup='eu.algites.pltf.modustro.builder'
    fi
    case "$locModule" in
        yamldefs) locArtifact='pub.gov.Algites_devops.build.yamldefs' ;;
        modustrobuild) locArtifact='pub.gov.Algites_devops.build.modustrobuild' ;;
        coreintf) locArtifact='pub.pltf.Modustro_platform.builder.core.intf' ;;
        coreimpl) locArtifact='pub.pltf.Modustro_platform.builder.core.impl' ;;
        gradle) locArtifact='pub.pltf.Modustro_platform.builder.gradle' ;;
        *) echo "Unknown bootstrap module: $locModule" >&2; exit 2 ;;
    esac
    printf "ext.bootstrapGroup = '%s'\next.bootstrapArtifactId = '%s'\napply from: rootProject.file('module.gradle')\n" \
        "$locGroup" "$locArtifact" > "$locTemporaryDirectory/$locModule/build.gradle"
done

cat >> "$locTemporaryDirectory/build.gradle" <<'GRADLE'
project(':coreintf') {
    dependencies { testImplementation 'org.testng:testng:7.11.0' }
}
project(':coreimpl') {
    dependencies {
        if (System.getenv('MODUSTRO_BOOTSTRAP_INCLUDES_COREINTF') == '0') {
            api("eu.algites.pltf.modustro.builder:pub.pltf.Modustro_platform.builder.core.intf:${project.findProperty('modustro.builder.bootstrapVersion') ?: '1.0-SNAPSHOT'}")
        } else {
            api project(':coreintf')
        }
        implementation 'com.fasterxml.jackson.core:jackson-databind:2.18.3'
        implementation 'com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.3'
        implementation 'com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.18.3'
        implementation 'eu.algites.lib.common:pub.lib.General_version.common.impl:1.1-SNAPSHOT'
        implementation 'eu.algites.lib.common:pub.lib.General_version.scheme.algites.v1:1.1-SNAPSHOT'
        implementation 'eu.algites.lib.common:pub.lib.General_version.scheme.gradle:1.1-SNAPSHOT'
        implementation 'eu.algites.lib.common:pub.lib.General_version.scheme.pep440:1.1-SNAPSHOT'
        implementation 'eu.algites.lib.common:pub.lib.General_version.scheme.conversion.algites2gradle.v1:1.1-SNAPSHOT'
        implementation 'eu.algites.lib.common:pub.lib.General_version.scheme.conversion.algites2pep440.v1:1.1-SNAPSHOT'
        testImplementation 'org.testng:testng:7.11.0'
    }
}
project(':modustrobuild') {
    dependencies {
        api project(':coreimpl')
        api 'eu.algites.tool.codegen:pub.tool.General_generators.code.defscodegen.impl:1.1-SNAPSHOT'
        api 'eu.algites.tool.codegen:pub.tool.General_generators.code.sdocodegen.impl:1.1-SNAPSHOT'
        implementation 'eu.algites.tool.codegen:pub.tool.General_generators.code.profile.algites:1.1-SNAPSHOT'
        implementation 'eu.algites.lib.security:pub.lib.Security_credentials.core.impl:1.1-SNAPSHOT'
    }
}
GRADLE

locVersion="${MODUSTRO_BUILDER_BOOTSTRAP_VERSION:-1.0-SNAPSHOT}"
locGradleCommand=(bash "$locRepositoryDirectory/gradlew")
if [[ -n "${MODUSTRO_GRADLE_EXECUTABLE:-}" ]]; then
    locGradleCommand=("${MODUSTRO_GRADLE_EXECUTABLE}")
fi
locGradleArgs=(--no-daemon)
if [[ "$locRefreshDependencies" == 1 ]]; then locGradleArgs+=(--refresh-dependencies); fi
if locContains coreintf; then
    export MODUSTRO_BOOTSTRAP_INCLUDES_COREINTF=1
else
    export MODUSTRO_BOOTSTRAP_INCLUDES_COREINTF=0
fi
locTasks=()
for locModule in "${locSelectedModules[@]}"; do
    if [[ "$locModule" == gradle ]]; then
        locTasks+=(":$locModule:test")
    else
        locTasks+=(":$locModule:check")
    fi
    locTasks+=(":$locModule:publishAllPublicationsToBootstrapRepository")
done
# Governance sources remain build prerequisites, never publications of this repo.
MODUSTRO_BOOTSTRAP_BUILD_ROOT="$locBuildRoot" \
"${locGradleCommand[@]}" "${locGradleArgs[@]}" --project-dir "$locTemporaryDirectory" \
    -Pmodustro.builder.bootstrapVersion="$locVersion" \
    -Pmodustro.gradle.version="$locVersion" \
    "${locTasks[@]}"

# Build Python distributions independently of the Modustro Gradle plugin.
# The workspace merges namespace package trees from all Python source roots,
# as the normal Python adapter does before invoking setuptools.
locPythonVersion="${MODUSTRO_BUILDER_BOOTSTRAP_PYTHON_VERSION:-}"
if [[ -z "$locPythonVersion" ]]; then
    if [[ "$locVersion" =~ ^([0-9]+(\.[0-9]+)*)-SNAPSHOT$ ]]; then
        locPythonVersion="${BASH_REMATCH[1]}.dev$(date -u +%Y%m%d%H%M%S)"
    elif [[ "$locVersion" =~ ^[0-9]+(\.[0-9]+)*$ ]]; then
        locPythonVersion="$locVersion"
    else
        echo "Set MODUSTRO_BUILDER_BOOTSTRAP_PYTHON_VERSION for nonstandard version $locVersion" >&2
        exit 2
    fi
fi
locPythonOutput="$locTemporaryDirectory/python-dist"
locPythonManifest="$locTemporaryDirectory/python-packages.tsv"
mkdir -p "$locPythonOutput"
locPythonArtifacts=()
if locContains coreintf; then locPythonArtifacts+=("$locBuilderDirectory/core/intf"); fi
PYTHONDONTWRITEBYTECODE=1 python3 - "$locPythonVersion" "$locPythonOutput" "$locPythonManifest" "$locRepositoryDirectory" \
    "${locPythonArtifacts[@]}" <<'PYTHON'
import pathlib
import re
import shutil
import subprocess
import sys
import tarfile
import tempfile
import tomllib
import zipfile

version, output_directory, manifest_file, repository_directory, *artifact_directories = sys.argv[1:]
output_directory = pathlib.Path(output_directory)
manifest_file = pathlib.Path(manifest_file)
with manifest_file.open('w', encoding='utf-8') as manifest:
    for artifact_string in artifact_directories:
        artifact = pathlib.Path(artifact_string)
        original_metadata = (artifact / 'pyproject.toml').read_text(encoding='utf-8')
        parsed = tomllib.loads(original_metadata)
        distribution = parsed['project']['name']
        roots = parsed['tool']['setuptools']['packages']['find']['where']
        locPythonWork = pathlib.Path(repository_directory) / 'build/run/builder-bootstrap/python-work'
        locPythonWork.mkdir(parents=True, exist_ok=True)
        with tempfile.TemporaryDirectory(prefix='modustro-bootstrap-python-', dir=locPythonWork) as temporary:
            project = pathlib.Path(temporary)
            source_root = project / 'src' / 'python'
            source_root.mkdir(parents=True)
            for root in roots:
                location = (artifact / root).resolve()
                if not location.is_relative_to(artifact.resolve()):
                    raise RuntimeError(f'Python source root escapes artifact: {root}')
                if not location.is_dir():
                    continue
                for source in sorted(location.rglob('*')):
                    if not source.is_file() or '__pycache__' in source.parts or source.suffix in ('.pyc', '.pyo'):
                        continue
                    destination = source_root / source.relative_to(location)
                    if destination.exists():
                        if destination.read_bytes() != source.read_bytes():
                            raise RuntimeError(f'Conflicting Python sources: {destination.relative_to(source_root)}')
                        continue
                    destination.parent.mkdir(parents=True, exist_ok=True)
                    shutil.copy2(source, destination)
            metadata = re.sub(r'(?m)^version\s*=\s*"[^"]+"\s*$', f'version = "{version}"', original_metadata, count=1)
            metadata = re.sub(r'(?m)^where\s*=.*$', 'where = ["src/python"]', metadata, count=1)
            if metadata == original_metadata and parsed['project']['version'] != version:
                raise RuntimeError(f'Unable to update Python version for {distribution}')
            (project / 'pyproject.toml').write_text(metadata, encoding='utf-8')
            for license_file in parsed['project'].get('license-files', []):
                original = artifact / license_file
                if not original.is_file():
                    original = pathlib.Path(repository_directory) / license_file
                if not original.is_file():
                    raise RuntimeError(f'Missing Python license {license_file} for {distribution}')
                destination = project / license_file
                destination.parent.mkdir(parents=True, exist_ok=True)
                shutil.copy2(original, destination)
            before = set(output_directory.iterdir())
            subprocess.run([sys.executable, '-m', 'build', '--no-isolation', '--outdir', str(output_directory)],
                           cwd=project, check=True)
            created = sorted(set(output_directory.iterdir()) - before)
            wheels = [item for item in created if item.suffix == '.whl']
            sdists = [item for item in created if item.name.endswith('.tar.gz')]
            if len(wheels) != 1 or len(sdists) != 1:
                raise RuntimeError(f'Expected one wheel and one sdist for {distribution}: {created}')
            for wheel in wheels:
                with zipfile.ZipFile(wheel) as archive:
                    if not any(name.endswith('.dist-info/METADATA') for name in archive.namelist()):
                        raise RuntimeError(f'Missing wheel metadata: {wheel}')
            for sdist in sdists:
                with tarfile.open(sdist, 'r:gz') as archive:
                    if not any(name.endswith('/PKG-INFO') for name in archive.getnames()):
                        raise RuntimeError(f'Missing sdist metadata: {sdist}')
            manifest.write(f'{wheels[0]}\t{sdists[0]}\n')
PYTHON

locRepo="${MODUSTRO_BUILDER_BOOTSTRAP_CLOUDSMITH_REPO:-algites/java-snapshots-pub}"
locPythonRepo="${MODUSTRO_BUILDER_BOOTSTRAP_PYTHON_CLOUDSMITH_REPO:-algites/python-snapshots-pub}"
locManifest="$locTemporaryDirectory/packages.tsv"
: > "$locTemporaryDirectory/coordinates.tsv"
for locModule in "${locSelectedModules[@]}"; do
    case "$locModule" in
        yamldefs|modustrobuild) locGroup='eu/algites/tool/build' ;;
        *) locGroup='eu/algites/pltf/modustro/builder' ;;
    esac
    case "$locModule" in
        yamldefs) locArtifact='pub.gov.Algites_devops.build.yamldefs' ;;
        modustrobuild) locArtifact='pub.gov.Algites_devops.build.modustrobuild' ;;
        coreintf) locArtifact='pub.pltf.Modustro_platform.builder.core.intf' ;;
        coreimpl) locArtifact='pub.pltf.Modustro_platform.builder.core.impl' ;;
        gradle) locArtifact='pub.pltf.Modustro_platform.builder.gradle' ;;
        *) echo "Unknown bootstrap module: $locModule" >&2; exit 2 ;;
    esac
    printf '%s\t%s\t%s\n' "${locGroup//\//.}" "$locArtifact" "$locVersion" >> "$locTemporaryDirectory/coordinates.tsv"
done


"${ALGITES_PYTHON_EXECUTABLE:-python3}" "$locRepositoryDirectory/devops/bootstrap/bootstrap-maven-stage.py" collect \
    --staging "$locBuildRoot/maven-repository" --coordinates "$locTemporaryDirectory/coordinates.tsv" \
    --output "$locTemporaryDirectory/staged-publications.json"
"${ALGITES_PYTHON_EXECUTABLE:-python3}" "$locRepositoryDirectory/devops/bootstrap/bootstrap-maven-stage.py" promote \
    --manifest "$locTemporaryDirectory/staged-publications.json" --output "$locManifest"
"${ALGITES_PYTHON_EXECUTABLE:-python3}" "$locRepositoryDirectory/devops/bootstrap/bootstrap-maven-stage.py" verify-gradle \
    --manifest "$locManifest" --gradle "${MODUSTRO_GRADLE_EXECUTABLE:-$locRepositoryDirectory/gradlew}" \
    --work "$locBuildRoot/local-gradle-verification"
# Publish verified Python distributions to the shared Modustro local Simple index.
"${ALGITES_PYTHON_EXECUTABLE:-python3}" "$locRepositoryDirectory/devops/bootstrap/modustro-local-python-repo.py" publish --manifest "$locPythonManifest"

locJavaCount="$(wc -l < "$locManifest")"
locPythonCount="$(wc -l < "$locPythonManifest")"
echo "Prepared $locJavaCount Maven artifacts and $locPythonCount Python wheels with their sdists."
echo "Java manifest: $locManifest"
echo "Python manifest: $locPythonManifest"
if [[ "${MODUSTRO_BOOTSTRAP_PREPARE_ONLY:-0}" == 1 ]]; then
    locOut="${MODUSTRO_BOOTSTRAP_OUTPUT_DIR:-$locBuildRoot}"
    mkdir -p "$locOut"
    cp "$locManifest" "$locOut/packages.tsv"
    cp "$locPythonManifest" "$locOut/python-packages.tsv"
    cp -a "$locPythonOutput" "$locOut/python-dist"
    echo "Preparation-only manifests: $locOut/packages.tsv and $locOut/python-packages.tsv"
    exit 0
fi
command -v algites-cloudsmith >/dev/null || { echo "algites-cloudsmith is required for upload" >&2; exit 2; }
algites-cloudsmith --session -- bash -c '
    set -euo pipefail
    locRepo="$1"; locManifest="$2"; locPythonRepo="$3"; locPythonManifest="$4"
    while IFS=$'"'"'\t'"'"' read -r locJar locPom locModule locSources; do
        locArgs=(--pom-file "$locPom" --extra-files "$locModule")
        if [[ -s "$locSources" ]]; then locArgs+=(--sources-file "$locSources"); fi
        cloudsmith push maven "$locRepo" "$locJar" "${locArgs[@]}"
    done < "$locManifest"
    while IFS=$'"'"'\t'"'"' read -r locWheel locSdist; do
        cloudsmith push python "$locPythonRepo" "$locWheel"
        cloudsmith push python "$locPythonRepo" "$locSdist"
    done < "$locPythonManifest"
' _ "$locRepo" "$locManifest" "$locPythonRepo" "$locPythonManifest"
