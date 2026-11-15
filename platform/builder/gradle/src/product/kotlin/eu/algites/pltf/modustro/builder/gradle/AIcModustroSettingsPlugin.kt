package eu.algites.pltf.modustro.builder.gradle

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.resolve.RepositoriesMode
import org.gradle.kotlin.dsl.*

abstract class AIcModustroSettingsPlugin : Plugin<Settings> {
    @get:javax.inject.Inject abstract val buildEvents: org.gradle.build.event.BuildEventsListenerRegistry
    override fun apply(aSettings: Settings) = with(aSettings) {
        val locMode = providers.gradleProperty("modustro.dependencyMode")
            .orElse(providers.environmentVariable("MODUSTRO_DEPENDENCY_MODE"))
            .orElse(providers.environmentVariable("MODUSTRO_BOOTSTRAP_DEPENDENCY_MODE"))
            .orElse("remote").get()
        require(locMode in setOf("local", "source", "remote", "offline")) {
            "Unsupported Modustro dependency mode: $locMode"
        }
        val locUseLocal = locMode in setOf("local", "source", "offline") ||
            providers.gradleProperty("modustro.useMavenLocalForResolution")
                .orElse(providers.environmentVariable("MODUSTRO_USE_MAVEN_LOCAL_FOR_RESOLUTION"))
                .map { it.equals("true", ignoreCase = true) }.orElse(false).get()
        val locLocalMavenRoot = providers.environmentVariable("MODUSTRO_LOCAL_MAVEN_REPOSITORY").orNull
            ?: java.io.File(providers.environmentVariable("MODUSTRO_HOME").orNull
                ?: java.io.File(System.getProperty("user.home"), ".modustro").path,
                "artifact-repos/java/maven").absolutePath
        pluginManagement.repositories.apply {
            if (locUseLocal) {
                maven {
                    name = "modustro-local-plugins"
                    url = java.io.File(locLocalMavenRoot).toURI()
                    content { includeGroupByRegex("eu\\.algites(\\..*)?") }
                }
            }
            gradlePluginPortal()
            mavenCentral()
            if (locMode == "remote") {
                maven {
                    name = "algites-public-snapshots"
                    url = java.net.URI("https://dl.cloudsmith.io/public/algites/java-snapshots-pub/maven/")
                    mavenContent { snapshotsOnly() }
                }
            }
        }
        dependencyResolutionManagement.repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        dependencyResolutionManagement.repositories.apply {
            if (locUseLocal) {
                maven {
                    name = "modustro-local-dependencies"
                    url = java.io.File(locLocalMavenRoot).toURI()
                    content { includeGroupByRegex("eu\\.algites(\\..*)?") }
                }
            }
            mavenCentral()
            if (locMode == "remote") {
                maven {
                    name = "algites-public-snapshot-dependencies"
                    url = java.net.URI("https://dl.cloudsmith.io/public/algites/java-snapshots-pub/maven/")
                    mavenContent { snapshotsOnly() }
                }
            }
        }
        val locService = gradle.sharedServices.registerIfAbsent("modustroPublications", AIcModustroPublicationService::class.java) {
            parameters.credentialBaseDirectory.set(settingsDir)
        }
        buildEvents.onTaskCompletion(locService)
        val locRuntime = AIcModustroGradleRuntime(aSettings)
        gradle.extra["modustroGradleRuntime"] = locRuntime
        locRuntime.install(extensions.extraProperties)
        gradle.beforeProject { locRuntime.install(extensions.extraProperties) }
        if (!providers.gradleProperty("modustro.gradle.metadataOnly")
                .orElse(providers.gradleProperty("modustro.gradleinit.metadataOnly"))
                .map { it.equals("true", ignoreCase = true) }.orElse(false).get()) {
            AIcDiscover(aSettings, locRuntime)
        }
    }
}
