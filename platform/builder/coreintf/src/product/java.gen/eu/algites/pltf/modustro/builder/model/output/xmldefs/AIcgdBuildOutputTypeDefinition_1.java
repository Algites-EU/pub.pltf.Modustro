package eu.algites.pltf.modustro.builder.model.output.xmldefs;

/**
 * Defines one build output type and how that output may be produced or consumed by dependencies.
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:model:build-output-type-definition:1/1. Source: src/product/xmldefs/eu/algites/pltf/modustro/builder/model/output/build-output-type-definition_1.xsd. Do not edit manually.</p>
 * @param buildOutputType Stable identifier of the build output type.
 * @param canBeProduced Whether Builder may produce this output type for an artifact.
 * @param canBeUsedInDependency Whether another artifact may request this output type from a dependency.
 * @param dependencyOutputAlternative Alternative output type that may satisfy a dependency request when this output is unavailable.
 * @param configurationSchemaId Optional canonical schema identifier for configuration specific to this output type.
 */
public record AIcgdBuildOutputTypeDefinition_1(
        String buildOutputType,
        Boolean canBeProduced,
        Boolean canBeUsedInDependency,
        java.util.List<String> dependencyOutputAlternative,
        String configurationSchemaId) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:model:build-output-type-definition:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/xmldefs/eu/algites/pltf/modustro/builder/model/output/build-output-type-definition_1.xsd";

    /**
     * <strong>Field Name:</strong> {@code BuildOutputType}<br/>
     * <strong>Field Description:</strong> Stable identifier of the build output type.
     */
    public static final String SCHEMA_FIELD_NAME__BUILD_OUTPUT_TYPE = "BuildOutputType";

    /**
     * <strong>Field Name:</strong> {@code CanBeProduced}<br/>
     * <strong>Field Description:</strong> Whether Builder may produce this output type for an artifact.
     */
    public static final String SCHEMA_FIELD_NAME__CAN_BE_PRODUCED = "CanBeProduced";

    /**
     * <strong>Field Name:</strong> {@code CanBeUsedInDependency}<br/>
     * <strong>Field Description:</strong> Whether another artifact may request this output type from a dependency.
     */
    public static final String SCHEMA_FIELD_NAME__CAN_BE_USED_IN_DEPENDENCY = "CanBeUsedInDependency";

    /**
     * <strong>Field Name:</strong> {@code DependencyOutputAlternative}<br/>
     * <strong>Field Description:</strong> Alternative output type that may satisfy a dependency request when this output is unavailable.
     */
    public static final String SCHEMA_FIELD_NAME__DEPENDENCY_OUTPUT_ALTERNATIVE = "DependencyOutputAlternative";

    /**
     * <strong>Field Name:</strong> {@code ConfigurationSchemaId}<br/>
     * <strong>Field Description:</strong> Optional canonical schema identifier for configuration specific to this output type.
     */
    public static final String SCHEMA_FIELD_NAME__CONFIGURATION_SCHEMA_ID = "ConfigurationSchemaId";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdBuildOutputTypeDefinition_1 {
        java.util.Objects.requireNonNull(buildOutputType, "BuildOutputType");
        if (buildOutputType != null) {
        }
        java.util.Objects.requireNonNull(canBeProduced, "CanBeProduced");
        if (canBeProduced != null) {
        }
        java.util.Objects.requireNonNull(canBeUsedInDependency, "CanBeUsedInDependency");
        if (canBeUsedInDependency != null) {
        }
        if (dependencyOutputAlternative != null) {
            for (var locItem : dependencyOutputAlternative) {
                java.util.Objects.requireNonNull(locItem, "Array item");
            }
        }
        if (configurationSchemaId != null) {
        }
    }
}
