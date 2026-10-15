from enum import Enum


class AIngBuildExecutionFailurePolicy_1(str, Enum):
    """Defines how an execution failure affects its parent execution hierarchy and the enclosing build. Canonical representations: YAMLDEFS: eu.algites.pltf.modustro.builder.model.execution.build-execution-failure-policy (src/product/yamldefs/eu/algites/pltf/modustro/builder/model/execution/build-execution-failure-policy_1.yamldef.schema.json); JSONDEFS: eu.algites.pltf.modustro.builder.model.execution.build-execution-failure-policy (src/product/jsondefs/eu/algites/pltf/modustro/builder/model/execution/build-execution-failure-policy_1.jsondef.schema.json)

    Generated from canonical definition eu.algites.pltf.modustro.builder.model.execution.build-execution-failure-policy/1. Source: src/product/yamldefs/eu/algites/pltf/modustro/builder/model/execution/build-execution-failure-policy_1.yamldef.schema.json. Do not edit manually.

    Values:
        FAIL_BUILD_ON_FAILURE: Requires the entire build to fail when this execution or any non-ignored descendant execution fails. This terminal failure cannot be absorbed by an ancestor execution policy.
        PROPAGATE_FAILURE: Propagates a non-terminal failure to the immediate parent execution, whose own BuildExecutionFailurePolicy decides whether to fail the build, continue propagation, or ignore the failure.
        IGNORE_FAILURE: Consumes a non-terminal failure at this execution boundary so that it is not propagated to an ancestor execution. An already terminal fail-build failure cannot be ignored.
    """
    __canonical_source_id__ = 'eu.algites.pltf.modustro.builder.model.execution.build-execution-failure-policy'
    __canonical_source_version__ = 1
    __canonical_source_resource__ = 'src/product/yamldefs/eu/algites/pltf/modustro/builder/model/execution/build-execution-failure-policy_1.yamldef.schema.json'
    FAIL_BUILD_ON_FAILURE = 'fail_build_on_failure'
    PROPAGATE_FAILURE = 'propagate_failure'
    IGNORE_FAILURE = 'ignore_failure'
