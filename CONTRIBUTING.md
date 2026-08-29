# Contributing to kotlin-percentage

Thanks for your interest. This is a small, deliberately narrow library: what it does is settled,
and its infrastructure is fully automated. That shapes how changes land.

## Before you write code

- **Behaviour is decided before it is coded.** The [API reference](https://silicontaiga.github.io/kotlin-percentage/latest/)
  is the description of record for what the library does. If a change alters *what the library
  does* rather than how it does it, open an issue describing the behaviour and the reasoning
  first, rather than a pull request that quietly redefines it. Maintainers will say yes or no on
  the design before anyone spends time on an implementation.
- **Non-goals are decisions, not gaps.** Formatting and parsing, range validation, multiplatform
  support and serialization are out of scope on purpose. So are `infix` functions (they bind
  looser than arithmetic operators, so the natural spelling silently regroups the expression) and
  `Int`/`Long`/`Double` overloads on application and derivation (they would hide a lossy
  conversion inside the library). Proposals to add them are welcome as issues; each will be
  weighed against the reasoning that excluded it.

## The loop

Development is test-driven, enforced socially in review as well as mechanically by the coverage
gate:

1. **Red** — write the failing test for the behaviour, in Kotest (`FunSpec`), named for the
   behaviour it pins.
2. **Green** — write the smallest implementation that passes.
3. **Refactor** — with the suite green.

Pull requests are expected to show that shape: tests and implementation together, never a
behaviour change without the test that pins it. Where a behaviour is an algebraic law
(`p.of(n) == n * p.ratio`, `sum`/`average` invariants, `equals`/`compareTo` agreement), prefer a
**property-based** test with Kotest Property over a handful of examples.

## Running the checks

```bash
./gradlew check        # everything CI runs: tests, coverage, API dump, ktlint, detekt
./gradlew ktlintFormat # fix formatting
./gradlew apiDump      # regenerate api/kotlin-percentage.api after an intentional API change
```

`./gradlew check` locally is exactly the CI gate, so a green local run means a green pull request.
You do not need a particular JDK installed: the build pins its own daemon and toolchain to JDK 21
(`gradle/gradle-daemon-jvm.properties`) and downloads it on first run. The artifact targets Java 11,
and CI additionally runs the suite on Java 11 and 17.

That pin is deliberate on both ends. JDK 21 is this project's build baseline, and detekt 1.23's
bundled compiler cannot parse the version string of JDK 24 and newer — running the build on a
newer JDK fails in `:detekt` with a bare version number as the message. Raise the pin only
together with a detekt version that supports the newer JDK.

What the gate enforces:

| Check | Rule |
|---|---|
| `test` | Kotest suite on the JUnit Platform; also run on Java 11 and 17 in CI |
| `koverVerify` | **100%** line *and* branch coverage — the build fails below it |
| `apiCheck` | the committed `api/kotlin-percentage.api` must match the compiled public ABI |
| `ktlintCheck` | ktlint, official code style |
| `detekt` | default ruleset plus this repo's deviations in `detekt.yml`, KDoc required on public API |

Explicit API mode is on: every public declaration needs an explicit visibility, an explicit return
type, and KDoc.

## The API dump

`api/kotlin-percentage.api` is the public ABI, committed. A signature change shows up as a diff
there, which is the point: it makes an accidental breaking change impossible to merge quietly.
When a change to the API is intended, run `./gradlew apiDump` and commit the result **in the same
pull request** — reviewers read that file as the summary of what consumers will see.

## Pull requests

- Branch from `main`; `main` is protected and only moves through pull requests with green CI.
- Keep the pull request title meaningful: release notes are generated from merged PR titles.
- Renovate keeps dependencies current; its patch-level PRs auto-merge when CI is green.

## Releases

Maintainers only, and entirely automated: push an annotated `vX.Y.Z` tag on `main` and the release
workflow runs the full check, publishes to Maven Central, publishes the versioned API docs and
creates the GitHub release. Every push to `main` publishes a `-SNAPSHOT` as well.

Versioning is SemVer with an enforced meaning: **MAJOR** = a breaking ABI change (the API dump is
the arbiter), **MINOR** = additive API, **PATCH** = behaviour-preserving fixes.

Until `1.0.0` the project stays on the `0.x` line, which SemVer defines as initial development:
the public API is not yet declared stable. The convention there shifts each slot down one — a
breaking change bumps the **minor** (`0.1.3` → `0.2.0`), and additions and fixes bump the
**patch**. The API dump still decides which of the two a change is. Reaching `1.0.0` is the act of
declaring the surface stable, after which the rule above applies literally.

## Code of conduct

Be decent. Discuss the design, not the person.
