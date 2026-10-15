#!/usr/bin/env bash
set -euo pipefail

locModuleDir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
locRepositoryDir="$(cd -- "${locModuleDir}/../../../../.." && pwd)"
locBootstrapDir="$(mktemp -d "${TMPDIR:-/tmp}/modustro-gradle-bootstrap.XXXXXXXX")"
trap 'rm -rf -- "${locBootstrapDir}"' EXIT

cat > "${locBootstrapDir}/settings.gradle.kts" <<'KTS'
pluginManagement { repositories { gradlePluginPortal(); mavenCentral() } }
dependencyResolutionManagement {
    repositories {
        if (providers.gradleProperty("modustro.useMavenLocalForResolution").orNull == "true") { mavenLocal() }
        mavenCentral()
        maven { url = uri("https://dl.cloudsmith.io/public/algites/java-snapshots-pub/maven/") }
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
subprojects {
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

if [[ $# -eq 0 ]]; then set -- :gradle:publishToMavenLocal; fi
_TMP_MODUSTRO_GRADLE_SOURCE="${locModuleDir}" \
    "${locGradleCommand[@]}" --no-daemon \
    --project-dir "${locBootstrapDir}" "$@"
