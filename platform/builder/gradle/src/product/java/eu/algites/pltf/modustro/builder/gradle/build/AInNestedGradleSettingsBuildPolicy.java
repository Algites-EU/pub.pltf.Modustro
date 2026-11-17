package eu.algites.pltf.modustro.builder.gradle.build;

/** Defines how nested Gradle settings files affect Modustro build-domain discovery. */
public enum AInNestedGradleSettingsBuildPolicy {
    IGNORE_NESTED_SETTINGS,
    USE_ISOLATED_BUILD_ON_NESTED_SETTINGS
}
