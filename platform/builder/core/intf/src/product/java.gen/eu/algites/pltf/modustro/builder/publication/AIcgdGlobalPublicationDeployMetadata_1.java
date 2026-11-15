package eu.algites.pltf.modustro.builder.publication;

/**
 * Deployment-controlled publication metadata generated from validated user metadata plus trusted publisher state. This document is not an author input contract. Canonical representations: YAMLDEFS: urn:algites:modustro:builder:yamldef:publication:global-publication-deploy-metadata:1 (src/product/yamldefs/eu/algites/pltf/modustro/builder/publication/global-publication-deploy-metadata_1.yamldef.schema.json); JSONDEFS: urn:algites:modustro:builder:jsondef:publication:global-publication-deploy-metadata:1 (src/product/jsondefs/eu/algites/pltf/modustro/builder/publication/global-publication-deploy-metadata_1.jsondef.schema.json)
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:yamldef:publication:global-publication-deploy-metadata:1/1. Source: src/product/yamldefs/eu/algites/pltf/modustro/builder/publication/global-publication-deploy-metadata_1.yamldef.schema.json. Do not edit manually.</p>
 * @param $schema Optional absolute URI of the schema describing this root document.
 * @param globalPublicationPathId Stable logical publication path copied from validated user metadata.
 * @param publicationState Server-controlled publication lifecycle state. Released definitions are immutable and cannot transition back to draft.
 * @param publicationRevision Server-controlled monotonically increasing revision of successfully deployed draft content. Release freezes the current content revision.
 * @param firstPublishedAt Server-generated timestamp of the first successful deployment for this GlobalPublicationPathId.
 * @param publishedAt Server-generated timestamp of the most recent successful deployment or release transition represented by this metadata.
 * @param releasedAt Server-generated timestamp at which this publication became an immutable release; absent while the publication is a draft.
 * @param publishedBy Optional trusted publisher identity derived by the deployment adapter from authenticated execution context; never copied from user metadata.
 * @param contentSha256 SHA-256 of the exact deployed canonical content, used to reject incomplete or inconsistent definition/sidecar pairs.
 */
public record AIcgdGlobalPublicationDeployMetadata_1(
        String $schema,
        String globalPublicationPathId,
        String publicationState,
        java.math.BigInteger publicationRevision,
        javax.xml.datatype.XMLGregorianCalendar firstPublishedAt,
        javax.xml.datatype.XMLGregorianCalendar publishedAt,
        javax.xml.datatype.XMLGregorianCalendar releasedAt,
        String publishedBy,
        String contentSha256) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:yamldef:publication:global-publication-deploy-metadata:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/yamldefs/eu/algites/pltf/modustro/builder/publication/global-publication-deploy-metadata_1.yamldef.schema.json";

    /**
     * <strong>Field Name:</strong> {@code $schema}<br/>
     * <strong>Field Description:</strong> Optional absolute URI of the schema describing this root document.
     */
    public static final String SCHEMA_FIELD_NAME__SCHEMA = "$schema";

    /**
     * <strong>Field Name:</strong> {@code GlobalPublicationPathId}<br/>
     * <strong>Field Description:</strong> Stable logical publication path copied from validated user metadata.
     */
    public static final String SCHEMA_FIELD_NAME__GLOBAL_PUBLICATION_PATH_ID = "GlobalPublicationPathId";

    /**
     * <strong>Field Name:</strong> {@code PublicationState}<br/>
     * <strong>Field Description:</strong> Server-controlled publication lifecycle state. Released definitions are immutable and cannot transition back to draft.
     */
    public static final String SCHEMA_FIELD_NAME__PUBLICATION_STATE = "PublicationState";

    /**
     * <strong>Field Name:</strong> {@code PublicationRevision}<br/>
     * <strong>Field Description:</strong> Server-controlled monotonically increasing revision of successfully deployed draft content. Release freezes the current content revision.
     */
    public static final String SCHEMA_FIELD_NAME__PUBLICATION_REVISION = "PublicationRevision";

    /**
     * <strong>Field Name:</strong> {@code FirstPublishedAt}<br/>
     * <strong>Field Description:</strong> Server-generated timestamp of the first successful deployment for this GlobalPublicationPathId.
     */
    public static final String SCHEMA_FIELD_NAME__FIRST_PUBLISHED_AT = "FirstPublishedAt";

    /**
     * <strong>Field Name:</strong> {@code PublishedAt}<br/>
     * <strong>Field Description:</strong> Server-generated timestamp of the most recent successful deployment or release transition represented by this metadata.
     */
    public static final String SCHEMA_FIELD_NAME__PUBLISHED_AT = "PublishedAt";

    /**
     * <strong>Field Name:</strong> {@code ReleasedAt}<br/>
     * <strong>Field Description:</strong> Server-generated timestamp at which this publication became an immutable release; absent while the publication is a draft.
     */
    public static final String SCHEMA_FIELD_NAME__RELEASED_AT = "ReleasedAt";

    /**
     * <strong>Field Name:</strong> {@code PublishedBy}<br/>
     * <strong>Field Description:</strong> Optional trusted publisher identity derived by the deployment adapter from authenticated execution context; never copied from user metadata.
     */
    public static final String SCHEMA_FIELD_NAME__PUBLISHED_BY = "PublishedBy";

    /**
     * <strong>Field Name:</strong> {@code ContentSha256}<br/>
     * <strong>Field Description:</strong> SHA-256 of the exact deployed canonical content, used to reject incomplete or inconsistent definition/sidecar pairs.
     */
    public static final String SCHEMA_FIELD_NAME__CONTENT_SHA256 = "ContentSha256";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdGlobalPublicationDeployMetadata_1 {
        if ($schema != null) {
        }
        java.util.Objects.requireNonNull(globalPublicationPathId, "GlobalPublicationPathId");
        if (globalPublicationPathId != null) {
            if (globalPublicationPathId.codePointCount(0, globalPublicationPathId.length()) < 1) throw new IllegalArgumentException("Schema value violates its constraints.");
            if (!java.util.regex.Pattern.compile("^(?!/)(?!.*(?:^|/)\\.\\.(?:/|$))(?!.*//)[A-Za-z0-9._/-]+$").matcher(globalPublicationPathId.toString()).find()) throw new IllegalArgumentException("Schema value violates its constraints.");
        }
        java.util.Objects.requireNonNull(publicationState, "PublicationState");
        if (publicationState != null) {
            if (!java.util.List.of("draft", "release").contains(publicationState.toString())) throw new IllegalArgumentException("Schema value violates its constraints.");
        }
        java.util.Objects.requireNonNull(publicationRevision, "PublicationRevision");
        if (publicationRevision != null) {
            if (new java.math.BigDecimal(publicationRevision.toString()).compareTo(new java.math.BigDecimal("1")) < 0) throw new IllegalArgumentException("Schema value violates its constraints.");
        }
        java.util.Objects.requireNonNull(firstPublishedAt, "FirstPublishedAt");
        if (firstPublishedAt != null) {
            if (!firstPublishedAt.isValid() || !"dateTime".equals(firstPublishedAt.getXMLSchemaType().getLocalPart())) throw new IllegalArgumentException("Schema value violates its constraints.");
        }
        java.util.Objects.requireNonNull(publishedAt, "PublishedAt");
        if (publishedAt != null) {
            if (!publishedAt.isValid() || !"dateTime".equals(publishedAt.getXMLSchemaType().getLocalPart())) throw new IllegalArgumentException("Schema value violates its constraints.");
        }
        if (releasedAt != null) {
            if (!releasedAt.isValid() || !"dateTime".equals(releasedAt.getXMLSchemaType().getLocalPart())) throw new IllegalArgumentException("Schema value violates its constraints.");
        }
        if (publishedBy != null) {
            if (publishedBy.codePointCount(0, publishedBy.length()) < 1) throw new IllegalArgumentException("Schema value violates its constraints.");
        }
        java.util.Objects.requireNonNull(contentSha256, "ContentSha256");
        if (contentSha256 != null) {
            if (!java.util.regex.Pattern.compile("^[a-f0-9]{64}$").matcher(contentSha256.toString()).find()) throw new IllegalArgumentException("Schema value violates its constraints.");
        }
    }
}
