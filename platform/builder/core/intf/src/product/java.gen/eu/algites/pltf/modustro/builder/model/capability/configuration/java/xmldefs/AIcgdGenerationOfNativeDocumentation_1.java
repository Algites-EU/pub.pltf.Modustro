package eu.algites.pltf.modustro.builder.model.capability.configuration.java.xmldefs;

/**
 * Java native-documentation capability configuration.
 *
 * <p>Generated from canonical definition generation-of-native-documentation/1. Source: src/product/xmldefs/eu/algites/pltf/modustro/builder/model/capability/configuration/java/generation-of-native-documentation_1.xsd. Do not edit manually.</p>
 * @param enabled Whether native Java documentation generation is enabled.
 * @param javadoc Javadoc-specific configuration passed to the Java native-documentation generator.
 */
public record AIcgdGenerationOfNativeDocumentation_1(
        Boolean enabled,
        Object javadoc) {
    public static final String CANONICAL_SOURCE_ID = "generation-of-native-documentation";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/xmldefs/eu/algites/pltf/modustro/builder/model/capability/configuration/java/generation-of-native-documentation_1.xsd";

    /**
     * <strong>Field Name:</strong> {@code Enabled}<br/>
     * <strong>Field Description:</strong> Whether native Java documentation generation is enabled.
     */
    public static final String SCHEMA_FIELD_NAME__ENABLED = "Enabled";

    /**
     * <strong>Field Name:</strong> {@code Javadoc}<br/>
     * <strong>Field Description:</strong> Javadoc-specific configuration passed to the Java native-documentation generator.
     */
    public static final String SCHEMA_FIELD_NAME__JAVADOC = "Javadoc";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdGenerationOfNativeDocumentation_1 {
        if (enabled != null) {
        }
        if (javadoc != null) {
        }
    }
}
