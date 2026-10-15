package eu.algites.pltf.modustro.builder.model.capability;

/**
 * Canonical representations: YAMLDEFS: urn:algites:modustro:builder:yamldef:model:capability-definition:1 (src/product/yamldefs/eu/algites/pltf/modustro/builder/model/capability/capability-definition_1.yamldef.schema.json); JSONDEFS: urn:algites:modustro:builder:jsondef:model:capability-definition:1 (src/product/jsondefs/eu/algites/pltf/modustro/builder/model/capability/capability-definition_1.jsondef.schema.json)
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:yamldef:model:capability-definition:1/1. Source: src/product/yamldefs/eu/algites/pltf/modustro/builder/model/capability/capability-definition_1.yamldef.schema.json. Do not edit manually.</p>
 * @param $schema Optional absolute URI of the schema describing this root document.
 * @param capability Value of canonical property Capability.
 * @param allowedScopes Value of canonical property AllowedScopes.
 * @param configurationSchemaId Value of canonical property ConfigurationSchemaId.
 */
public record AIcgdCapabilityDefinition_1(
        String $schema,
        String capability,
        java.util.List<String> allowedScopes,
        String configurationSchemaId) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:yamldef:model:capability-definition:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/yamldefs/eu/algites/pltf/modustro/builder/model/capability/capability-definition_1.yamldef.schema.json";

    /**
     * <strong>Field Name:</strong> {@code $schema}<br/>
     * <strong>Field Description:</strong> Optional absolute URI of the schema describing this root document.
     */
    public static final String SCHEMA_FIELD_NAME__SCHEMA = "$schema";

    /**
     * <strong>Field Name:</strong> {@code Capability}
     */
    public static final String SCHEMA_FIELD_NAME__CAPABILITY = "Capability";

    /**
     * <strong>Field Name:</strong> {@code AllowedScopes}
     */
    public static final String SCHEMA_FIELD_NAME__ALLOWED_SCOPES = "AllowedScopes";

    /**
     * <strong>Field Name:</strong> {@code ConfigurationSchemaId}
     */
    public static final String SCHEMA_FIELD_NAME__CONFIGURATION_SCHEMA_ID = "ConfigurationSchemaId";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdCapabilityDefinition_1 {
        if ($schema != null) {
        }
        java.util.Objects.requireNonNull(capability, "Capability");
        if (capability != null) {
            if (!java.util.regex.Pattern.compile("^[a-z][a-z0-9]*(?:_[a-z0-9]+)*$").matcher(capability.toString()).find()) throw new IllegalArgumentException("Schema value violates its constraints.");
        }
        java.util.Objects.requireNonNull(allowedScopes, "AllowedScopes");
        if (allowedScopes != null) {
            for (var locItem : allowedScopes) {
                java.util.Objects.requireNonNull(locItem, "Array item");
                if (!java.util.List.of("repository", "artifact_set", "artifact").contains(locItem.toString())) throw new IllegalArgumentException("Schema value violates its constraints.");
            }
        }
        if (configurationSchemaId != null) {
            if (configurationSchemaId.codePointCount(0, configurationSchemaId.length()) < 1) throw new IllegalArgumentException("Schema value violates its constraints.");
        }
    }
}
