# Modustro Builder runtime bundle (1.1-SNAPSHOT)

This **source-free** Java publication exports the coherent 1.1 classpath required by
`platform/builder/gradle` and by the applied Gradle Project scripts. It replaces the
legacy governance artifact `pub.gov.Algites_devops.build.modustrobuild:1.0-SNAPSHOT`.

Maven identity: `eu.algites.pltf.modustro.builder:pub.pltf.Modustro_platform.builder.runtime:1.1-SNAPSHOT`.
The Maven POM and Gradle `.module` are essential: the JAR contains no runtime classes.
The bundle provides Builder `core.impl`, Defs/SDO Codegen 1.1, the naming profile and
version utilities. Security provider implementations remain runtime-only dependencies.

Publish this artifact after Builder `core.intf` and `core.impl` and before the Gradle
integration JAR. No governance-specific YAML schema artifact is needed; definitions
are already hosted by `core/intf`.
