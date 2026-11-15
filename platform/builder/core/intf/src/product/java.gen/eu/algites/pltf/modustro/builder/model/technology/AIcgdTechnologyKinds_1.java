package eu.algites.pltf.modustro.builder.model.technology;

/**
 * Canonical representations: YAMLDEFS: urn:algites:modustro:builder:yamldef:model:technology-kinds:1 (src/product/yamldefs/eu/algites/pltf/modustro/builder/model/technology/technology-kinds_1.yamldef.schema.json); JSONDEFS: urn:algites:modustro:builder:jsondef:model:technology-kinds:1 (src/product/jsondefs/eu/algites/pltf/modustro/builder/model/technology/technology-kinds_1.jsondef.schema.json)
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:yamldef:model:technology-kinds:1/1. Source: src/product/yamldefs/eu/algites/pltf/modustro/builder/model/technology/technology-kinds_1.yamldef.schema.json. Do not edit manually.</p>
 * @param value Value of canonical property value.
 */
public record AIcgdTechnologyKinds_1(
        Object value) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:yamldef:model:technology-kinds:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/yamldefs/eu/algites/pltf/modustro/builder/model/technology/technology-kinds_1.yamldef.schema.json";

    /**
     * <strong>Field Name:</strong> {@code value}
     */
    public static final String SCHEMA_FIELD_NAME__VALUE = "value";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdTechnologyKinds_1 {
        java.util.Objects.requireNonNull(value, "value");
        if (value != null) {
        }
    }
}
