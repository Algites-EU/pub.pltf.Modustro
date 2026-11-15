package eu.algites.pltf.modustro.builder.common;

/**
 * Common technical metadata accepted by Algites/Modustro YAML and JSON root documents. Canonical representations: YAMLDEFS: https://defs.dev.algites.eu/api/yamldefs/eu/algites/pltf/modustro/builder/common/document-common-metadata_1.yamldef.schema.json (src/product/yamldefs/eu/algites/pltf/modustro/builder/common/document-common-metadata_1.yamldef.schema.json); JSONDEFS: https://defs.dev.algites.eu/api/jsondefs/eu/algites/pltf/modustro/builder/common/document-common-metadata_1.jsondef.schema.json (src/product/jsondefs/eu/algites/pltf/modustro/builder/common/document-common-metadata_1.jsondef.schema.json)
 *
 * <p>Generated from canonical definition https://defs.dev.algites.eu/api/yamldefs/eu/algites/pltf/modustro/builder/common/document-common-metadata_1.yamldef.schema.json/1. Source: src/product/yamldefs/eu/algites/pltf/modustro/builder/common/document-common-metadata_1.yamldef.schema.json. Do not edit manually.</p>
 * @param $schema Optional absolute URI of the schema describing this root document.
 */
public record AIcgdDocumentCommonMetadata_1(
        String $schema) {
    public static final String CANONICAL_SOURCE_ID = "https://defs.dev.algites.eu/api/yamldefs/eu/algites/pltf/modustro/builder/common/document-common-metadata_1.yamldef.schema.json";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/yamldefs/eu/algites/pltf/modustro/builder/common/document-common-metadata_1.yamldef.schema.json";

    /**
     * <strong>Field Name:</strong> {@code $schema}<br/>
     * <strong>Field Description:</strong> Optional absolute URI of the schema describing this root document.
     */
    public static final String SCHEMA_FIELD_NAME__SCHEMA = "$schema";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdDocumentCommonMetadata_1 {
        if ($schema != null) {
        }
    }
}
