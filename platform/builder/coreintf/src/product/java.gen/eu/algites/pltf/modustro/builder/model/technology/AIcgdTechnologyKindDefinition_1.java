package eu.algites.pltf.modustro.builder.model.technology;

/**
 * Canonical representations: YAMLDEFS: urn:algites:modustro:builder:yamldef:model:technology-kind-definition:1 (src/product/yamldefs/eu/algites/pltf/modustro/builder/model/technology/technology-kind-definition_1.yamldef.schema.json); JSONDEFS: urn:algites:modustro:builder:jsondef:model:technology-kind-definition:1 (src/product/jsondefs/eu/algites/pltf/modustro/builder/model/technology/technology-kind-definition_1.jsondef.schema.json)
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:yamldef:model:technology-kind-definition:1/1. Source: src/product/yamldefs/eu/algites/pltf/modustro/builder/model/technology/technology-kind-definition_1.yamldef.schema.json. Do not edit manually.</p>
 * @param $schema Optional absolute URI of the schema describing this root document.
 * @param technologyKind Value of canonical property TechnologyKind.
 * @param capabilities Value of canonical property Capabilities.
 * @param buildOutputTypes Value of canonical property BuildOutputTypes.
 * @param defaultBuildOutputTypes Value of canonical property DefaultBuildOutputTypes.
 * @param defaultDependencyOutputTypes Value of canonical property DefaultDependencyOutputTypes.
 */
public record AIcgdTechnologyKindDefinition_1(
        String $schema,
        String technologyKind,
        java.util.List<java.util.Map<String, Object>> capabilities,
        java.util.List<java.util.Map<String, Object>> buildOutputTypes,
        java.util.List<String> defaultBuildOutputTypes,
        java.util.List<String> defaultDependencyOutputTypes) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:yamldef:model:technology-kind-definition:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/yamldefs/eu/algites/pltf/modustro/builder/model/technology/technology-kind-definition_1.yamldef.schema.json";

    /**
     * <strong>Field Name:</strong> {@code $schema}<br/>
     * <strong>Field Description:</strong> Optional absolute URI of the schema describing this root document.
     */
    public static final String SCHEMA_FIELD_NAME__SCHEMA = "$schema";

    /**
     * <strong>Field Name:</strong> {@code TechnologyKind}
     */
    public static final String SCHEMA_FIELD_NAME__TECHNOLOGY_KIND = "TechnologyKind";

    /**
     * <strong>Field Name:</strong> {@code Capabilities}
     */
    public static final String SCHEMA_FIELD_NAME__CAPABILITIES = "Capabilities";

    /**
     * <strong>Field Name:</strong> {@code BuildOutputTypes}
     */
    public static final String SCHEMA_FIELD_NAME__BUILD_OUTPUT_TYPES = "BuildOutputTypes";

    /**
     * <strong>Field Name:</strong> {@code DefaultBuildOutputTypes}
     */
    public static final String SCHEMA_FIELD_NAME__DEFAULT_BUILD_OUTPUT_TYPES = "DefaultBuildOutputTypes";

    /**
     * <strong>Field Name:</strong> {@code DefaultDependencyOutputTypes}
     */
    public static final String SCHEMA_FIELD_NAME__DEFAULT_DEPENDENCY_OUTPUT_TYPES = "DefaultDependencyOutputTypes";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdTechnologyKindDefinition_1 {
        if ($schema != null) {
        }
        java.util.Objects.requireNonNull(technologyKind, "TechnologyKind");
        if (technologyKind != null) {
            if (!java.util.regex.Pattern.compile("^[a-z][a-z0-9]*(?:_[a-z0-9]+)*$").matcher(technologyKind.toString()).find()) throw new IllegalArgumentException("Schema value violates its constraints.");
        }
        java.util.Objects.requireNonNull(capabilities, "Capabilities");
        if (capabilities != null) {
            for (var locItem : capabilities) {
                java.util.Objects.requireNonNull(locItem, "Array item");
            }
        }
        java.util.Objects.requireNonNull(buildOutputTypes, "BuildOutputTypes");
        if (buildOutputTypes != null) {
            for (var locItem : buildOutputTypes) {
                java.util.Objects.requireNonNull(locItem, "Array item");
            }
        }
        java.util.Objects.requireNonNull(defaultBuildOutputTypes, "DefaultBuildOutputTypes");
        if (defaultBuildOutputTypes != null) {
            for (var locItem : defaultBuildOutputTypes) {
                java.util.Objects.requireNonNull(locItem, "Array item");
                if (locItem.codePointCount(0, locItem.length()) < 1) throw new IllegalArgumentException("Schema value violates its constraints.");
            }
        }
        java.util.Objects.requireNonNull(defaultDependencyOutputTypes, "DefaultDependencyOutputTypes");
        if (defaultDependencyOutputTypes != null) {
            for (var locItem : defaultDependencyOutputTypes) {
                java.util.Objects.requireNonNull(locItem, "Array item");
                if (locItem.codePointCount(0, locItem.length()) < 1) throw new IllegalArgumentException("Schema value violates its constraints.");
            }
        }
    }
}
