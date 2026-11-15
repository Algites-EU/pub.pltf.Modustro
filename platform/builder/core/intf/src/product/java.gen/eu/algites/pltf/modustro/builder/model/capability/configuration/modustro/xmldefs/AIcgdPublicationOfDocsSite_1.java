package eu.algites.pltf.modustro.builder.model.capability.configuration.modustro.xmldefs;

/**
 * Documentation-site publication capability configuration.
 *
 * <p>Generated from canonical definition publication-of-docs-site/1. Source: src/product/xmldefs/eu/algites/pltf/modustro/builder/model/capability/configuration/modustro/publication-of-docs-site_1.xsd. Do not edit manually.</p>
 * @param enabled Whether documentation-site publication is enabled.
 * @param publicationDestinations PublicationEndpoint identifiers selected as publication destinations for the documentation site.
 */
public record AIcgdPublicationOfDocsSite_1(
        Boolean enabled,
        Object publicationDestinations) {
    public static final String CANONICAL_SOURCE_ID = "publication-of-docs-site";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/xmldefs/eu/algites/pltf/modustro/builder/model/capability/configuration/modustro/publication-of-docs-site_1.xsd";

    /**
     * <strong>Field Name:</strong> {@code Enabled}<br/>
     * <strong>Field Description:</strong> Whether documentation-site publication is enabled.
     */
    public static final String SCHEMA_FIELD_NAME__ENABLED = "Enabled";

    /**
     * <strong>Field Name:</strong> {@code PublicationDestinations}<br/>
     * <strong>Field Description:</strong> PublicationEndpoint identifiers selected as publication destinations for the documentation site.
     */
    public static final String SCHEMA_FIELD_NAME__PUBLICATION_DESTINATIONS = "PublicationDestinations";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdPublicationOfDocsSite_1 {
        if (enabled != null) {
        }
        if (publicationDestinations != null) {
        }
    }
}
