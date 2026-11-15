package eu.algites.pltf.modustro.builder.model.capability.configuration.modustro.xmldefs;

/**
 * Global-definition publication capability configuration.
 *
 * <p>Generated from canonical definition publication-of-global-schemas/1. Source: src/product/xmldefs/eu/algites/pltf/modustro/builder/model/capability/configuration/modustro/publication-of-global-schemas_1.xsd. Do not edit manually.</p>
 * @param enabled Whether global definition publication is enabled.
 * @param sourceKinds Canonical definition source kinds included in publication.
 * @param additionalSourceRoots Additional canonical definition roots to include beyond standard source roots.
 * @param publicationDestinations PublicationEndpoint identifiers selected as global-definition publication destinations.
 * @param modustroSchemaVersionValidation Controls validation of Modustro schema-definition versions before publication.
 */
public record AIcgdPublicationOfGlobalSchemas_1(
        Boolean enabled,
        Object sourceKinds,
        Object additionalSourceRoots,
        Object publicationDestinations,
        Object modustroSchemaVersionValidation) {
    public static final String CANONICAL_SOURCE_ID = "publication-of-global-schemas";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/xmldefs/eu/algites/pltf/modustro/builder/model/capability/configuration/modustro/publication-of-global-schemas_1.xsd";

    /**
     * <strong>Field Name:</strong> {@code Enabled}<br/>
     * <strong>Field Description:</strong> Whether global definition publication is enabled.
     */
    public static final String SCHEMA_FIELD_NAME__ENABLED = "Enabled";

    /**
     * <strong>Field Name:</strong> {@code SourceKinds}<br/>
     * <strong>Field Description:</strong> Canonical definition source kinds included in publication.
     */
    public static final String SCHEMA_FIELD_NAME__SOURCE_KINDS = "SourceKinds";

    /**
     * <strong>Field Name:</strong> {@code AdditionalSourceRoots}<br/>
     * <strong>Field Description:</strong> Additional canonical definition roots to include beyond standard source roots.
     */
    public static final String SCHEMA_FIELD_NAME__ADDITIONAL_SOURCE_ROOTS = "AdditionalSourceRoots";

    /**
     * <strong>Field Name:</strong> {@code PublicationDestinations}<br/>
     * <strong>Field Description:</strong> PublicationEndpoint identifiers selected as global-definition publication destinations.
     */
    public static final String SCHEMA_FIELD_NAME__PUBLICATION_DESTINATIONS = "PublicationDestinations";

    /**
     * <strong>Field Name:</strong> {@code ModustroSchemaVersionValidation}<br/>
     * <strong>Field Description:</strong> Controls validation of Modustro schema-definition versions before publication.
     */
    public static final String SCHEMA_FIELD_NAME__MODUSTRO_SCHEMA_VERSION_VALIDATION = "ModustroSchemaVersionValidation";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdPublicationOfGlobalSchemas_1 {
        if (enabled != null) {
        }
        if (sourceKinds != null) {
        }
        if (additionalSourceRoots != null) {
        }
        if (publicationDestinations != null) {
        }
        if (modustroSchemaVersionValidation != null) {
        }
    }
}
