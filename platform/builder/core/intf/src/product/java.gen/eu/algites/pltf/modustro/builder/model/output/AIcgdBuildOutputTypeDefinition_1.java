package eu.algites.pltf.modustro.builder.model.output;

/**
 * Canonical representations: YAMLDEFS: urn:algites:modustro:builder:yamldef:model:build-output-type-definition:1 (src/product/yamldefs/eu/algites/pltf/modustro/builder/model/output/build-output-type-definition_1.yamldef.schema.json); JSONDEFS: urn:algites:modustro:builder:jsondef:model:build-output-type-definition:1 (src/product/jsondefs/eu/algites/pltf/modustro/builder/model/output/build-output-type-definition_1.jsondef.schema.json)
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:yamldef:model:build-output-type-definition:1/1. Source: src/product/yamldefs/eu/algites/pltf/modustro/builder/model/output/build-output-type-definition_1.yamldef.schema.json. Do not edit manually.</p>
 * @param $schema Optional absolute URI of the schema describing this root document.
 * @param buildOutputType Value of canonical property BuildOutputType.
 * @param canBeProduced Value of canonical property CanBeProduced.
 * @param canBeUsedInDependency Value of canonical property CanBeUsedInDependency.
 * @param dependencyOutputAlternatives Value of canonical property DependencyOutputAlternatives.
 * @param configurationSchemaId Value of canonical property ConfigurationSchemaId.
 */
public record AIcgdBuildOutputTypeDefinition_1(
        String $schema,
        String buildOutputType,
        Boolean canBeProduced,
        Boolean canBeUsedInDependency,
        java.util.List<String> dependencyOutputAlternatives,
        String configurationSchemaId) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:yamldef:model:build-output-type-definition:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/yamldefs/eu/algites/pltf/modustro/builder/model/output/build-output-type-definition_1.yamldef.schema.json";

    /**
     * <strong>Field Name:</strong> {@code $schema}<br/>
     * <strong>Field Description:</strong> Optional absolute URI of the schema describing this root document.
     */
    public static final String SCHEMA_FIELD_NAME__SCHEMA = "$schema";

    /**
     * <strong>Field Name:</strong> {@code BuildOutputType}
     */
    public static final String SCHEMA_FIELD_NAME__BUILD_OUTPUT_TYPE = "BuildOutputType";

    /**
     * <strong>Field Name:</strong> {@code CanBeProduced}
     */
    public static final String SCHEMA_FIELD_NAME__CAN_BE_PRODUCED = "CanBeProduced";

    /**
     * <strong>Field Name:</strong> {@code CanBeUsedInDependency}
     */
    public static final String SCHEMA_FIELD_NAME__CAN_BE_USED_IN_DEPENDENCY = "CanBeUsedInDependency";

    /**
     * <strong>Field Name:</strong> {@code DependencyOutputAlternatives}
     */
    public static final String SCHEMA_FIELD_NAME__DEPENDENCY_OUTPUT_ALTERNATIVES = "DependencyOutputAlternatives";

    /**
     * <strong>Field Name:</strong> {@code ConfigurationSchemaId}
     */
    public static final String SCHEMA_FIELD_NAME__CONFIGURATION_SCHEMA_ID = "ConfigurationSchemaId";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdBuildOutputTypeDefinition_1 {
        if ($schema != null) {
        }
        java.util.Objects.requireNonNull(buildOutputType, "BuildOutputType");
        if (buildOutputType != null) {
            if (!java.util.regex.Pattern.compile("^[a-z][a-z0-9]*(?:_[a-z0-9]+)*$").matcher(buildOutputType.toString()).find()) throw new IllegalArgumentException("Schema value violates its constraints.");
        }
        java.util.Objects.requireNonNull(canBeProduced, "CanBeProduced");
        if (canBeProduced != null) {
        }
        java.util.Objects.requireNonNull(canBeUsedInDependency, "CanBeUsedInDependency");
        if (canBeUsedInDependency != null) {
        }
        if (dependencyOutputAlternatives != null) {
            for (var locItem : dependencyOutputAlternatives) {
                java.util.Objects.requireNonNull(locItem, "Array item");
                if (!java.util.regex.Pattern.compile("^[a-z][a-z0-9]*(?:_[a-z0-9]+)*$").matcher(locItem.toString()).find()) throw new IllegalArgumentException("Schema value violates its constraints.");
            }
        }
        if (configurationSchemaId != null) {
            if (configurationSchemaId.codePointCount(0, configurationSchemaId.length()) < 1) throw new IllegalArgumentException("Schema value violates its constraints.");
        }
    }
}
