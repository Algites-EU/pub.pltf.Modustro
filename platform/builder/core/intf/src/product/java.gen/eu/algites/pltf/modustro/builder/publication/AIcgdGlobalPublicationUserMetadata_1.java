package eu.algites.pltf.modustro.builder.publication;

/**
 * Author-controlled source metadata used as input to global definition publication. Only the logical publication path is accepted; deployment-controlled fields are deliberately forbidden. Canonical representations: YAMLDEFS: urn:algites:modustro:builder:yamldef:publication:global-publication-user-metadata:1 (src/product/yamldefs/eu/algites/pltf/modustro/builder/publication/global-publication-user-metadata_1.yamldef.schema.json); JSONDEFS: urn:algites:modustro:builder:jsondef:publication:global-publication-user-metadata:1 (src/product/jsondefs/eu/algites/pltf/modustro/builder/publication/global-publication-user-metadata_1.jsondef.schema.json)
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:yamldef:publication:global-publication-user-metadata:1/1. Source: src/product/yamldefs/eu/algites/pltf/modustro/builder/publication/global-publication-user-metadata_1.yamldef.schema.json. Do not edit manually.</p>
 * @param $schema Optional absolute URI of the schema describing this root document.
 * @param globalPublicationPathId Stable logical path selected by the definition author below the canonical definition source root.
 */
public record AIcgdGlobalPublicationUserMetadata_1(
        String $schema,
        String globalPublicationPathId) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:yamldef:publication:global-publication-user-metadata:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/yamldefs/eu/algites/pltf/modustro/builder/publication/global-publication-user-metadata_1.yamldef.schema.json";

    /**
     * <strong>Field Name:</strong> {@code $schema}<br/>
     * <strong>Field Description:</strong> Optional absolute URI of the schema describing this root document.
     */
    public static final String SCHEMA_FIELD_NAME__SCHEMA = "$schema";

    /**
     * <strong>Field Name:</strong> {@code GlobalPublicationPathId}<br/>
     * <strong>Field Description:</strong> Stable logical path selected by the definition author below the canonical definition source root.
     */
    public static final String SCHEMA_FIELD_NAME__GLOBAL_PUBLICATION_PATH_ID = "GlobalPublicationPathId";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdGlobalPublicationUserMetadata_1 {
        if ($schema != null) {
        }
        java.util.Objects.requireNonNull(globalPublicationPathId, "GlobalPublicationPathId");
        if (globalPublicationPathId != null) {
            if (globalPublicationPathId.codePointCount(0, globalPublicationPathId.length()) < 1) throw new IllegalArgumentException("Schema value violates its constraints.");
            if (!java.util.regex.Pattern.compile("^(?!/)(?!.*(?:^|/)\\.\\.(?:/|$))(?!.*//)[A-Za-z0-9._/-]+$").matcher(globalPublicationPathId.toString()).find()) throw new IllegalArgumentException("Schema value violates its constraints.");
        }
    }
}
