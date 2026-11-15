package eu.algites.pltf.modustro.builder.model.capability.configuration.modustro.xmldefs;

/**
 * Documentation-site content capability configuration.
 *
 * <p>Generated from canonical definition docs-site-content/1. Source: src/product/xmldefs/eu/algites/pltf/modustro/builder/model/capability/configuration/modustro/docs-site-content_1.xsd. Do not edit manually.</p>
 * @param enabled Whether this artifact contributes content to the documentation site.
 */
public record AIcgdDocsSiteContent_1(
        Boolean enabled) {
    public static final String CANONICAL_SOURCE_ID = "docs-site-content";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/xmldefs/eu/algites/pltf/modustro/builder/model/capability/configuration/modustro/docs-site-content_1.xsd";

    /**
     * <strong>Field Name:</strong> {@code Enabled}<br/>
     * <strong>Field Description:</strong> Whether this artifact contributes content to the documentation site.
     */
    public static final String SCHEMA_FIELD_NAME__ENABLED = "Enabled";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdDocsSiteContent_1 {
        if (enabled != null) {
        }
    }
}
