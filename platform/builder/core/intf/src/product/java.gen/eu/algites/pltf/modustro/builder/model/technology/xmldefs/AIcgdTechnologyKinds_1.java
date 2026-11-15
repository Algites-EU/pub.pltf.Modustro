package eu.algites.pltf.modustro.builder.model.technology.xmldefs;

/**
 * Configures TechnologyKinds selected for an artifact, including per-technology output and capability overrides. XML uses the extended representation; YAML and JSON also support the compact string-list representation.
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:model:technology-kinds:1/1. Source: src/product/xmldefs/eu/algites/pltf/modustro/builder/model/technology/technology-kinds_1.xsd. Do not edit manually.</p>
 * @param item One selected TechnologyKind entry.
 */
public record AIcgdTechnologyKinds_1(
        java.util.List<java.util.Map<String, Object>> item) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:model:technology-kinds:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/xmldefs/eu/algites/pltf/modustro/builder/model/technology/technology-kinds_1.xsd";

    /**
     * <strong>Field Name:</strong> {@code Item}<br/>
     * <strong>Field Description:</strong> One selected TechnologyKind entry.
     */
    public static final String SCHEMA_FIELD_NAME__ITEM = "Item";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdTechnologyKinds_1 {
        if (item != null) {
            for (var locItem : item) {
                java.util.Objects.requireNonNull(locItem, "Array item");
            }
        }
    }
}
