package eu.algites.pltf.modustro.builder.model.technology.xmldefs;

/**
 * Defines a TechnologyKind, its supported capabilities and output types, and default output choices.
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:model:technology-kind-definition:1/1. Source: src/product/xmldefs/eu/algites/pltf/modustro/builder/model/technology/technology-kind-definition_1.xsd. Do not edit manually.</p>
 * @param technologyKind Stable identifier of the TechnologyKind.
 * @param capability Capability supported by artifacts of this TechnologyKind.
 * @param buildOutputType Build output type supported by this TechnologyKind.
 * @param defaultBuildOutputType Output type produced by default when an artifact does not explicitly select build outputs.
 * @param defaultDependencyOutputType Output type requested by default when a dependency does not explicitly request an output type.
 */
public record AIcgdTechnologyKindDefinition_1(
        String technologyKind,
        java.util.List<Object> capability,
        java.util.List<Object> buildOutputType,
        java.util.List<String> defaultBuildOutputType,
        java.util.List<String> defaultDependencyOutputType) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:model:technology-kind-definition:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/xmldefs/eu/algites/pltf/modustro/builder/model/technology/technology-kind-definition_1.xsd";

    /**
     * <strong>Field Name:</strong> {@code TechnologyKind}<br/>
     * <strong>Field Description:</strong> Stable identifier of the TechnologyKind.
     */
    public static final String SCHEMA_FIELD_NAME__TECHNOLOGY_KIND = "TechnologyKind";

    /**
     * <strong>Field Name:</strong> {@code Capability}<br/>
     * <strong>Field Description:</strong> Capability supported by artifacts of this TechnologyKind.
     */
    public static final String SCHEMA_FIELD_NAME__CAPABILITY = "Capability";

    /**
     * <strong>Field Name:</strong> {@code BuildOutputType}<br/>
     * <strong>Field Description:</strong> Build output type supported by this TechnologyKind.
     */
    public static final String SCHEMA_FIELD_NAME__BUILD_OUTPUT_TYPE = "BuildOutputType";

    /**
     * <strong>Field Name:</strong> {@code DefaultBuildOutputType}<br/>
     * <strong>Field Description:</strong> Output type produced by default when an artifact does not explicitly select build outputs.
     */
    public static final String SCHEMA_FIELD_NAME__DEFAULT_BUILD_OUTPUT_TYPE = "DefaultBuildOutputType";

    /**
     * <strong>Field Name:</strong> {@code DefaultDependencyOutputType}<br/>
     * <strong>Field Description:</strong> Output type requested by default when a dependency does not explicitly request an output type.
     */
    public static final String SCHEMA_FIELD_NAME__DEFAULT_DEPENDENCY_OUTPUT_TYPE = "DefaultDependencyOutputType";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdTechnologyKindDefinition_1 {
        java.util.Objects.requireNonNull(technologyKind, "TechnologyKind");
        if (technologyKind != null) {
        }
        if (capability != null) {
            for (var locItem : capability) {
                java.util.Objects.requireNonNull(locItem, "Array item");
            }
        }
        if (buildOutputType != null) {
            for (var locItem : buildOutputType) {
                java.util.Objects.requireNonNull(locItem, "Array item");
            }
        }
        if (defaultBuildOutputType != null) {
            for (var locItem : defaultBuildOutputType) {
                java.util.Objects.requireNonNull(locItem, "Array item");
            }
        }
        if (defaultDependencyOutputType != null) {
            for (var locItem : defaultDependencyOutputType) {
                java.util.Objects.requireNonNull(locItem, "Array item");
            }
        }
    }
}
