package eu.algites.pltf.modustro.builder.gradle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Guards the shared Java baseline for Modustro descriptors and Gradle variants. */
public final class AItcModustroJavaEnvironmentDefaultsTest {
    private static Path AIcRepository() throws IOException {
        Path locRepository = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        while (locRepository != null && !Files.isRegularFile(locRepository.resolve("modustro-source-repository.yml"))) {
            locRepository = locRepository.getParent();
        }
        if (locRepository == null) throw new IOException("Cannot locate governance repository sources.");
        return locRepository;
    }

    /** Java target must not silently follow the local Gradle daemon JDK. */
    @Test
    public void AIcHasSharedJava17Baseline() throws IOException {
        Path locRepository = AIcRepository();
        String locDefaults = Files.readString(locRepository.resolve(
                "platform/builder/gradle/src/product/resources/algites-repository-defaults-public.yml"));
        String locDescriptor = Files.readString(locRepository.resolve(
                "platform/builder/gradle/modustro-artifact.yml"));
        String locStandalone = Files.readString(locRepository.resolve(
                "platform/builder/gradle/build.gradle.kts"));
        for (String locConfig : new String[] {locDefaults}) {
            Assert.assertTrue(locConfig.contains("EnvironmentRequirements:\n  Java:\n    Minimum: \">=17\"\n    Prefer: \"17\""),
                    "A Java 17 environment requirement must be present.");
        }
        Assert.assertTrue(locStandalone.contains("jvmToolchain(17)"),
                "The standalone bootstrap must use Java 17.");
        Assert.assertTrue(locDescriptor.contains("Minimum: \">=17\""),
                "The published Gradle plugin must declare Java 17 as its minimum runtime.");
    }
}
