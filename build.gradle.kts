import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SourcesJar
import io.gitlab.arturbosch.detekt.Detekt
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
    alias(libs.plugins.binary.compatibility.validator)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.maven.publish)
}

// ---------------------------------------------------------------------------
// Compilation — Java 11 bytecode, built on the JDK 21 toolchain
// ---------------------------------------------------------------------------

kotlin {
    // Every public declaration must be explicitly `public` and fully typed.
    explicitApi()

    jvmToolchain(21)

    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
        // Compile against the Java 11 API, not just to Java 11 bytecode: without this
        // a newer JDK's methods would link fine here and fail on a consumer's Java 11.
        freeCompilerArgs.add("-Xjdk-release=11")
        allWarningsAsErrors = true
    }
}

// The Java tasks must agree with Kotlin's jvmTarget, or the Kotlin plugin rejects the
// mismatch — even in a repository with no Java sources.
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 11
}

tasks.jar {
    manifest {
        attributes("Automatic-Module-Name" to "io.github.silicontaiga.percentage")
    }
}

// ---------------------------------------------------------------------------
// Dependencies — the library has no third-party runtime dependencies, by design
// ---------------------------------------------------------------------------

dependencies {
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.property)
}

// ---------------------------------------------------------------------------
// Testing — Kotest on the JUnit Platform
// ---------------------------------------------------------------------------

val javaToolchains = extensions.getByType<JavaToolchainService>()

// CI passes -PtestJavaVersion=11|17|21 so the Java 11 bytecode floor is proven on
// every supported JVM rather than assumed; locally the toolchain JDK runs the tests.
val testJavaVersion: Provider<String> = providers.gradleProperty("testJavaVersion")

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    if (testJavaVersion.isPresent) {
        javaLauncher =
            javaToolchains.launcherFor {
                languageVersion = JavaLanguageVersion.of(testJavaVersion.get())
            }
    }
    testLogging {
        events("failed", "skipped")
        exceptionFormat = TestExceptionFormat.FULL
    }
}

// ---------------------------------------------------------------------------
// Coverage — 100% line and branch, build-breaking
// ---------------------------------------------------------------------------

kover {
    reports {
        total {
            xml { onCheck = true } // consumed by the Codecov upload in ci.yml
            html { onCheck = false }
        }
        verify {
            rule("Line coverage must be 100%") {
                bound {
                    minValue = 100
                    coverageUnits = CoverageUnit.LINE
                }
            }
            rule("Branch coverage must be 100%") {
                bound {
                    minValue = 100
                    coverageUnits = CoverageUnit.BRANCH
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Public ABI — the committed dump is the arbiter of SemVer MAJOR
// ---------------------------------------------------------------------------

apiValidation {
    // Nothing is excluded: everything public is intentional under explicit API mode.
}

// ---------------------------------------------------------------------------
// Code quality
// ---------------------------------------------------------------------------

ktlint {
    version = libs.versions.ktlint.asProvider()
    filter {
        exclude { it.file.path.contains("${File.separator}build${File.separator}") }
    }
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(files("$rootDir/detekt.yml"))
    // detekt-formatting is deliberately absent: ktlint owns formatting.
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = JvmTarget.JVM_11.target
    reports {
        html.required = true
        xml.required = true
        sarif.required = false
        txt.required = false
        md.required = false
    }
}

// `./gradlew check` locally must equal CI: tests, coverage gate, ABI, ktlint, detekt.
tasks.check {
    dependsOn(tasks.koverVerify)
}

// ---------------------------------------------------------------------------
// Documentation
// ---------------------------------------------------------------------------

dokka {
    moduleName = "kotlin-percentage"
    dokkaSourceSets.configureEach {
        jdkVersion = 11
        sourceLink {
            localDirectory = file("src/main/kotlin")
            remoteUrl("https://github.com/silicontaiga/kotlin-percentage/blob/main/src/main/kotlin")
            remoteLineSuffix = "#L"
        }
    }
}

// ---------------------------------------------------------------------------
// Publishing — Central Portal, released automatically after validation
// ---------------------------------------------------------------------------

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
    configure(
        KotlinJvm(
            javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"),
            sourcesJar = SourcesJar.Sources(),
        ),
    )
}
