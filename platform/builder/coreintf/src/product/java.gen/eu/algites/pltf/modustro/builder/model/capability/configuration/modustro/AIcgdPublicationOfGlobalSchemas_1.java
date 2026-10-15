package eu.algites.pltf.modustro.builder.model.capability.configuration.modustro;

/**
 * Canonical representations: YAMLDEFS: urn:algites:modustro:builder:yamldef:capability-configuration:modustro:publication-of-global-schemas:1 (src/product/yamldefs/eu/algites/pltf/modustro/builder/model/capability/configuration/modustro/publication-of-global-schemas_1.yamldef.schema.json); JSONDEFS: urn:algites:modustro:builder:jsondef:capability-configuration:modustro:publication-of-global-schemas:1 (src/product/jsondefs/eu/algites/pltf/modustro/builder/model/capability/configuration/modustro/publication-of-global-schemas_1.jsondef.schema.json)
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:yamldef:capability-configuration:modustro:publication-of-global-schemas:1/1. Source: src/product/yamldefs/eu/algites/pltf/modustro/builder/model/capability/configuration/modustro/publication-of-global-schemas_1.yamldef.schema.json. Do not edit manually.</p>
 * @param $schema Optional absolute URI of the schema describing this root document.
 * @param enabled Value of canonical property Enabled.
 * @param sourceKinds Value of canonical property SourceKinds.
 * @param additionalSourceRoots Value of canonical property AdditionalSourceRoots.
 * @param publicationDestinations Value of canonical property PublicationDestinations.
 * @param modustroSchemaVersionValidation Value of canonical property ModustroSchemaVersionValidation.
 */
public record AIcgdPublicationOfGlobalSchemas_1(
        String $schema,
        Boolean enabled,
        java.util.List<String> sourceKinds,
        java.util.List<String> additionalSourceRoots,
        java.util.List<String> publicationDestinations,
        java.util.Map<String, Object> modustroSchemaVersionValidation) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:yamldef:capability-configuration:modustro:publication-of-global-schemas:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/yamldefs/eu/algites/pltf/modustro/builder/model/capability/configuration/modustro/publication-of-global-schemas_1.yamldef.schema.json";

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
     * <strong>Field Name:</strong> {@code SourceKinds}
     */
    public static final String SCHEMA_FIELD_NAME__SOURCE_KINDS = "SourceKinds";

    /**
     * <strong>Field Name:</strong> {@code AdditionalSourceRoots}
     */
    public static final String SCHEMA_FIELD_NAME__ADDITIONAL_SOURCE_ROOTS = "AdditionalSourceRoots";

    /**
     * <strong>Field Name:</strong> {@code PublicationDestinations}
     */
    public static final String SCHEMA_FIELD_NAME__PUBLICATION_DESTINATIONS = "PublicationDestinations";

    /**
     * <strong>Field Name:</strong> {@code ModustroSchemaVersionValidation}
     */
    public static final String SCHEMA_FIELD_NAME__MODUSTRO_SCHEMA_VERSION_VALIDATION = "ModustroSchemaVersionValidation";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdPublicationOfGlobalSchemas_1 {
        if ($schema != null) {
        }
        if (enabled != null) {
        }
        if (sourceKinds != null) {
            for (var locItem : sourceKinds) {
                java.util.Objects.requireNonNull(locItem, "Array item");
                if (!java.util.List.of("yamldefs", "jsondefs", "xmldefs").contains(locItem.toString())) throw new IllegalArgumentException("Schema value violates its constraints.");
            }
        }
        if (additionalSourceRoots != null) {
            for (var locItem : additionalSourceRoots) {
                java.util.Objects.requireNonNull(locItem, "Array item");
                if (locItem.codePointCount(0, locItem.length()) < 1) throw new IllegalArgumentException("Schema value violates its constraints.");
            }
        }
        if (publicationDestinations != null) {
            for (var locItem : publicationDestinations) {
                java.util.Objects.requireNonNull(locItem, "Array item");
                if (locItem.codePointCount(0, locItem.length()) < 1) throw new IllegalArgumentException("Schema value violates its constraints.");
            }
        }
        if (modustroSchemaVersionValidation != null) {
        }
    }
}
