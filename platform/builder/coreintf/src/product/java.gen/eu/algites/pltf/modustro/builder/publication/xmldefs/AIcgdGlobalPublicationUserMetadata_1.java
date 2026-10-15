package eu.algites.pltf.modustro.builder.publication.xmldefs;

/**
 * Author-controlled source metadata used as input to global definition publication. Deployment-controlled fields are not accepted.
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:publication:global-publication-user-metadata:1/1. Source: src/product/xmldefs/eu/algites/pltf/modustro/builder/publication/global-publication-user-metadata_1.xsd. Do not edit manually.</p>
 * @param globalPublicationPathId Stable logical path selected by the definition author below the canonical definition source root.
 */
public record AIcgdGlobalPublicationUserMetadata_1(
        String globalPublicationPathId) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:publication:global-publication-user-metadata:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/xmldefs/eu/algites/pltf/modustro/builder/publication/global-publication-user-metadata_1.xsd";

    /**
     * <strong>Field Name:</strong> {@code GlobalPublicationPathId}<br/>
     * <strong>Field Description:</strong> Stable logical path selected by the definition author below the canonical definition source root.
     */
    public static final String SCHEMA_FIELD_NAME__GLOBAL_PUBLICATION_PATH_ID = "GlobalPublicationPathId";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdGlobalPublicationUserMetadata_1 {
        java.util.Objects.requireNonNull(globalPublicationPathId, "GlobalPublicationPathId");
        if (globalPublicationPathId != null) {
        }
    }
}
