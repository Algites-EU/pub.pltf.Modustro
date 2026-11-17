package eu.algites.pltf.modustro.builder.schema;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Ensures the core schema-loader merge and local dependency migration remain intact. */
public final class AItcBuilderCoreLayoutTest {

    /** Locates the source checkout used to compile this module. */
    private static Path AIcSourceRoot() throws IOException {
        Path locCurrent = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        while (locCurrent != null && !Files.isRegularFile(locCurrent.resolve("modustro-source-repository.yml"))) {
            locCurrent = locCurrent.getParent();
        }
        if (locCurrent == null) {
            throw new IOException("Modustro source checkout not found.");
        }
        return locCurrent;
    }

    /** The loader implementation belongs to core/impl, not to a separate Jackson artifact. */
    @Test
    public void AIcSchemaLoaderUsesCoreArtifacts() throws Exception {
        Path locBuilder = AIcSourceRoot().resolve("platform/builder");
        Assert.assertTrue(Files.isRegularFile(locBuilder.resolve("core/modustro-artifact-set.yml")));
        Assert.assertTrue(Files.isRegularFile(locBuilder.resolve("core/intf/modustro-artifact.yml")));
        Assert.assertTrue(Files.isRegularFile(locBuilder.resolve("core/impl/modustro-artifact.yml")));
        Assert.assertTrue(Files.isRegularFile(locBuilder.resolve(
                "core/intf/src/product/java/eu/algites/pltf/modustro/builder/model/schema/AIiSchemaDataLoader.java")));
        Assert.assertTrue(Files.isRegularFile(locBuilder.resolve(
                "core/impl/src/product/java/eu/algites/pltf/modustro/builder/schema/AIcJacksonSchemaDataLoader.java")));
        Assert.assertFalse(Files.exists(locBuilder.resolve("structureddata")));
        Assert.assertFalse(Files.exists(locBuilder.resolve("coreintf")));
        Assert.assertFalse(Files.exists(locBuilder.resolve("coreimpl")));
    }

    /** Gradle must use the current local core interface, never the historical governance JAR. */
    @Test
    public void AIcDependenciesTargetCurrentBuilderCore() throws Exception {
        Path locBuilder = AIcSourceRoot().resolve("platform/builder");
        String locImplementationDescriptor = Files.readString(
                locBuilder.resolve("core/impl/modustro-artifact.yml"));
        String locGradleDescriptor = Files.readString(
                locBuilder.resolve("gradle/modustro-artifact.yml"));
        String locGradleBuild = Files.readString(
                locBuilder.resolve("gradle/build.gradle.kts"));
        String locNewCore = "pub.pltf.Modustro_platform.builder.core.intf";
        String locObsoleteCore = "pub.gov.Algites_devops.build.modustro.builder.coreintf";
        Assert.assertTrue(locImplementationDescriptor.contains(locNewCore));
        Assert.assertTrue(locGradleDescriptor.contains(locNewCore));
        Assert.assertTrue(locGradleBuild.contains("project.findProject(\":platform:builder:core:intf\")"));
        Assert.assertFalse(locImplementationDescriptor.contains(locObsoleteCore));
        Assert.assertFalse(locGradleDescriptor.contains(locObsoleteCore));
        Assert.assertFalse(locGradleBuild.contains(locObsoleteCore));
        Assert.assertTrue(locImplementationDescriptor.contains("pub.lib.General_version.common.impl"));
        Assert.assertFalse(locImplementationDescriptor.contains("pub.lib.General_version.core"));
        Assert.assertTrue(locImplementationDescriptor.contains("Exact: \"1.1-SNAPSHOT\""));
        Assert.assertTrue(locImplementationDescriptor.contains("jackson-dataformat-xml"));
        String locBootstrap = Files.readString(AIcSourceRoot().resolve("devops/bootstrap/build.sh"));
        String locRuntimeDescriptor = Files.readString(
                locBuilder.resolve("runtime/modustro-artifact.yml"));
        Assert.assertTrue(locBootstrap.contains("prepare.py"));
        Assert.assertTrue(locBootstrap.contains("bootstrapSdoJava"));
        Assert.assertTrue(Files.isRegularFile(locBuilder.resolve("runtime/modustro-artifact.yml")));
        Assert.assertTrue(locRuntimeDescriptor.contains("pub.lib.General_version.common.impl"));
        Assert.assertTrue(locRuntimeDescriptor.contains("pub.lib.General_naming.convention.profile.algites"));
        Assert.assertTrue(locRuntimeDescriptor.contains("pub.tool.General_generators.code.defscodegen.impl"));
        Assert.assertTrue(locRuntimeDescriptor.contains("pub.tool.General_generators.code.sdocodegen.impl"));
        Assert.assertTrue(locRuntimeDescriptor.contains("pub.pltf.Modustro_platform.builder.core.impl"));
        Assert.assertFalse(locRuntimeDescriptor.contains("pub.gov.Algites_devops.build.modustrobuild"));
    }
}
