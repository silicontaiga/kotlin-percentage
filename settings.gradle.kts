plugins {
    // Downloads the JDK 21 toolchain when it is not already installed locally, so a
    // clean checkout builds identically everywhere.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "kotlin-percentage"

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
    }
}
