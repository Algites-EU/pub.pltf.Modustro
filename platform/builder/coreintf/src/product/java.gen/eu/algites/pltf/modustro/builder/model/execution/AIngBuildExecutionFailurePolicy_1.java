package eu.algites.pltf.modustro.builder.model.execution;

/**
 * Defines how an execution failure affects its parent execution hierarchy and the enclosing build. Canonical representations: YAMLDEFS: eu.algites.pltf.modustro.builder.model.execution.build-execution-failure-policy (src/product/yamldefs/eu/algites/pltf/modustro/builder/model/execution/build-execution-failure-policy_1.yamldef.schema.json); JSONDEFS: eu.algites.pltf.modustro.builder.model.execution.build-execution-failure-policy (src/product/jsondefs/eu/algites/pltf/modustro/builder/model/execution/build-execution-failure-policy_1.jsondef.schema.json)
 *
 * <p>Generated from canonical definition eu.algites.pltf.modustro.builder.model.execution.build-execution-failure-policy/1. Source: src/product/yamldefs/eu/algites/pltf/modustro/builder/model/execution/build-execution-failure-policy_1.yamldef.schema.json. Do not edit manually.</p>
 */
public enum AIngBuildExecutionFailurePolicy_1 {
    /**
     * Requires the entire build to fail when this execution or any non-ignored descendant execution fails. This terminal failure cannot be absorbed by an ancestor execution policy.
     */
    FAIL_BUILD_ON_FAILURE("fail_build_on_failure"),
    /**
     * Propagates a non-terminal failure to the immediate parent execution, whose own BuildExecutionFailurePolicy decides whether to fail the build, continue propagation, or ignore the failure.
     */
    PROPAGATE_FAILURE("propagate_failure"),
    /**
     * Consumes a non-terminal failure at this execution boundary so that it is not propagated to an ancestor execution. An already terminal fail-build failure cannot be ignored.
     */
    IGNORE_FAILURE("ignore_failure");

    public static final String CANONICAL_SOURCE_ID = "eu.algites.pltf.modustro.builder.model.execution.build-execution-failure-policy";
    public static final Integer CANONICAL_SOURCE_VERSION = 1;
    public static final String CANONICAL_SOURCE_RESOURCE = "src/product/yamldefs/eu/algites/pltf/modustro/builder/model/execution/build-execution-failure-policy_1.yamldef.schema.json";

    private final String wireValue;

    AIngBuildExecutionFailurePolicy_1(String aWireValue) {
        wireValue = aWireValue;
    }

    /**
     * Returns the exact serialized value declared by the canonical definition.
     *
     * @return canonical wire value
     */
    public String wireValue() {
        return wireValue;
    }
}
