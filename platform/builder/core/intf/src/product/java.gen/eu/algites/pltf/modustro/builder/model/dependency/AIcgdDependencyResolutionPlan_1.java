package eu.algites.pltf.modustro.builder.model.dependency;

/**
 * Canonical representations: YAMLDEFS: urn:algites:modustro:builder:yamldef:model:dependency-resolution-plan:1 (src/product/yamldefs/eu/algites/pltf/modustro/builder/model/dependency/dependency-resolution-plan_1.yamldef.schema.json); JSONDEFS: urn:algites:modustro:builder:jsondef:model:dependency-resolution-plan:1 (src/product/jsondefs/eu/algites/pltf/modustro/builder/model/dependency/dependency-resolution-plan_1.jsondef.schema.json)
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:yamldef:model:dependency-resolution-plan:1/1. Source: src/product/yamldefs/eu/algites/pltf/modustro/builder/model/dependency/dependency-resolution-plan_1.yamldef.schema.json. Do not edit manually.</p>
 * @param $schema Optional absolute URI of the schema describing this root document.
 * @param dependencyKind Value of canonical property DependencyKind.
 * @param technologyKind Value of canonical property TechnologyKind.
 * @param entries Value of canonical property Entries.
 * @param diagnostics Value of canonical property Diagnostics.
 */
public record AIcgdDependencyResolutionPlan_1(
        String $schema,
        String dependencyKind,
        String technologyKind,
        java.util.List<java.util.Map<String, Object>> entries,
        java.util.List<java.util.Map<String, Object>> diagnostics) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:yamldef:model:dependency-resolution-plan:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/yamldefs/eu/algites/pltf/modustro/builder/model/dependency/dependency-resolution-plan_1.yamldef.schema.json";

    /**
     * <strong>Field Name:</strong> {@code $schema}<br/>
     * <strong>Field Description:</strong> Optional absolute URI of the schema describing this root document.
     */
    public static final String SCHEMA_FIELD_NAME__SCHEMA = "$schema";

    /**
     * <strong>Field Name:</strong> {@code DependencyKind}
     */
    public static final String SCHEMA_FIELD_NAME__DEPENDENCY_KIND = "DependencyKind";

    /**
     * <strong>Field Name:</strong> {@code TechnologyKind}
     */
    public static final String SCHEMA_FIELD_NAME__TECHNOLOGY_KIND = "TechnologyKind";

    /**
     * <strong>Field Name:</strong> {@code Entries}
     */
    public static final String SCHEMA_FIELD_NAME__ENTRIES = "Entries";

    /**
     * <strong>Field Name:</strong> {@code Diagnostics}
     */
    public static final String SCHEMA_FIELD_NAME__DIAGNOSTICS = "Diagnostics";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdDependencyResolutionPlan_1 {
        if ($schema != null) {
        }
        java.util.Objects.requireNonNull(dependencyKind, "DependencyKind");
        if (dependencyKind != null) {
            if (dependencyKind.codePointCount(0, dependencyKind.length()) < 1) throw new IllegalArgumentException("Schema value violates its constraints.");
        }
        java.util.Objects.requireNonNull(technologyKind, "TechnologyKind");
        if (technologyKind != null) {
            if (!java.util.regex.Pattern.compile("^[a-z][a-z0-9]*(?:_[a-z0-9]+)*$").matcher(technologyKind.toString()).find()) throw new IllegalArgumentException("Schema value violates its constraints.");
        }
        java.util.Objects.requireNonNull(entries, "Entries");
        if (entries != null) {
            for (var locItem : entries) {
                java.util.Objects.requireNonNull(locItem, "Array item");
            }
        }
        java.util.Objects.requireNonNull(diagnostics, "Diagnostics");
        if (diagnostics != null) {
            for (var locItem : diagnostics) {
                java.util.Objects.requireNonNull(locItem, "Array item");
            }
        }
    }
}
