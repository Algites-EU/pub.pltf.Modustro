package eu.algites.pltf.modustro.builder.model.dependency;

/**
 * Canonical representations: YAMLDEFS: urn:algites:modustro:builder:yamldef:model:version-requirement:1 (src/product/yamldefs/eu/algites/pltf/modustro/builder/model/dependency/version-requirement_1.yamldef.schema.json); JSONDEFS: urn:algites:modustro:builder:jsondef:model:version-requirement:1 (src/product/jsondefs/eu/algites/pltf/modustro/builder/model/dependency/version-requirement_1.jsondef.schema.json)
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:yamldef:model:version-requirement:1/1. Source: src/product/yamldefs/eu/algites/pltf/modustro/builder/model/dependency/version-requirement_1.yamldef.schema.json. Do not edit manually.</p>
 * @param $schema Optional absolute URI of the schema describing this root document.
 * @param exact Value of canonical property Exact.
 * @param minimum Value of canonical property Minimum.
 * @param maximum Value of canonical property Maximum.
 * @param maximumStrict Value of canonical property MaximumStrict.
 * @param exclude Value of canonical property Exclude.
 * @param prefer Value of canonical property Prefer.
 */
public record AIcgdVersionRequirement_1(
        String $schema,
        String exact,
        Object minimum,
        Object maximum,
        Boolean maximumStrict,
        Object exclude,
        String prefer) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:yamldef:model:version-requirement:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/yamldefs/eu/algites/pltf/modustro/builder/model/dependency/version-requirement_1.yamldef.schema.json";

    /**
     * <strong>Field Name:</strong> {@code $schema}<br/>
     * <strong>Field Description:</strong> Optional absolute URI of the schema describing this root document.
     */
    public static final String SCHEMA_FIELD_NAME__SCHEMA = "$schema";

    /**
     * <strong>Field Name:</strong> {@code Exact}
     */
    public static final String SCHEMA_FIELD_NAME__EXACT = "Exact";

    /**
     * <strong>Field Name:</strong> {@code Minimum}
     */
    public static final String SCHEMA_FIELD_NAME__MINIMUM = "Minimum";

    /**
     * <strong>Field Name:</strong> {@code Maximum}
     */
    public static final String SCHEMA_FIELD_NAME__MAXIMUM = "Maximum";

    /**
     * <strong>Field Name:</strong> {@code MaximumStrict}
     */
    public static final String SCHEMA_FIELD_NAME__MAXIMUM_STRICT = "MaximumStrict";

    /**
     * <strong>Field Name:</strong> {@code Exclude}
     */
    public static final String SCHEMA_FIELD_NAME__EXCLUDE = "Exclude";

    /**
     * <strong>Field Name:</strong> {@code Prefer}
     */
    public static final String SCHEMA_FIELD_NAME__PREFER = "Prefer";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdVersionRequirement_1 {
        if ($schema != null) {
        }
        if (exact != null) {
            if (exact.codePointCount(0, exact.length()) < 1) throw new IllegalArgumentException("Schema value violates its constraints.");
        }
        if (minimum != null) {
        }
        if (maximum != null) {
        }
        if (maximumStrict != null) {
        }
        if (exclude != null) {
        }
        if (prefer != null) {
            if (prefer.codePointCount(0, prefer.length()) < 1) throw new IllegalArgumentException("Schema value violates its constraints.");
        }
    }
}
