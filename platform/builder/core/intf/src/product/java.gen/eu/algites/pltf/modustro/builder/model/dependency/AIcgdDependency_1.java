package eu.algites.pltf.modustro.builder.model.dependency;

/**
 * Canonical representations: YAMLDEFS: urn:algites:modustro:builder:yamldef:model:dependency:1 (src/product/yamldefs/eu/algites/pltf/modustro/builder/model/dependency/dependency_1.yamldef.schema.json); JSONDEFS: urn:algites:modustro:builder:jsondef:model:dependency:1 (src/product/jsondefs/eu/algites/pltf/modustro/builder/model/dependency/dependency_1.jsondef.schema.json)
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:yamldef:model:dependency:1/1. Source: src/product/yamldefs/eu/algites/pltf/modustro/builder/model/dependency/dependency_1.yamldef.schema.json. Do not edit manually.</p>
 * @param $schema Optional absolute URI of the schema describing this root document.
 * @param dependencies Value of canonical property Dependencies.
 * @param dependencyConstraints Value of canonical property DependencyConstraints.
 */
public record AIcgdDependency_1(
        String $schema,
        java.util.List<java.util.Map<String, Object>> dependencies,
        java.util.List<java.util.Map<String, Object>> dependencyConstraints) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:yamldef:model:dependency:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/yamldefs/eu/algites/pltf/modustro/builder/model/dependency/dependency_1.yamldef.schema.json";

    /**
     * <strong>Field Name:</strong> {@code $schema}<br/>
     * <strong>Field Description:</strong> Optional absolute URI of the schema describing this root document.
     */
    public static final String SCHEMA_FIELD_NAME__SCHEMA = "$schema";

    /**
     * <strong>Field Name:</strong> {@code Dependencies}
     */
    public static final String SCHEMA_FIELD_NAME__DEPENDENCIES = "Dependencies";

    /**
     * <strong>Field Name:</strong> {@code DependencyConstraints}
     */
    public static final String SCHEMA_FIELD_NAME__DEPENDENCY_CONSTRAINTS = "DependencyConstraints";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdDependency_1 {
        if ($schema != null) {
        }
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
