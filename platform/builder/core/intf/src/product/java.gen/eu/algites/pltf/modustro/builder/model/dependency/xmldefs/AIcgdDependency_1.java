package eu.algites.pltf.modustro.builder.model.dependency.xmldefs;

/**
 * Defines declared dependencies and dependency constraints before technology-specific resolution.
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:model:dependency:1/1. Source: src/product/xmldefs/eu/algites/pltf/modustro/builder/model/dependency/dependency_1.xsd. Do not edit manually.</p>
 * @param dependencies Dependency group whose items are consumed as dependencies.
 * @param dependencyConstraints Dependency group whose items contribute version constraints without necessarily being consumed.
 */
public record AIcgdDependency_1(
        java.util.List<java.util.Map<String, Object>> dependencies,
        java.util.List<java.util.Map<String, Object>> dependencyConstraints) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:model:dependency:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/xmldefs/eu/algites/pltf/modustro/builder/model/dependency/dependency_1.xsd";

    /**
     * <strong>Field Name:</strong> {@code Dependencies}<br/>
     * <strong>Field Description:</strong> Dependency group whose items are consumed as dependencies.
     */
    public static final String SCHEMA_FIELD_NAME__DEPENDENCIES = "Dependencies";

    /**
     * <strong>Field Name:</strong> {@code DependencyConstraints}<br/>
     * <strong>Field Description:</strong> Dependency group whose items contribute version constraints without necessarily being consumed.
     */
    public static final String SCHEMA_FIELD_NAME__DEPENDENCY_CONSTRAINTS = "DependencyConstraints";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdDependency_1 {
        if (dependencies != null) {
            for (var locItem : dependencies) {
                java.util.Objects.requireNonNull(locItem, "Array item");
            }
        }
        if (dependencyConstraints != null) {
            for (var locItem : dependencyConstraints) {
                java.util.Objects.requireNonNull(locItem, "Array item");
            }
        }
    }
}
