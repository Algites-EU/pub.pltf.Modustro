package eu.algites.pltf.modustro.builder.model.capability.configuration.java;

/**
 * Canonical representations: YAMLDEFS: urn:algites:modustro:builder:yamldef:capability-configuration:java:generation-of-native-documentation:1 (src/product/yamldefs/eu/algites/pltf/modustro/builder/model/capability/configuration/java/generation-of-native-documentation_1.yamldef.schema.json); JSONDEFS: urn:algites:modustro:builder:jsondef:capability-configuration:java:generation-of-native-documentation:1 (src/product/jsondefs/eu/algites/pltf/modustro/builder/model/capability/configuration/java/generation-of-native-documentation_1.jsondef.schema.json)
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:yamldef:capability-configuration:java:generation-of-native-documentation:1/1. Source: src/product/yamldefs/eu/algites/pltf/modustro/builder/model/capability/configuration/java/generation-of-native-documentation_1.yamldef.schema.json. Do not edit manually.</p>
 * @param $schema Optional absolute URI of the schema describing this root document.
 * @param enabled Value of canonical property Enabled.
 * @param javadoc Value of canonical property Javadoc.
 */
public record AIcgdGenerationOfNativeDocumentation_1(
        String $schema,
        Boolean enabled,
        java.util.Map<String, Object> javadoc) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:yamldef:capability-configuration:java:generation-of-native-documentation:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/yamldefs/eu/algites/pltf/modustro/builder/model/capability/configuration/java/generation-of-native-documentation_1.yamldef.schema.json";

    /**
     * <strong>Field Name:</strong> {@code $schema}<br/>
     * <strong>Field Description:</strong> Optional absolute URI of the schema describing this root document.
     */
    public static final String SCHEMA_FIELD_NAME__SCHEMA = "$schema";

    /**
     * <strong>Field Name:</strong> {@code Enabled}
     */
    public static final String SCHEMA_FIELD_NAME__ENABLED = "Enabled";

    /**
     * <strong>Field Name:</strong> {@code Javadoc}
     */
    public static final String SCHEMA_FIELD_NAME__JAVADOC = "Javadoc";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdGenerationOfNativeDocumentation_1 {
        if ($schema != null) {
        }
        if (enabled != null) {
        }
        if (javadoc != null) {
        }
    }
}
