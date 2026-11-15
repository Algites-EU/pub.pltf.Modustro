package eu.algites.pltf.modustro.builder.model.capability.configuration.modustro;

/**
 * Canonical representations: YAMLDEFS: urn:algites:modustro:builder:yamldef:capability-configuration:modustro:publication-of-docs-site:1 (src/product/yamldefs/eu/algites/pltf/modustro/builder/model/capability/configuration/modustro/publication-of-docs-site_1.yamldef.schema.json); JSONDEFS: urn:algites:modustro:builder:jsondef:capability-configuration:modustro:publication-of-docs-site:1 (src/product/jsondefs/eu/algites/pltf/modustro/builder/model/capability/configuration/modustro/publication-of-docs-site_1.jsondef.schema.json)
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:yamldef:capability-configuration:modustro:publication-of-docs-site:1/1. Source: src/product/yamldefs/eu/algites/pltf/modustro/builder/model/capability/configuration/modustro/publication-of-docs-site_1.yamldef.schema.json. Do not edit manually.</p>
 * @param $schema Optional absolute URI of the schema describing this root document.
 * @param enabled Value of canonical property Enabled.
 * @param publicationDestinations Value of canonical property PublicationDestinations.
 */
public record AIcgdPublicationOfDocsSite_1(
        String $schema,
        Boolean enabled,
        java.util.List<String> publicationDestinations) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:yamldef:capability-configuration:modustro:publication-of-docs-site:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/yamldefs/eu/algites/pltf/modustro/builder/model/capability/configuration/modustro/publication-of-docs-site_1.yamldef.schema.json";

    /**
     * <strong>Field Name:</strong> {@code $schema}<br/>
     * <strong>Field Description:</strong> Optional absolute URI of the schema describing this root document.
     */
    public static final String SCHEMA_FIELD_NAME__SCHEMA = "$schema";

    /**
     * <strong>Field Name:</strong> {@code Enabled}
     */
    public static final String SCHEMA_FIELD_NAME__ENABLED = "Enabled";

    /**
     * <strong>Field Name:</strong> {@code PublicationDestinations}
     */
    public static final String SCHEMA_FIELD_NAME__PUBLICATION_DESTINATIONS = "PublicationDestinations";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdPublicationOfDocsSite_1 {
        if ($schema != null) {
        }
        if (enabled != null) {
        }
        if (publicationDestinations != null) {
            for (var locItem : publicationDestinations) {
                java.util.Objects.requireNonNull(locItem, "Array item");
                if (locItem.codePointCount(0, locItem.length()) < 1) throw new IllegalArgumentException("Schema value violates its constraints.");
            }
        }
    }
}
