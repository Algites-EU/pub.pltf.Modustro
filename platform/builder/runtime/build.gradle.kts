plugins {
    `java-library`
    `maven-publish`
}

// This intentional source-free module exports a governed, versioned Builder
// buildscript classpath. Consumers must publish/resolve POM + .module, not just a JAR.
