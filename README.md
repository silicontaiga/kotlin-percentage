# kotlin-percentage

[![Maven Central](https://img.shields.io/maven-central/v/io.github.silicontaiga/kotlin-percentage?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.silicontaiga/kotlin-percentage)
[![CI](https://github.com/silicontaiga/kotlin-percentage/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/silicontaiga/kotlin-percentage/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/silicontaiga/kotlin-percentage/branch/main/graph/badge.svg)](https://codecov.io/gh/silicontaiga/kotlin-percentage)
[![docs](https://img.shields.io/website?url=https%3A%2F%2Fsilicontaiga.github.io%2Fkotlin-percentage%2Flatest%2F&label=docs)](https://silicontaiga.github.io/kotlin-percentage/latest/)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue)](LICENSE)
[![Kotlin](https://img.shields.io/badge/dynamic/toml?url=https%3A%2F%2Fraw.githubusercontent.com%2Fsilicontaiga%2Fkotlin-percentage%2Fmain%2Fgradle%2Flibs.versions.toml&query=%24.versions.kotlin&label=Kotlin&logo=kotlin&color=7F52FF)](gradle/libs.versions.toml)

Exact, explicit percentage arithmetic for Kotlin/JVM.

While the version stays below `1.0.0` the API may still change — see
[versioning](CONTRIBUTING.md#releases).

## Usage

```kotlin
20.percent.of(250.toBigDecimal())                            // 50
7.percent.of("100.50".toBigDecimal())                        // 7.035
250.toBigDecimal().increasedBy(20.percent)                   // 300
250.toBigDecimal().decreasedBy(20.percent)                   // 200

50.toBigDecimal().asPercentageOf(250.toBigDecimal())         // 20%
Percentage.changeBetween(from = 80.toBigDecimal(), to = 100.toBigDecimal())   // 25%
Percentage.changeBetween(from = 100.toBigDecimal(), to = 80.toBigDecimal())   // -20%

listOf(10.percent, 20.percent, 30.percent).sum()             // 60%
```

A `Percentage` stores the ratio. Every other scale is a named factory going in and an accessor of the
same name coming out, so no number crosses the boundary without saying which scale it is on:

```kotlin
50.percent.ratio             // 0.5   — the stored scale
50.percent.percentValue      // 50
50.percent.perMille          // 500
50.percent.permyriad         // 5000
50.percent.perCentMille      // 50000
```

The finer scales have literals and factories of their own, so a rate quoted in basis points enters
under the name it was quoted in:

```kotlin
25.perMille                       // 2.5%
250.permyriad                     // 2.5%   — 250 basis points
2500.perCentMille                 // 2.5%
Percentage.fromPermyriad(250)     // 2.5%

Percentage.of(50) == Percentage.fromRatio(0.5)       // true
250.permyriad == 2.5.percent                         // true — the scale is a spelling, not an identity
```

| scale | unit | 50% is | reached from the ratio by |
|---|---|---|---|
| ratio | — | `0.5` | stored as-is |
| per cent | `%` | `50` | ×100 |
| per mille | `‰` | `500` | ×1000 |
| permyriad (basis point) | `‱` | `5000` | ×10000 |
| per cent mille | `pcm` | `50000` | ×100000 |

## Exact arithmetic

Values are backed by `java.math.BigDecimal`. Construction, addition, subtraction, negation,
multiplication and application are exact — every scale conversion only moves the decimal point, and
applying a percentage multiplies the base by the stored ratio:

```kotlin
100.toBigDecimal().decreasedBy(20.percent).increasedBy(20.percent)   // 96
```

Only genuine divisions can produce a non-terminating expansion. Those default to
`MathContext.DECIMAL128` (34 digits, `HALF_EVEN`) and each has an overload taking an explicit
`MathContext`:

```kotlin
1.toBigDecimal().asPercentageOf(3.toBigDecimal())    // 33.33333333333333333333333333333333%
```

Operations that are mathematically undefined throw `ArithmeticException` rather than returning a
sentinel — there is no `NaN` in exact JVM arithmetic:

```kotlin
x.asPercentageOf(BigDecimal.ZERO)                                          // throws
Percentage.changeBetween(from = BigDecimal.ZERO, to = 100.toBigDecimal())  // throws
emptyList<Percentage>().average()                                          // throws
```

## What does not compile

Numbers enter and leave application and derivation as `BigDecimal` only, so the caller performs
every conversion in view. No function is `infix`, so no expression can regroup itself:

```kotlin
250.increasedBy(20.percent)                             // error: no Int receiver
20.percent.of(250)                                      // error: no Int argument
250.toBigDecimal().increasedBy(20.percent) + 5.percent  // error: BigDecimal + Percentage
20.percent.of(x).of(y)                                  // error: no `of` on BigDecimal
```

Both readings of an ambiguous intent stay expressible, and each one says which it is:

```kotlin
20.percent.of(250.toBigDecimal()) + BigDecimal(50)        // 100
20.percent.of((250 + 50).toBigDecimal())                  // 60
250.toBigDecimal().increasedBy(20.percent + 5.percent)    // 312.5
250.toBigDecimal().increasedBy(20.percent).increasedBy(5.percent)   // 315
```

## API

| | |
|---|---|
| Construct | `Percentage.of(50)`, `50.percent`, `12.5.percent`, `Percentage.fromRatio(0.5)`, `25.perMille`, `250.permyriad`, `2500.perCentMille` — each from `Int`, `Long`, `Double` or `BigDecimal` |
| Read | `p.ratio`, `p.percentValue`, `p.perMille`, `p.permyriad`, `p.perCentMille` |
| Combine | `p + q`, `p - q`, `-p`, `p * 2`, `p / 2`, `p++`, `p * q` (→ `Percentage`), `p / q` (→ `BigDecimal`) |
| Apply | `p.of(base)`, `base.increasedBy(p)`, `base.decreasedBy(p)` |
| Derive | `n.asPercentageOf(base)`, `Percentage.changeBetween(from, to)` |
| Aggregate | `sum()`, `average()`, `average(context)` over any `Iterable<Percentage>` |
| Compare | `Comparable<Percentage>`; `equals`, `hashCode` and `compareTo` agree on mathematical value |

Negative percentages and percentages above 100 are valid. Instances are immutable and
thread-safe. `p++` and `p--` step by one percentage point.

The table above is a summary; the generated API reference documents every overload, its rounding
and what it throws. It is published per release, so a version can be read at the version you
actually depend on:

- [Latest release](https://silicontaiga.github.io/kotlin-percentage/latest/)
- A specific one at `https://silicontaiga.github.io/kotlin-percentage/<version>/` — for example
  [0.1.0](https://silicontaiga.github.io/kotlin-percentage/0.1.0/)

## Install

```kotlin
dependencies {
    implementation("io.github.silicontaiga:kotlin-percentage:<version>")
}
```

The Maven Central badge above shows the current release.

No third-party runtime dependencies. Java 11 bytecode, so it runs on any JVM from 11 up, and on
Android with desugaring. The jar declares `Automatic-Module-Name: io.github.silicontaiga.percentage`.

### Snapshots

Every push to `main` publishes a `-SNAPSHOT`, so a fix can be tried before it is released.

Snapshots are **not** on Maven Central proper: they are not indexed by its search and are not
mirrored to `repo1.maven.org`, so looking there will not find them. They live in a separate
repository, which has to be declared:

```kotlin
repositories {
    mavenCentral()
    maven("https://central.sonatype.com/repository/maven-snapshots/")
}

dependencies {
    implementation("io.github.silicontaiga:kotlin-percentage:0.1.1-SNAPSHOT")
}
```

The snapshot line is always `VERSION_NAME` in [`gradle.properties`](gradle.properties): the release
workflow overrides that value only for a tagged release, so `main` always carries the next
`-SNAPSHOT`. Gradle caches snapshots for 24 hours — pass `--refresh-dependencies` to force a
re-resolve sooner.

## Out of scope

- **Formatting and parsing.** `toString()` is debug output: locale-independent, `"12.5%"`. Use
  `java.text` for display. Note that `NumberFormat.getPercentInstance` multiplies by 100, so feed
  it `ratio`, never `percentValue`.
- **Range validation.** There is no bounded 0–100% type; enforce domain ranges at your boundary.
- **Multiplatform.** JVM only, because the exactness comes from `java.math`.
- **Serialization.** Serialize via whichever scale's accessor and factory suit you; `ratio` and
  `fromRatio` are the stored scale, so they round-trip with no conversion at all.

## Building

```bash
./gradlew check   # tests, 100% coverage gate, API dump, ktlint, detekt — the same as CI
```

The build pins itself to JDK 21 (`gradle/gradle-daemon-jvm.properties`) and downloads it if you do
not have it; the artifact targets Java 11.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md). Development is test-driven: every behaviour arrives as a
failing test first.

## License

[Apache-2.0](LICENSE)
