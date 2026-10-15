package eu.algites.pltf.modustro.builder.model.dependency.xmldefs;

/**
 * Defines exact, bounded, excluded, and preferred versions used by Modustro Builder dependency resolution.
 *
 * <p>Generated from canonical definition urn:algites:modustro:builder:model:version-requirement:1/1. Source: src/product/xmldefs/eu/algites/pltf/modustro/builder/model/dependency/version-requirement_1.xsd. Do not edit manually.</p>
 * @param exact Requires exactly the specified version.
 * @param minimum Sets the minimum allowed version, optionally inclusive.
 * @param maximum Sets a non-strict maximum version that may be relaxed by phased dependency resolution.
 * @param maximumStrict Sets a strict maximum version that must not be exceeded.
 * @param exclude Lists versions that must not be selected.
 * @param prefer Specifies a preferred version to try before falling back to the allowed range.
 */
public record AIcgdVersionRequirement_1(
        String exact,
        java.util.Map<String, Object> minimum,
        java.util.Map<String, Object> maximum,
        Boolean maximumStrict,
        java.util.Map<String, Object> exclude,
        String prefer) {
    public static final String CANONICAL_SOURCE_ID = "urn:algites:modustro:builder:model:version-requirement:1";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/xmldefs/eu/algites/pltf/modustro/builder/model/dependency/version-requirement_1.xsd";

    /**
     * <strong>Field Name:</strong> {@code Exact}<br/>
     * <strong>Field Description:</strong> Requires exactly the specified version.
     */
    public static final String SCHEMA_FIELD_NAME__EXACT = "Exact";

    /**
     * <strong>Field Name:</strong> {@code Minimum}<br/>
     * <strong>Field Description:</strong> Sets the minimum allowed version, optionally inclusive.
     */
    public static final String SCHEMA_FIELD_NAME__MINIMUM = "Minimum";

    /**
     * <strong>Field Name:</strong> {@code Maximum}<br/>
     * <strong>Field Description:</strong> Sets a non-strict maximum version that may be relaxed by phased dependency resolution.
     */
    public static final String SCHEMA_FIELD_NAME__MAXIMUM = "Maximum";

    /**
     * <strong>Field Name:</strong> {@code MaximumStrict}<br/>
     * <strong>Field Description:</strong> Sets a strict maximum version that must not be exceeded.
     */
    public static final String SCHEMA_FIELD_NAME__MAXIMUM_STRICT = "MaximumStrict";

    /**
     * <strong>Field Name:</strong> {@code Exclude}<br/>
     * <strong>Field Description:</strong> Lists versions that must not be selected.
     */
    public static final String SCHEMA_FIELD_NAME__EXCLUDE = "Exclude";

    /**
     * <strong>Field Name:</strong> {@code Prefer}<br/>
     * <strong>Field Description:</strong> Specifies a preferred version to try before falling back to the allowed range.
     */
    public static final String SCHEMA_FIELD_NAME__PREFER = "Prefer";

    /** Validates the scalar constraints retained from the canonical schema. */
    public AIcgdVersionRequirement_1 {
        if (exact != null) {
        }
        if (minimum != null) {
        }
        if (maximum != null) {
        }
        if (maximumStrict != null) {
        }
        if (exclude != null) {
        }
        if (prefer != null) {
        }
    }
}
