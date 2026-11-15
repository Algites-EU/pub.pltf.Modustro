package eu.algites.pltf.modustro.builder.model.dependency.xmldefs;

/**
 * Defines the resolved technology-specific dependency plan produced by Modustro Builder, including native usage mapping, version requirements, required outputs, and diagnostics.
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:model:dependency-resolution-plan:1/1. Source: src/product/xmldefs/eu/algites/pltf/modustro/builder/model/dependency/dependency-resolution-plan_1.xsd. Do not edit manually.</p>
 * @param entry Resolved dependency entry contained in the plan.
 * @param diagnostic Diagnostic produced while creating the plan.
 */
public record AIcgdDependencyResolutionPlan_1(
        java.util.List<java.util.Map<String, Object>> entry,
        java.util.List<java.util.Map<String, Object>> diagnostic) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:model:dependency-resolution-plan:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/xmldefs/eu/algites/pltf/modustro/builder/model/dependency/dependency-resolution-plan_1.xsd";

    /**
     * <strong>Field Name:</strong> {@code Entry}<br/>
     * <strong>Field Description:</strong> Resolved dependency entry contained in the plan.
     */
    public static final String SCHEMA_FIELD_NAME__ENTRY = "Entry";

    /**
     * <strong>Field Name:</strong> {@code Diagnostic}<br/>
     * <strong>Field Description:</strong> Diagnostic produced while creating the plan.
     */
    public static final String SCHEMA_FIELD_NAME__DIAGNOSTIC = "Diagnostic";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdDependencyResolutionPlan_1 {
        if (entry != null) {
            for (var locItem : entry) {
                java.util.Objects.requireNonNull(locItem, "Array item");
            }
        }
        if (diagnostic != null) {
            for (var locItem : diagnostic) {
                java.util.Objects.requireNonNull(locItem, "Array item");
            }
        }
    }
}
