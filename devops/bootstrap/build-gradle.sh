#!/usr/bin/env bash
set -euo pipefail

locRepositoryDir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd -P)"
locModuleDir="$locRepositoryDir/platform/builder/gradle"
# Keep generated Gradle metadata and task outputs below the repository root build.
locBuildRoot="$locRepositoryDir/build/run/gradle-bootstrap"
# All standalone bootstraps share the same dependency precedence policy.
# remote: published Cloudsmith snapshot first, Maven Local only as fallback;
# local: Maven Local first; source: source-prepared Maven Local first.
locAction="${1:---upload}"
case "$locAction" in
    --plan-only|--prepare-only|--upload) ;;
    --help|-h) echo 'Usage: bootstrap.sh [--plan-only|--prepare-only|--upload]'; exit 0 ;;
    *) echo "Unsupported action: $locAction" >&2; exit 2 ;;
esac
[[ $# -le 1 ]] || { echo 'Only one bootstrap action may be specified' >&2; exit 2; }
if [[ "$locAction" == --prepare-only ]]; then export MODUSTRO_BOOTSTRAP_PREPARE_ONLY=1; fi
if [[ "$locAction" == --plan-only ]]; then
    echo 'Plan: build and test standalone Gradle plugin, stage Maven, promote to Maven Local, verify, optionally upload.'
    exit 0
fi
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
# In source mode, do not merely prefer an arbitrary existing Maven Local JAR.
# Build the current Builder dependency graph from source before this standalone
# Gradle plugin build so interdependent SNAPSHOTs come from one source tree.
if [[ "$locDependencyMode" == source && "${MODUSTRO_BOOTSTRAP_SOURCE_PREPARED:-0}" != 1 ]]; then
    locBuilderBootstrap="$locRepositoryDir/devops/bootstrap/bootstrap.sh"
    [[ -f "$locBuilderBootstrap" ]] || {
        echo "Builder source bootstrap is missing: $locBuilderBootstrap" >&2
        exit 2
    }
    echo "Preparing Builder source dependencies via $locBuilderBootstrap"
    MODUSTRO_BOOTSTRAP_DEPENDENCY_MODE=source \
    MODUSTRO_BOOTSTRAP_PREPARE_ONLY=1 \
    MODUSTRO_BOOTSTRAP_SOURCE_PREPARED=0 \
        bash "$locBuilderBootstrap"
fi
mkdir -p "$locBuildRoot/work"
rm -rf -- "$locBuildRoot/maven-repository"
locBootstrapDir="$(mktemp -d "$locBuildRoot/work/bootstrap.XXXXXXXX")"
# Keep Python bytecode and pytest caches out of the checked-out source tree.
export PYTHONDONTWRITEBYTECODE=1
export PYTEST_ADDOPTS="${PYTEST_ADDOPTS:+${PYTEST_ADDOPTS} }-p no:cacheprovider"
trap 'rm -rf -- "${locBootstrapDir}"' EXIT

cat > "${locBootstrapDir}/settings.gradle.kts" <<'KTS'
pluginManagement { repositories { gradlePluginPortal(); mavenCentral() } }
dependencyResolutionManagement {
    repositories {
        val bootstrapMode = System.getenv("MODUSTRO_BOOTSTRAP_DEPENDENCY_MODE") ?: "remote"
        if (bootstrapMode != "remote") maven { name = "modustro-local"; url = uri(System.getenv("MODUSTRO_LOCAL_MAVEN_REPOSITORY") ?: java.io.File(System.getenv("MODUSTRO_HOME") ?: java.io.File(System.getProperty("user.home"), ".modustro").path, "artifact-repos/java/maven")) }
        if (bootstrapMode == "remote") maven {
            url = uri("https://dl.cloudsmith.io/public/algites/java-snapshots-pub/maven/")
            content { includeGroupByRegex("eu\\.algites\\..*") }
        }
        mavenCentral()

    }
}
rootProject.name = "modustro-gradle-bootstrap"
include(":gradle")
project(":gradle").projectDir = file(System.getenv("_TMP_MODUSTRO_GRADLE_SOURCE"))
KTS

cat > "${locBootstrapDir}/build.gradle.kts" <<'KTS'
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.kotlin.dsl.*
val locBootstrapOutput = file(System.getenv("MODUSTRO_BOOTSTRAP_BUILD_ROOT")
    ?: error("MODUSTRO_BOOTSTRAP_BUILD_ROOT is required"))
layout.buildDirectory.set(file("$locBootstrapOutput/gradle/root"))
subprojects {
    layout.buildDirectory.set(file("$locBootstrapOutput/gradle/modules/$name"))
    plugins.withId("maven-publish") {
        providers.gradleProperty("modustro.gradle.bootstrapRepositoryUrl")
            .orElse(providers.gradleProperty("modustro.gradleinit.bootstrapRepositoryUrl"))
            .orNull?.let { locUrl ->
            extensions.configure<PublishingExtension> {
                repositories.maven {
                    name = "bootstrap"
                    url = uri(locUrl)
                    val locUsername = providers.environmentVariable("MODUSTRO_GRADLE_BOOTSTRAP_USERNAME")
                        .orElse(providers.environmentVariable("MODUSTRO_GRADLEINIT_BOOTSTRAP_USERNAME")).orNull
                    val locPassword = providers.environmentVariable("MODUSTRO_GRADLE_BOOTSTRAP_PASSWORD")
                        .orElse(providers.environmentVariable("MODUSTRO_GRADLEINIT_BOOTSTRAP_PASSWORD")).orNull
                    if (locUsername != null || locPassword != null) {
                        credentials(PasswordCredentials::class) {
                            username = locUsername
                            password = locPassword
                        }
                    }
                }
            }
        }
    }
}
KTS

locGradleCommand=(bash "${locRepositoryDir}/gradlew")
if [[ -n "${MODUSTRO_GRADLE_EXECUTABLE:-}" ]]; then
    locGradleCommand=("${MODUSTRO_GRADLE_EXECUTABLE}")
fi

locPublicationTask=:gradle:publishAllPublicationsToBootstrapRepository
locGradleArgs=(--no-daemon)
if [[ "$locRefreshDependencies" == 1 ]]; then locGradleArgs+=(--refresh-dependencies); fi
MODUSTRO_BOOTSTRAP_BUILD_ROOT="$locBuildRoot" \
_TMP_MODUSTRO_GRADLE_SOURCE="${locModuleDir}" \
    "${locGradleCommand[@]}" "${locGradleArgs[@]}" \
    --project-dir "${locBootstrapDir}" -Pmodustro.gradle.bootstrapRepositoryUrl="file://$locBuildRoot/maven-repository" -Pmodustro.gradle.version="${MODUSTRO_BUILDER_BOOTSTRAP_VERSION:-1.0-SNAPSHOT}" :gradle:test "$locPublicationTask"

# The publication is now entirely staged. Only after the Gradle build succeeds
# is it installed to Maven Local; remote Cloudsmith follows local resolution.
locVersion="${MODUSTRO_BUILDER_BOOTSTRAP_VERSION:-1.0-SNAPSHOT}"
locStageHelper="$locRepositoryDir/devops/bootstrap/bootstrap-maven-stage.py"
locManifest="$locBuildRoot/packages.tsv"
printf '%s\t%s\t%s\n' 'eu.algites.pltf.modustro.builder' 'pub.pltf.Modustro_platform.builder.gradle' "$locVersion" > "$locBuildRoot/coordinates.tsv"
"${ALGITES_PYTHON_EXECUTABLE:-python3}" "$locStageHelper" collect \
    --staging "$locBuildRoot/maven-repository" --coordinates "$locBuildRoot/coordinates.tsv" \
    --output "$locBuildRoot/staged-publications.json"
"${ALGITES_PYTHON_EXECUTABLE:-python3}" "$locStageHelper" promote \
    --manifest "$locBuildRoot/staged-publications.json" --output "$locManifest"
"${ALGITES_PYTHON_EXECUTABLE:-python3}" "$locStageHelper" verify-gradle \
    --manifest "$locManifest" --gradle "${MODUSTRO_GRADLE_EXECUTABLE:-$locRepositoryDir/gradlew}" \
    --work "$locBuildRoot/local-gradle-verification"
locJavaCount="$(wc -l < "$locManifest")"
echo "Prepared $locJavaCount Maven artifacts and 0 Python distributions (Maven-only bootstrap)."
echo "Java manifest: $locManifest"
if [[ "${MODUSTRO_BOOTSTRAP_PREPARE_ONLY:-0}" == 1 ]]; then
    echo "Verified Gradle plugin Maven Local publication: $locManifest"
    exit 0
fi
command -v algites-cloudsmith >/dev/null || { echo 'algites-cloudsmith is required for upload' >&2; exit 2; }
algites-cloudsmith --session -- bash -c '
    set -euo pipefail
    while IFS=$'"'"'\t'"'"' read -r jar pom module sources; do
        args=(--pom-file "$pom" --extra-files "$module")
        [[ -s "$sources" ]] && args+=(--sources-file "$sources")
        cloudsmith push maven "$1" "$jar" "${args[@]}"
    done < "$2"
' _ "${MODUSTRO_BUILDER_BOOTSTRAP_CLOUDSMITH_REPO:-algites/java-snapshots-pub}" "$locManifest"
