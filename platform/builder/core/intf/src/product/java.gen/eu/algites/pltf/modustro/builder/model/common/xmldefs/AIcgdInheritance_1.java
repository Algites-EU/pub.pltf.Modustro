package eu.algites.pltf.modustro.builder.model.common.xmldefs;

/**
 * Defines the generic inheritance controls used when child configuration overlays collections inherited from a parent scope.
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:model:inheritance:1/1. Source: src/product/xmldefs/eu/algites/pltf/modustro/builder/model/common/inheritance_1.xsd. Do not edit manually.</p>
 * @param items Collection item value participating in inheritance.
 */
public record AIcgdInheritance_1(
        java.util.List<Object> items) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:model:inheritance:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/xmldefs/eu/algites/pltf/modustro/builder/model/common/inheritance_1.xsd";

    /**
     * <strong>Field Name:</strong> {@code Items}<br/>
     * <strong>Field Description:</strong> Collection item value participating in inheritance.
     */
    public static final String SCHEMA_FIELD_NAME__ITEMS = "Items";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdInheritance_1 {
        if (items != null) {
            for (var locItem : items) {
                java.util.Objects.requireNonNull(locItem, "Array item");
            }
        }
    }
}
