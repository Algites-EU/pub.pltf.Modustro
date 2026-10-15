package eu.algites.pltf.modustro.builder.gradle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Guards the Gradle integration module identity and the separation between public plugin IDs and implementation packages. */
public final class AItcModustroGradleIntegrationNamingTest {

    @Test
    public void AIcPluginImplementationsUseGradleNamespace() throws IOException {
        Path locGradle = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        if (!Files.isRegularFile(locGradle.resolve("build.gradle.kts"))) {
            throw new IOException("Test working directory must be the Gradle integration artifact root: " + locGradle);
        }
        String locBuild = Files.readString(locGradle.resolve("build.gradle.kts"));
        Assert.assertTrue(locBuild.contains("id = \"eu.algites.pltf.modustro.builder.settings\""));
        Assert.assertTrue(locBuild.contains("id = \"eu.algites.pltf.modustro.builder.repository\""));
        Assert.assertTrue(locBuild.contains("implementationClass = \"eu.algites.pltf.modustro.builder.gradle.AIcModustroSettingsPlugin\""));
        Assert.assertTrue(locBuild.contains("implementationClass = \"eu.algites.pltf.modustro.builder.gradle.AIcModustroRepositoryPlugin\""));
        Assert.assertTrue(locBuild.contains("pub.pltf.Modustro_platform.builder.gradle"));
        Assert.assertTrue(locBuild.contains("modustro.gradleinit.version"), "Existing version property must remain supported.");
    }

    @Test
    public void AIcMetadataOnlyLegacyAliasRemainsSupported() throws IOException {
        Path locGradle = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        String locSettings = Files.readString(locGradle.resolve(
            "src/product/kotlin/eu/algites/pltf/modustro/builder/gradle/AIcModustroSettingsPlugin.kt"));
        Assert.assertTrue(locSettings.contains("modustro.gradle.metadataOnly"));
        Assert.assertTrue(locSettings.contains("modustro.gradleinit.metadataOnly"));
    }
}
