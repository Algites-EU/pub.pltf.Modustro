plugins {
    `kotlin-dsl`
    `java-library`
    `maven-publish`
}

group = "eu.algites.pltf.modustro.builder"
version = providers.gradleProperty("modustro.gradle.version")
    .orElse(providers.gradleProperty("modustro.gradleinit.version"))
    .orElse("1.0-SNAPSHOT").get()

kotlin {
    jvmToolchain(17)
    sourceSets.main { kotlin.setSrcDirs(listOf("src/product/kotlin")) }
}

sourceSets.test { java.setSrcDirs(listOf("src/develop/java")); resources.setSrcDirs(listOf("src/develop/resources")) }

sourceSets.main {
    resources.srcDir("../../../../../repository/defaults")
    resources.include("algites-repository-defaults-public.yml")
}

dependencies {
    /* AIcModustroGradleRuntime links AIcInputSubscription during Settings initialization.
     * Keep this runtime/API dependency explicit in the standalone bootstrap and
     * in modustro-artifact.yml; do not rely on transitive bundle metadata. */
    if (project.findProject(":platform:builder:core:intf") != null) {
        api(project(":platform:builder:core:intf"))
    } else {
        api("eu.algites.pltf.modustro.builder:pub.pltf.Modustro_platform.builder.core.intf:1.0-SNAPSHOT")
    }
    api("eu.algites.tool.build:pub.gov.Algites_devops.build.modustrobuild:1.0-SNAPSHOT") {
        // The still-published governance metadata depends on the incompatible
        // legacy Security coordinate. The standalone Stage 0 bootstrap also
        // substitutes that coordinate while governance is being migrated.
        exclude(group = "eu.algites.lib.security", module = "pub.lib.Security_credentials.coreimpl")
    }
    // Include only the new credential generation. This is runtime integration;
    // the Gradle adapter does not depend on security APIs for compilation.
    runtimeOnly("eu.algites.lib.security:pub.lib.Security_credentials.core.impl:1.1-SNAPSHOT")
    runtimeOnly("eu.algites.tool.security:pub.tool.Security_credentials.gradle:1.1-SNAPSHOT")
    testImplementation("org.testng:testng:7.11.0")
}

gradlePlugin {
    isAutomatedPublishing = false
    plugins {
        create("modustroSettings") {
            id = "eu.algites.pltf.modustro.builder.settings"
            implementationClass = "eu.algites.pltf.modustro.builder.gradle.AIcModustroSettingsPlugin"
        }
        create("modustroRepository") {
            id = "eu.algites.pltf.modustro.builder.repository"
            implementationClass = "eu.algites.pltf.modustro.builder.gradle.AIcModustroRepositoryPlugin"
        }
    }
}

publishing {
    publications {
        if (findByName("mavenJava") == null) {
            create<MavenPublication>("mavenJava") { from(components["java"]) }
        }
        withType<MavenPublication>().configureEach {
            artifactId = "pub.pltf.Modustro_platform.builder.gradle"
        }
    }
}

tasks.test { useTestNG() }
