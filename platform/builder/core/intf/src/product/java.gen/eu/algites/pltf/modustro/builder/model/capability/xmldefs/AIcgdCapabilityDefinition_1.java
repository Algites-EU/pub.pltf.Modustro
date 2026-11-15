package eu.algites.pltf.modustro.builder.model.capability.xmldefs;

/**
 * Defines a Modustro Builder capability, the model scopes where it may be configured, and the optional schema that describes capability-specific configuration.
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:model:capability-definition:1/1. Source: src/product/xmldefs/eu/algites/pltf/modustro/builder/model/capability/capability-definition_1.xsd. Do not edit manually.</p>
 * @param capability Stable identifier of the capability being defined.
 * @param allowedScope Descriptor scope at which this capability may be declared.
 * @param configurationSchemaId Optional canonical schema identifier for capability-specific configuration.
 */
public record AIcgdCapabilityDefinition_1(
        String capability,
        java.util.List<String> allowedScope,
        String configurationSchemaId) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:model:capability-definition:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/xmldefs/eu/algites/pltf/modustro/builder/model/capability/capability-definition_1.xsd";

    /**
     * <strong>Field Name:</strong> {@code Capability}<br/>
     * <strong>Field Description:</strong> Stable identifier of the capability being defined.
     */
    public static final String SCHEMA_FIELD_NAME__CAPABILITY = "Capability";

    /**
     * <strong>Field Name:</strong> {@code AllowedScope}<br/>
     * <strong>Field Description:</strong> Descriptor scope at which this capability may be declared.
     */
    public static final String SCHEMA_FIELD_NAME__ALLOWED_SCOPE = "AllowedScope";

    /**
     * <strong>Field Name:</strong> {@code ConfigurationSchemaId}<br/>
     * <strong>Field Description:</strong> Optional canonical schema identifier for capability-specific configuration.
     */
    public static final String SCHEMA_FIELD_NAME__CONFIGURATION_SCHEMA_ID = "ConfigurationSchemaId";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdCapabilityDefinition_1 {
        java.util.Objects.requireNonNull(capability, "Capability");
        if (capability != null) {
        }
        java.util.Objects.requireNonNull(allowedScope, "AllowedScope");
        if (allowedScope != null) {
            for (var locItem : allowedScope) {
                java.util.Objects.requireNonNull(locItem, "Array item");
                if (!java.util.List.of("repository", "artifact_set", "artifact").contains(locItem.toString())) throw new IllegalArgumentException("Schema value violates its constraints.");
            }
        }
        if (configurationSchemaId != null) {
        }
    }
}
