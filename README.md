# kotlin-percentage

[![Maven Central](https://img.shields.io/maven-central/v/io.github.silicontaiga/kotlin-percentage?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.silicontaiga/kotlin-percentage)
[![CI](https://github.com/silicontaiga/kotlin-percentage/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/silicontaiga/kotlin-percentage/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/silicontaiga/kotlin-percentage/branch/main/graph/badge.svg)](https://codecov.io/gh/silicontaiga/kotlin-percentage)
[![docs](https://img.shields.io/website?url=https%3A%2F%2Fsilicontaiga.github.io%2Fkotlin-percentage%2Flatest%2F&label=docs)](https://silicontaiga.github.io/kotlin-percentage/latest/)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue)](LICENSE)
[![Kotlin](https://img.shields.io/badge/dynamic/toml?url=https%3A%2F%2Fraw.githubusercontent.com%2Fsilicontaiga%2Fkotlin-percentage%2Fmain%2Fgradle%2Flibs.versions.toml&query=%24.versions.kotlin&label=Kotlin&logo=kotlin&color=7F52FF)](gradle/libs.versions.toml)

Exact, explicit percentage arithmetic for Kotlin/JVM.

## Why not just `BigDecimal`

Reaching for a general-purpose number here is **primitive obsession**, and the missing piece is the
scale. Twenty percent is written `0.2`, `20`, `200` or `2000` — as a ratio, a percent value, per
mille or in basis points — and all four are correct. A `BigDecimal` holding one of them is
indistinguishable from a `BigDecimal` holding any other, so which scale a figure is on has to live
somewhere else: a parameter name, a column header, a comment, a convention two functions away. None
of those travels with the figure, so nothing can check it:

```kotlin
fun priceAfterDiscount(price: BigDecimal, discount: BigDecimal): BigDecimal =
    price - price * discount                     // wants a ratio; nothing says so

val price = 100.toBigDecimal()
priceAfterDiscount(price, "0.2".toBigDecimal())  // 80.0 — the ratio it wanted
priceAfterDiscount(price, 20.toBigDecimal())     // -1900 — twenty percent, said the other way
```

That is the same figure written on the scale a human says it on, handed to code expecting the scale
the arithmetic wants — a factor of a hundred out, every time, and silently.

`Percentage` is a **value object**: the quantity and the scale it is on are one inseparable value.
Each factory states the scale its argument is on, each accessor states the scale it returns, and
what travels in between is the quantity rather than a bare number:

```kotlin
20.percent.of(price)          // 20% of price
200.perMille.of(price)        // the same 20%, quoted as per mille quotes it
2000.permyriad.of(price)      // the same 20%, quoted in basis points

20.percent == 200.perMille    // true — the scale is a spelling, not an identity
```

A rate quoted in basis points therefore enters the program under the name it was quoted in, and the
conversion happens once, at the edge, where somebody can see it.

While the version stays below `1.0.0` the API may still change — see
[versioning](CONTRIBUTING.md#releases).

## Install

```kotlin
dependencies {
    implementation("io.github.silicontaiga:kotlin-percentage:0.1.0")
}
```

The Maven Central badge above shows the current release.

`percent`, `perMille`, `permyriad`, `perCentMille`, `of`, `increasedBy`, `decreasedBy` and
`asPercentageOf` are all extensions, so the star import is what makes them appear on `Int` and
`BigDecimal`:

```kotlin
import io.github.silicontaiga.percentage.*
```

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

## Quick start

```kotlin
import io.github.silicontaiga.percentage.*

20.percent.of(250.toBigDecimal())                            // 50
7.percent.of("100.50".toBigDecimal())                        // 7.035
250.toBigDecimal().increasedBy(20.percent)                   // 300
250.toBigDecimal().decreasedBy(20.percent)                   // 200

50.toBigDecimal().asPercentageOf(250.toBigDecimal())         // 20%
Percentage.changeBetween(from = 80.toBigDecimal(), to = 100.toBigDecimal())   // 25%
Percentage.changeBetween(from = 100.toBigDecimal(), to = 80.toBigDecimal())   // -20%

listOf(10.percent, 20.percent, 30.percent).sum()             // 60%
listOf(10.percent, 20.percent, 30.percent).average()         // 20%
```

`changeBetween` reads differently in each direction, so it takes its arguments by name and the
direction stays visible at the call site.

Percentages combine with each other as well as with bases:

```kotlin
20.percent + 5.percent          // 25%
20.percent * 2                  // 40%   — and 2 * 20.percent, either way round
20.percent / 2                  // 10%
-(20.percent)                   // -20%
50.percent * 50.percent         // 25%   — a ratio times a ratio is a ratio
50.percent / 25.percent         // 2     — a ratio over a ratio is a plain number
```

Note the last pair: **`p * q` is a `Percentage`, `p / q` is a `BigDecimal`.** Half of a half is a
quarter, but "how many times does 25% fit into 50%" is a count, not a percentage.

`p++` and `p--` step by one **percentage point**, matching Kotlin's own `BigDecimal.inc()`, so
`10.percent++` is `11%` — not 10% larger.

## Scales: every scale is a name

A `Percentage` stores the ratio. Every other scale is a named factory going in and an accessor of
the same name coming out, so no number crosses the boundary without saying which scale it is on:

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

250.permyriad == 2.5.percent      // true — the scale is a spelling, not an identity
```

| scale | unit | 50% is | reached from the ratio by |
|---|---|---|---|
| ratio | — | `0.5` | stored as-is |
| per cent | `%` | `50` | ×100 |
| per mille | `‰` | `500` | ×1000 |
| permyriad (basis point) | `‱` | `5000` | ×10000 |
| per cent mille | `pcm` | `50000` | ×100000 |

Every literal and factory takes `Int`, `Long`, `Double` or `BigDecimal`, so `12.5.percent` and
`Percentage.of(50)` are both first-class.

## Exact arithmetic

Values are backed by `java.math.BigDecimal`. Construction, addition, subtraction, negation,
multiplication and application are exact — every scale conversion only moves the decimal point, and
applying a percentage multiplies the base by the stored ratio, so `of` takes no rounding parameter
at all:

```kotlin
19.percent.of("12.34".toBigDecimal())    // 2.3446 — exact, not rounded to 2.34
```

Only genuine divisions can produce a non-terminating expansion. Those default to
`MathContext.DECIMAL128` (34 digits, `HALF_EVEN`) and each has an overload taking an explicit
`MathContext`:

```kotlin
1.toBigDecimal().asPercentageOf(3.toBigDecimal())    // 33.33333333333333333333333333333333%
```

The dividing operations are exactly `asPercentageOf`, `changeBetween`, `average`, `p / n` and
`p / q`; everything else is exact.

Operations that are mathematically undefined throw `ArithmeticException` rather than returning a
sentinel — there is no `NaN` in exact JVM arithmetic:

```kotlin
x.asPercentageOf(BigDecimal.ZERO)                                          // throws
Percentage.changeBetween(from = BigDecimal.ZERO, to = 100.toBigDecimal())  // throws
emptyList<Percentage>().average()                                          // throws
```

`sum()` on an empty list is `0%` instead, because zero is the identity for addition and the mean of
nothing is not.

A `Double` is read as the decimal you wrote it as, not as its binary expansion — construction goes
through `BigDecimal.valueOf` rather than `BigDecimal(double)`, so a literal cannot smuggle a
floating-point artifact into a library whose premise is exactness. `NaN` and the infinities have no
exact decimal at all, and are rejected with `IllegalArgumentException`:

```kotlin
BigDecimal(0.1)          // 0.1000000000000000055511151231257827021181583404541015625
0.1.percent              // 0.1%
```

## Two percentages are equal when they are worth the same

`BigDecimal` is the well-known counterexample: `50` and `50.00` are the same number but not
`equals`, so a `HashSet` keeps both copies and `contains` misses. Anything built on it inherits that
unless it does something about it. This does — the stored ratio is normalized on construction, so
each mathematical value has exactly one representative:

```kotlin
BigDecimal("50") == BigDecimal("50.00")                    // false
Percentage.of(50) == Percentage.of("50.00".toBigDecimal()) // true
Percentage.of(50) == Percentage.fromRatio(0.5)             // true — however it was built

setOf(BigDecimal("50"), BigDecimal("50.00")).size          // 2
setOf(Percentage.of(50), 50.percent).size                  // 1
```

`equals`, `hashCode` and `compareTo` all agree, so a `Percentage` is safe as a map key or in a set
with no normalising step of your own, and sorting and equality never disagree about two values.

## Increase and decrease do not cancel

Not a quirk of this library — it is what percentages do, and it catches people regularly. A decrease
and an increase of the same percentage are taken of **different bases**:

```kotlin
100.toBigDecimal().decreasedBy(20.percent).increasedBy(20.percent)   // 96, not 100
1000.toBigDecimal().decreasedBy(10.percent).increasedBy(10.percent)  // 990
```

Nothing here tries to paper over that by making the two inverse. Chaining the calls is what puts the
second base in view: the `20%` is of `80`, because `80` is what it is written after.

"Add 20% and 5%" splits the same way — one rate of 25%, or 20% applied and then 5% applied to the
result. Both readings stay expressible, and each says which it is:

```kotlin
250.toBigDecimal().increasedBy(20.percent + 5.percent)               // 312.5 — one rate of 25%
250.toBigDecimal().increasedBy(20.percent).increasedBy(5.percent)    // 315   — applied in turn

20.percent.of(250.toBigDecimal()) + BigDecimal(50)                   // 100 — percentage, then addition
20.percent.of((250 + 50).toBigDecimal())                             // 60  — addition, then percentage
```

For a single rate that does reverse a change, derive it rather than assuming one: `changeBetween`
gives the percentage that actually maps one figure to another.

## What does not compile

Numbers enter and leave application and derivation as `BigDecimal` only, so the caller performs
every conversion in view. No function is `infix`, so no expression can regroup itself:

```kotlin
250.increasedBy(20.percent)                             // error: no Int receiver
20.percent.of(250)                                      // error: no Int argument
250.toBigDecimal().increasedBy(20.percent) + 5.percent  // error: BigDecimal + Percentage
20.percent.of(x).of(y)                                  // error: no `of` on BigDecimal
```

Construction and the scalar operators *do* take `Int`, `Long` and `Double`, because those
conversions cannot lose anything. It is only application and derivation — where a base could quietly
change representation — that insist on `BigDecimal`.

These rejections are enforced by a test that runs a real compiler over each snippet, so they are
part of the contract rather than a description of it.

## Using it with money

Applying a percentage to a monetary amount needs the amount to round to its currency's scale, which
is a money concern rather than a percentage one. The sibling library
[`kotlin-money`](https://github.com/silicontaiga/kotlin-money) depends on this one and adds those
overloads:

```kotlin
import io.github.silicontaiga.money.*
import io.github.silicontaiga.percentage.*

19.percent.of(12.34.eur)           // 2.34 EUR — rounded to the currency's scale
250.eur.increasedBy(19.percent)    // 297.50 EUR
50.eur.asPercentageOf(250.eur)     // 20%
```

Watch the difference in `of`: applied to a `BigDecimal` it is exact and offers no rounding
parameter, while applied to a `Money` it rounds — 19% of `12.34` is `2.3446`, and a monetary amount
has to land on a payable figure.

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

Every operation that rounds also has an overload taking an explicit `MathContext`.

Negative percentages and percentages above 100 are valid — a `-20%` change and a `150%` uplift are
both ordinary. Instances are immutable and thread-safe.

The table above is a summary; the generated API reference documents every overload, its rounding
and what it throws. It is published per release, so a version can be read at the version you
actually depend on:

- [Latest release](https://silicontaiga.github.io/kotlin-percentage/latest/)
- A specific one at `https://silicontaiga.github.io/kotlin-percentage/<version>/` — for example
  [0.1.0](https://silicontaiga.github.io/kotlin-percentage/0.1.0/)

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
