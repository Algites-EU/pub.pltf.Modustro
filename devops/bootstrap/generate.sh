#!/usr/bin/env bash
set -euo pipefail

# The definition phase is independent of the Modustro Settings/Project plugins.
# Only the standalone Codegen CLIs see their own, resolved generator classpath.
locRepository="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd -P)"
locBuilder="$locRepository/platform/builder"
# The generic entry point owns cleanup and source scope for every repository.
[[ "${MODUSTRO_BOOTSTRAP_REPOSITORY:-}" == "$locRepository" ]] || {
    echo 'Invoke devops/bootstrap/bootstrap.sh (not the generator directly)' >&2; exit 2;
}
locChoiceRoot="${MODUSTRO_BOOTSTRAP_ROOT:-$locRepository}"
locGeneratedArtifact="$locBuilder/core/intf"
if [[ "$locGeneratedArtifact" != "$locChoiceRoot" && "$locGeneratedArtifact" != "$locChoiceRoot/"* ]]; then
    echo 'Selected root contains no Builder Defs/SDO generation inputs; skipping Builder codegen.'
    exit 0
fi
locMode="${MODUSTRO_BOOTSTRAP_DEPENDENCY_MODE:-remote}"
case "$locMode" in remote|local|source) ;; *) echo "Invalid dependency mode: $locMode" >&2; exit 2;; esac
locBuildRoot="$locRepository/build/run/builder-bootstrap"
mkdir -p "$locBuildRoot/work"
locTemporary="$(mktemp -d "$locBuildRoot/work/generators.XXXXXXXX")"
trap 'rm -rf -- "$locTemporary"' EXIT
export PYTHONDONTWRITEBYTECODE=1
export PYTEST_ADDOPTS="${PYTEST_ADDOPTS:+${PYTEST_ADDOPTS} }-p no:cacheprovider"

if [[ "$locMode" == source && "${MODUSTRO_BOOTSTRAP_SOURCE_PREPARED:-0}" != 1 ]]; then
    locTool="${MODUSTRO_BOOTSTRAP_TOOL_SOURCE_DIR:-$(dirname -- "$locRepository")/pub.tool.General}"
    [[ -f "$locTool/generators/code/bootstrap.sh" ]] || {
        echo "Source mode requires the pub.tool.General source checkout: $locTool" >&2; exit 2;
    }
    MODUSTRO_BOOTSTRAP_DEPENDENCY_MODE=source MODUSTRO_BOOTSTRAP_PREPARE_ONLY=1 \
        bash "$locTool/generators/code/bootstrap.sh"
fi

# No Builder artifact is a dependency of this project. In particular, neither
# gradleinit nor modustrobuild is loaded during code-generation bootstrap.
cat > "$locTemporary/settings.gradle" <<'GRADLE'
rootProject.name = 'algites-builder-definitions-bootstrap'
GRADLE
cat > "$locTemporary/build.gradle" <<'GRADLE'
configurations { generators }
repositories {
    def locMode = System.getenv('MODUSTRO_BOOTSTRAP_DEPENDENCY_MODE') ?: 'remote'
    if (locMode != 'remote') maven { name = 'modustro-local'; url = uri(System.getenv('MODUSTRO_LOCAL_MAVEN_REPOSITORY') ?: new File(System.getenv('MODUSTRO_HOME') ?: new File(System.getProperty('user.home'), '.modustro').path, 'artifact-repos/java/maven')) }
    if (locMode == 'remote') maven {
        url = uri('https://dl.cloudsmith.io/public/algites/java-snapshots-pub/maven/')
        content { includeGroupByRegex('eu\\.algites\\..*') }
    }
    mavenCentral()

}
dependencies {
    generators 'eu.algites.tool.codegen:pub.tool.General_generators.code.defscodegen.intf:1.1-SNAPSHOT'
    generators 'eu.algites.tool.codegen:pub.tool.General_generators.code.defscodegen.impl:1.1-SNAPSHOT'
    generators 'eu.algites.tool.codegen:pub.tool.General_generators.code.defscodegen.cli:1.1-SNAPSHOT'
    generators 'eu.algites.tool.codegen:pub.tool.General_generators.code.sdocodegen.intf:1.1-SNAPSHOT'
    generators 'eu.algites.tool.codegen:pub.tool.General_generators.code.sdocodegen.impl:1.1-SNAPSHOT'
    generators 'eu.algites.tool.codegen:pub.tool.General_generators.code.sdocodegen.cli:1.1-SNAPSHOT'
    generators 'eu.algites.tool.codegen:pub.tool.General_generators.code.profile.algites:1.1-SNAPSHOT'
    generators 'eu.algites.lib.naming:pub.lib.General_naming.convention.impl:1.1-SNAPSHOT'
    generators 'eu.algites.lib.naming:pub.lib.General_naming.convention.intf:1.1-SNAPSHOT'
    generators 'eu.algites.lib.data:pub.lib.General_data.dataobject.intf:1.1-SNAPSHOT'
    generators 'eu.algites.lib.data:pub.lib.General_data.smartdataobject.impl:1.1-SNAPSHOT'
    generators 'eu.algites.lib.common:pub.lib.General_common:1.1-SNAPSHOT'
}
tasks.register('exportGeneratorClasspath') {
    doLast {
        def locResolved = configurations.generators.resolve().toList().sort { it.absolutePath }
        def locRequired = [
            'eu/algites/tool/codegen/defs/AInDefsNameRole.class',
            'eu/algites/tool/codegen/defs/impl/standard/AIcSchemaContractBindingsGenerator.class',
            'eu/algites/tool/codegen/defs/impl/cli/AIcDefsCodegenCli.class',
            'eu/algites/tool/codegen/sdo/impl/standard/AIcSdoCodegenService.class',
            'eu/algites/tool/codegen/sdo/impl/cli/AIcSdoCodegenCli.class',
            'eu/algites/lib/naming/convention/impl/standard/AIcNameConventionCatalog.class',
            'eu/algites/lib/data/dataobject/AInFieldPresenceKind.class'
        ]
        def locPresent = new HashSet<String>()
        locResolved.each { locJar ->
            if (!locJar.name.endsWith('.jar')) return
            new java.util.zip.ZipFile(locJar).withCloseable { locZip ->
                locRequired.each { locEntry ->
                    if (locZip.getEntry(locEntry) != null) locPresent.add(locEntry)
                }
            }
        }
        def locMissing = locRequired.findAll { !locPresent.contains(it) }
        if (!locMissing.isEmpty())
            throw new GradleException('Resolved bootstrap Codegen classpath is incompatible: ' +
                locMissing + '. Rebuild the 1.1-SNAPSHOT sources using pub.tool.General/generators/code/bootstrap.sh.')
        def locNamingJar = locResolved.find { it.name.startsWith('pub.lib.General_naming.convention.profile.algites-') && it.name.endsWith('.jar') }
        if (locNamingJar == null)
            throw new GradleException('Missing Algites naming profile transitive dependency on generator classpath')
        if (!locResolved.any { it.name.startsWith('pub.tool.General_generators.code.profile.algites-') })
            throw new GradleException('Missing explicitly selected Algites codegen profile')
        def locNamingEntry = 'eu/algites/lib/naming/convention/algites-java-name-conventions.yml'
        def locNamingData = new java.util.zip.ZipFile(locNamingJar).withCloseable { locZip ->
            def locEntry = locZip.getEntry(locNamingEntry)
            if (locEntry == null) return null
            locZip.getInputStream(locEntry).withCloseable { locInput ->
                locInput.getText('UTF-8')
            }
        }
        if (locNamingData == null || !locNamingData.contains('  algites-java-gen-intf-versioned_1:')
                || !locNamingData.contains('getter_method_name:')
                || !locNamingData.contains('setter_method_name:'))
            throw new GradleException('Incompatible naming catalog resources in ' + locNamingJar +
                ': missing current 1.1 generated-name roles/getter/setter structures')
        def locSharedJar = locResolved.find { it.name.startsWith('pub.lib.General_naming.convention.intf-') && it.name.endsWith('.jar') }
        if (locSharedJar == null)
            throw new GradleException('Missing shared naming convention intf dependency on generator classpath')
        new java.util.zip.ZipFile(locSharedJar).withCloseable { locZip ->
            if (locZip.getEntry('eu/algites/lib/naming/convention/standard-conventions.yml') == null)
                throw new GradleException('Outdated shared naming conventions in ' + locSharedJar +
                    '; republish pub.lib.General/naming/convention/intf first')
        }
        logger.lifecycle('Algites naming catalog and shared intf grammar verified: ' + locNamingJar.absolutePath)
        def locPath = new File(System.getenv('MODUSTRO_CODEGEN_BOOTSTRAP_CLASSPATH_FILE'))
        locPath.parentFile.mkdirs()
        locPath.text = locResolved.collect { it.absolutePath }.join(File.pathSeparator)
        logger.lifecycle('Standalone Defs/SDO Codegen classpath: ' + locResolved.size() + ' JARs')
        locResolved.findAll { it.name.contains('codegen') || it.name.contains('naming.convention.profile.algites') }
            .each { logger.lifecycle('  ' + it.absolutePath) }
    }
}
GRADLE
locGradle=(bash "$locRepository/gradlew")
if [[ -n "${MODUSTRO_GRADLE_EXECUTABLE:-}" ]]; then locGradle=("${MODUSTRO_GRADLE_EXECUTABLE}"); fi
locGradleArgs=(--no-daemon)
if [[ "${MODUSTRO_BOOTSTRAP_REFRESH_DEPENDENCIES:-$([[ "$locMode" == remote ]] && echo 1 || echo 0)}" == 1 ]]; then
    locGradleArgs+=(--refresh-dependencies)
fi
locClasspathFile="$locTemporary/generators.classpath"
MODUSTRO_CODEGEN_BOOTSTRAP_CLASSPATH_FILE="$locClasspathFile" \
MODUSTRO_BOOTSTRAP_DEPENDENCY_MODE="$locMode" \
"${locGradle[@]}" "${locGradleArgs[@]}" --project-dir "$locTemporary" exportGeneratorClasspath
[[ -s "$locClasspathFile" ]] || { echo 'Generator classpath resolution produced no JARs' >&2; exit 1; }
locJavaClasspath="$(cat "$locClasspathFile")"

# Source mode must not silently import outdated Python generator distributions.
# Namespace roots from sibling checkouts take precedence over installed wheels.
locPython="${ALGITES_PYTHON_EXECUTABLE:-python3}"

# Remote mode is self-contained. Never rely on previously installed Python
# generator modules or on neighboring source checkouts. A fresh virtualenv also
# prevents stale 1.1 snapshots from being imported via the user's interpreter.
if [[ "$locMode" == remote || "$locMode" == local ]]; then
    export PYTHONPATH=
    export PYTHONNOUSERSITE=1
    locVenv="$locTemporary/python-codegen-venv"
    echo "Preparing isolated Python Codegen environment from $locMode artifact repository."
    if ! "$locPython" -m venv "$locVenv"; then
        echo "FAILED: cannot create Python Codegen virtualenv with $locPython (is python3-venv installed?)." >&2
        exit 1
    fi
    locPython="$locVenv/bin/python"
    locCloudsmithPythonIndex="${MODUSTRO_BOOTSTRAP_PYTHON_INDEX_URL:-https://dl.cloudsmith.io/public/algites/python-snapshots-pub/python/simple/}"
    locLocalPythonRepo="${MODUSTRO_LOCAL_PYTHON_REPOSITORY:-${MODUSTRO_HOME:-$HOME/.modustro}/artifact-repos/python}"
    locPythonIndex="$locCloudsmithPythonIndex"
    if [[ "$locMode" == local ]]; then
        locPythonIndex="$("${ALGITES_PYTHON_EXECUTABLE:-python3}" "$locRepository/devops/bootstrap/modustro-local-python-repo.py" show-index-url --repository "$locLocalPythonRepo")"
        [[ -f "$locLocalPythonRepo/simple/index.html" ]] || {
            echo "Local Modustro Python index missing: $locPythonIndex; bootstrap pub.lib.General and pub.tool.General first." >&2
            exit 2
        }
    fi
    locPythonPackages=(
        'eu-algites-pub-lib-general-common>=1.1.dev0,<1.1a0'
        'eu-algites-pub-lib-general-data-dataobject-intf>=1.1.dev0,<1.1a0'
        'eu-algites-pub-lib-general-naming-convention-intf>=1.1.dev0,<1.1a0'
        'eu-algites-pub-lib-general-naming-convention-impl>=1.1.dev0,<1.1a0'
        'eu-algites-pub-tool-general-generators-code-profile-algites>=1.1.dev0,<1.1a0'
        'eu-algites-pub-lib-general-data-smartdataobject-intf>=1.1.dev0,<1.1a0'
        'eu-algites-pub-lib-general-data-smartdataobject-impl>=1.1.dev0,<1.1a0'
        'eu-algites-pub-tool-general-generators-code-defscodegen-intf>=1.1.dev0,<1.1a0'
        'eu-algites-pub-tool-general-generators-code-defscodegen-impl>=1.1.dev0,<1.1a0'
        'eu-algites-pub-tool-general-generators-code-defscodegen-cli>=1.1.dev0,<1.1a0'
        'eu-algites-pub-tool-general-generators-code-sdocodegen-intf>=1.1.dev0,<1.1a0'
        'eu-algites-pub-tool-general-generators-code-sdocodegen-impl>=1.1.dev0,<1.1a0'
        'eu-algites-pub-tool-general-generators-code-sdocodegen-cli>=1.1.dev0,<1.1a0'
        'pyyaml>=6,<7'
    )
    if ! "$locPython" -m pip install --disable-pip-version-check --no-input --pre \
            --index-url "$locPythonIndex" \
            --extra-index-url 'https://pypi.org/simple/' \
            "${locPythonPackages[@]}"; then
        echo 'FAILED: published Python 1.1 Codegen/General dependencies could not be installed.' >&2
        echo "Check Python distributions in the selected index: $locPythonIndex" >&2
        exit 1
    fi
    if ! "$locPython" -m pip check; then
        echo 'FAILED: isolated Python Codegen dependency metadata is inconsistent.' >&2
        exit 1
    fi
fi
locPythonRoots=()
if [[ "$locMode" == source ]]; then
    locTool="${MODUSTRO_BOOTSTRAP_TOOL_SOURCE_DIR:-$(dirname -- "$locRepository")/pub.tool.General}"
    locGeneral="${MODUSTRO_BOOTSTRAP_GENERAL_SOURCE_DIR:-$(dirname -- "$locRepository")/pub.lib.General}"
    for locSource in \
        "$locTool/generators/code/defscodegen/intf/src/product/python" \
        "$locTool/generators/code/defscodegen/impl/src/product/python" \
        "$locTool/generators/code/defscodegen/cli/src/product/python" \
        "$locTool/generators/code/sdocodegen/intf/src/product/python" \
        "$locTool/generators/code/sdocodegen/impl/src/product/python" \
        "$locTool/generators/code/sdocodegen/cli/src/product/python" \
        "$locTool/generators/code/defscodegen/intf/src/product/config" \
        "$locTool/generators/code/sdocodegen/intf/src/product/config" \
        "$locGeneral/common/src/product/python" \
        "$locGeneral/factory/cpdiscovery/intf/src/product/python" \
        "$locGeneral/factory/cpdiscovery/impl/src/product/python" \
        "$locGeneral/data/dataobject/intf/src/product/python" \
        "$locGeneral/data/smartdataobject/intf/src/product/python" \
        "$locGeneral/data/smartdataobject/impl/src/product/python" \
        "$locGeneral/naming/convention/intf/src/product/python" \
        "$locGeneral/naming/convention/intf/src/product/python.gen" \
        "$locGeneral/naming/convention/intf/src/product/config" \
        "$locGeneral/naming/convention/impl/src/product/python" \
        "$locGeneral/naming/convention/profile/algites/src/product/python" \
        "$locGeneral/naming/convention/profile/algites/src/product/config" \
        "$locTool/generators/code/profile/algites/src/product/config" \
        "$locGeneral/text/format/src/product/python"; do
        if [[ -d "$locSource" ]]; then locPythonRoots+=("$locSource"); fi
    done
    [[ -d "$locTool/generators/code/defscodegen/impl/src/product/python" &&
       -d "$locGeneral/naming/convention/profile/algites/src/product/python" ]] || {
        echo 'Source mode requires current pub.tool.General and pub.lib.General Python sources.' >&2; exit 2;
    }
fi
locPythonPrefix=""
if (( ${#locPythonRoots[@]} )); then locPythonPrefix="$(IFS=:; echo "${locPythonRoots[*]}")"; fi
export PYTHONPATH="${locPythonPrefix:+$locPythonPrefix:}${PYTHONPATH:-}"

if "$locPython" - <<'PYTHON'
import importlib
import pathlib
import sys
required = [
    'eu.algites.lib.naming.convention.impl.standard.aic_name_convention_catalog',
    'eu.algites.tool.codegen.defs.impl.cli.aic_defs_codegen_cli',
    'eu.algites.tool.codegen.sdo.impl.cli.aic_sdo_codegen_cli',
]
for name in required:
    try:
        module = importlib.import_module(name)
    except ImportError as error:
        raise SystemExit(f'Python Codegen dependency unavailable: {name}: {error}. '
                         'Use source mode with current pub.tool.General and pub.lib.General '
                         'or install the 1.1 Python Codegen distributions.')
    print(f'Python Codegen module: {name} -> {module.__file__}')
    if sys.prefix != sys.base_prefix and not pathlib.Path(module.__file__).resolve().is_relative_to(pathlib.Path(sys.prefix).resolve()):
        raise SystemExit(f'Python Codegen module imported outside isolated environment: {name} -> {module.__file__}')
from eu.algites.lib.naming.convention.impl.standard.aic_name_convention_catalog import AIcNameConventionCatalog
for technology in ('java', 'python'):
    catalog = AIcNameConventionCatalog.load_resource(f'algites-{technology}-name-conventions.yml')
    required = {f'algites-{technology}-gen-intf-versioned_1',
                f'algites-{technology}-gen-yaml-intf-versioned_1',
                f'algites-{technology}-gen-json-intf-versioned_1'}
    missing = required - set(catalog.names)
    if missing:
        raise SystemExit(f'Python-loaded {technology} naming catalog is missing {sorted(missing)}; '
                         'update the Python naming resources before regenerating Builder sources')
    print(f'{technology} native naming catalog verified: {len(catalog.names)} profiles')
PYTHON
then
    echo 'Python Codegen imports and naming profiles verified.'
else
    locPythonValidationStatus=$?
    echo 'FAILED: Python Codegen validation failed; Builder compilation will not start.' >&2
    exit "$locPythonValidationStatus"
fi

locLegacyGovernanceDirectory="${MODUSTRO_LEGACY_GOVERNANCE_DIR:-$(dirname -- "$locRepository")/pub.gov.Algites}"
locArtifacts=("$locLegacyGovernanceDirectory/devops/build/yamldefs" "$locBuilder/core/intf")
locStaging=()
for locArtifact in "${locArtifacts[@]}"; do
    [[ -d "$locArtifact/src/product" ]] || { echo "Missing artifact: $locArtifact" >&2; exit 2; }
    locStage="$locTemporary/schema-$(basename -- "$locArtifact")"
    "$locPython" "$locRepository/devops/bootstrap/codegen-staging.py" prepare "$locArtifact" "$locStage"
    locStaging+=("$locStage")
done

# Resolve cross-artifact allOf inheritance and cross-definition type references
# within one canonical batch. Generating each artifact separately would hide
# the other artifact's definition documents from the generator's reference
# index, even if the later Gradle compilation has correct project dependencies.
locSharedStage="$locTemporary/schema-combined"
"$locPython" "$locRepository/devops/bootstrap/codegen-staging.py" combine "$locSharedStage" "${locStaging[@]}"
echo 'Generating Defs and SDO sources in one shared schema context.'
# Current 1.1 Codegen CLIs merge packaged defaults, optional settings layers,
# and the explicitly qualified options below. The packaged defaults supply
# naming profiles, catalogs, subpackages, and setter inheritance policy;
# bootstrap binds the source/output paths to its disposable combined staging
# tree. Do not probe --help: settings expansion is not an argparse help parser.
locCodegenRoot="--repository-root=$locSharedStage"
locDefsInputs=(
    '--schema-input-dir:xmldefs=src/product/xmldefs'
    '--schema-input-dir:yamldefs=src/product/yamldefs'
    '--schema-input-dir:jsondefs=src/product/jsondefs'
)
locJavaDefsOutput='--technology-dointf-output-dir:java=src/product/java.gen'
locPythonDefsOutput='--technology-dointf-output-dir:python=src/product/python.gen'
locJavaSdoPaths=(
    '--technology-dointf-input-dir:java=src/product/java.gen'
    '--technology-sdointf-output-dir:java=src/product/java.gen'
    '--technology-sdoimpl-output-dir:java=src/product/java.gen'
)
locPythonSdoPaths=(
    '--technology-dointf-input-dir:python=src/product/python.gen'
    '--technology-sdointf-output-dir:python=src/product/python.gen'
    '--technology-sdoimpl-output-dir:python=src/product/python.gen'
)
(
    cd -- "$locSharedStage" || exit $?
    # A subshell used as the left side of || does not inherit reliable errexit
    # handling. Explicitly stop on the FIRST failing generator instead of
    # running SDO on missing Defs output and obscuring the actual failure.
    java -cp "$locJavaClasspath" eu.algites.tool.codegen.defs.impl.cli.AIcDefsCodegenCli \
        "$locCodegenRoot" "${locDefsInputs[@]}" "$locJavaDefsOutput" || exit $?
    "$locPython" -m eu.algites.tool.codegen.defs.impl.cli.aic_defs_codegen_cli \
        "$locCodegenRoot" "${locDefsInputs[@]}" "$locPythonDefsOutput" || exit $?
    java -cp "$locJavaClasspath" eu.algites.tool.codegen.sdo.impl.cli.AIcSdoCodegenCli \
        "$locCodegenRoot" "${locJavaSdoPaths[@]}" || exit $?
    "$locPython" -m eu.algites.tool.codegen.sdo.impl.cli.aic_sdo_codegen_cli \
        "$locCodegenRoot" "${locPythonSdoPaths[@]}" || exit $?
) || {
    echo 'FAILED: joint Defs/SDO generation failed; generated source roots remain empty.' >&2
    exit 1
}
# The shared generator sees all definitions, but each output belongs to one
# artifact. Partition by schema package before the rollback-protected commit.
"$locPython" "$locRepository/devops/bootstrap/codegen-staging.py" partition "$locSharedStage" "${locStaging[@]}" || {
    echo 'FAILED: generated source ownership check failed; generated source roots remain empty.' >&2
    exit 1
}

locCommitPairs=()
for locIndex in "${!locArtifacts[@]}"; do
    [[ "$locIndex" == 0 ]] && continue  # Legacy gov yamldefs is reference-only here.
    locCommitPairs+=("${locStaging[$locIndex]}=${locArtifacts[$locIndex]}")
done
"$locPython" "$locRepository/devops/bootstrap/codegen-staging.py" commit "${locCommitPairs[@]}" || {
    echo 'FAILED: cannot commit regenerated Builder sources; bootstrap stops.' >&2
    exit 1
}
printf 'SUCCESS: standalone Defs/SDO Java and Python generation completed.\n'
