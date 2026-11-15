package eu.algites.pltf.modustro.builder.model.capability.configuration.python.xmldefs;

/**
 * Python native-documentation capability configuration.
 *
 * <p>Generated from canonical definition generation-of-native-documentation/1. Source: src/product/xmldefs/eu/algites/pltf/modustro/builder/model/capability/configuration/python/generation-of-native-documentation_1.xsd. Do not edit manually.</p>
 * @param enabled Whether native Python documentation generation is enabled.
 * @param sphinx Sphinx-specific configuration passed to the Python native-documentation generator.
 */
public record AIcgdGenerationOfNativeDocumentation_1(
        Boolean enabled,
        Object sphinx) {
    public static final String CANONICAL_SOURCE_ID = "generation-of-native-documentation";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/xmldefs/eu/algites/pltf/modustro/builder/model/capability/configuration/python/generation-of-native-documentation_1.xsd";

    /**
     * <strong>Field Name:</strong> {@code Enabled}<br/>
     * <strong>Field Description:</strong> Whether native Python documentation generation is enabled.
     */
    public static final String SCHEMA_FIELD_NAME__ENABLED = "Enabled";

    /**
     * <strong>Field Name:</strong> {@code Sphinx}<br/>
     * <strong>Field Description:</strong> Sphinx-specific configuration passed to the Python native-documentation generator.
     */
    public static final String SCHEMA_FIELD_NAME__SPHINX = "Sphinx";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdGenerationOfNativeDocumentation_1 {
        if (enabled != null) {
        }
        if (sphinx != null) {
        }
    }
}
