package eu.algites.pltf.modustro.builder.schema;

import java.nio.file.Files;
import java.nio.file.Path;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Tests format-neutral YAML, JSON and XML mapping without coupling the loader to one governance model. */
public final class AItcJacksonSchemaDataLoaderTest {

    /** Simple format-neutral fixture. */
    public static final class AIcFixture {
        public String Name;
        public boolean Enabled;
        public AIcFixture() { }
    }

    @Test
    public void AIcLoadsYaml() throws Exception {
        Path locFile = Files.createTempFile("modustro-schema-data-", ".yml");
        try {
            Files.writeString(locFile, "Name: yaml\nEnabled: true\n");
            AIcFixture locValue = AIcJacksonSchemaDataLoader.yaml().load(locFile, AIcFixture.class);
            Assert.assertEquals(locValue.Name, "yaml");
            Assert.assertTrue(locValue.Enabled);
        } finally {
            Files.deleteIfExists(locFile);
        }
    }

    @Test
    public void AIcLoadsJson() throws Exception {
        Path locFile = Files.createTempFile("modustro-schema-data-", ".json");
        try {
            Files.writeString(locFile, "{\"Name\":\"json\",\"Enabled\":true}\n");
            AIcFixture locValue = AIcJacksonSchemaDataLoader.json().load(locFile, AIcFixture.class);
            Assert.assertEquals(locValue.Name, "json");
            Assert.assertTrue(locValue.Enabled);
        } finally {
            Files.deleteIfExists(locFile);
        }
    }

    /** Generated enums may serialize with wire values distinct from Java constant names. */
    public enum AInFixtureWireValue {
        ONE("one");
        private final String wireValue;
        AInFixtureWireValue(String aWireValue) { wireValue = aWireValue; }
        public String wireValue() { return wireValue; }
    }

    /** Jackson test fixture representing one generated enum-bearing schema model. */
    public static final class AIcEnumFixture {
        public AInFixtureWireValue Kind;
        public AIcEnumFixture() { }
    }

    @Test
    public void AIcResolvesGeneratedEnumWireValue() throws Exception {
        Path locFile = Files.createTempFile("modustro-schema-data-", ".json");
        try {
            Files.writeString(locFile, "{\"Kind\":\"one\"}\n");
            AIcEnumFixture locValue = AIcJacksonSchemaDataLoader.json().load(locFile, AIcEnumFixture.class);
            Assert.assertEquals(locValue.Kind, AInFixtureWireValue.ONE);
        } finally {
            Files.deleteIfExists(locFile);
        }
    }

    @Test
    public void AIcIgnoresSchemaIdAtDocumentRoot() throws Exception {
        Path locFile = Files.createTempFile("modustro-schema-data-", ".json");
        try {
            Files.writeString(locFile, "{\"$schema\":\"https://example.invalid/schema.json\",\"Name\":\"schema\",\"Enabled\":true}\n");
            AIcFixture locValue = AIcJacksonSchemaDataLoader.json().load(locFile, AIcFixture.class);
            Assert.assertEquals(locValue.Name, "schema");
            Assert.assertTrue(locValue.Enabled);
        } finally {
            Files.deleteIfExists(locFile);
        }
    }

    @Test
    public void AIcLoadsXml() throws Exception {
        Path locFile = Files.createTempFile("modustro-schema-data-", ".xml");
        try {
            Files.writeString(locFile, "<Fixture><Name>xml</Name><Enabled>true</Enabled></Fixture>\n");
            AIcFixture locValue = AIcJacksonSchemaDataLoader.xml().load(locFile, AIcFixture.class);
            Assert.assertEquals(locValue.Name, "xml");
            Assert.assertTrue(locValue.Enabled);
        } finally {
            Files.deleteIfExists(locFile);
        }
    }
}
