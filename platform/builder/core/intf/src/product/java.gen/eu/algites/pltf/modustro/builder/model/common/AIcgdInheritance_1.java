package eu.algites.pltf.modustro.builder.model.common;

/**
 * Canonical representations: YAMLDEFS: urn:algites:modustro:builder:yamldef:model:inheritance:1 (src/product/yamldefs/eu/algites/pltf/modustro/builder/model/common/inheritance_1.yamldef.schema.json); JSONDEFS: urn:algites:modustro:builder:jsondef:model:inheritance:1 (src/product/jsondefs/eu/algites/pltf/modustro/builder/model/common/inheritance_1.jsondef.schema.json)
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:yamldef:model:inheritance:1/1. Source: src/product/yamldefs/eu/algites/pltf/modustro/builder/model/common/inheritance_1.yamldef.schema.json. Do not edit manually.</p>
 * @param $schema Optional absolute URI of the schema describing this root document.
 * @param itemsInheritancePolicy Value of canonical property ItemsInheritancePolicy.
 * @param items Value of canonical property Items.
 */
public record AIcgdInheritance_1(
        String $schema,
        String itemsInheritancePolicy,
        java.util.List<Object> items) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:yamldef:model:inheritance:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/yamldefs/eu/algites/pltf/modustro/builder/model/common/inheritance_1.yamldef.schema.json";

    /**
     * <strong>Field Name:</strong> {@code $schema}<br/>
     * <strong>Field Description:</strong> Optional absolute URI of the schema describing this root document.
     */
    public static final String SCHEMA_FIELD_NAME__SCHEMA = "$schema";

    /**
     * <strong>Field Name:</strong> {@code ItemsInheritancePolicy}
     */
    public static final String SCHEMA_FIELD_NAME__ITEMS_INHERITANCE_POLICY = "ItemsInheritancePolicy";

    /**
     * <strong>Field Name:</strong> {@code Items}
     */
    public static final String SCHEMA_FIELD_NAME__ITEMS = "Items";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdInheritance_1 {
        if ($schema != null) {
        }
        if (itemsInheritancePolicy != null) {
            if (!java.util.List.of("merge_missing_items", "remove_missing_items").contains(itemsInheritancePolicy.toString())) throw new IllegalArgumentException("Schema value violates its constraints.");
        }
        java.util.Objects.requireNonNull(items, "Items");
        if (items != null) {
            for (var locItem : items) {
                java.util.Objects.requireNonNull(locItem, "Array item");
            }
        }
    }
}
