// kctfork drives the compiler through its own experimental API. Opting in here, at the one file that
// touches it, keeps `allWarningsAsErrors` switched on for every other line of the project.
@file:OptIn(ExperimentalCompilerApi::class)

package io.github.silicontaiga.percentage

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.OutputStream

/**
 * The API's negative space: spellings that must *not* compile.
 *
 * These guarantees cannot be pinned any other way. The committed API dump proves an absent overload
 * by absence, but it records nothing about `infix`, so nothing else in the build would notice a
 * function acquiring that modifier — and with it the looser binding that silently regroups
 * `20.percent of 250 + 50` into twenty percent of three hundred.
 *
 * Every rejection is paired with the accepted spelling it differs from, so a snippet that failed for
 * some unrelated reason — a typo, a missing import — shows up as the positive case going red rather
 * than as a rejection passing for the wrong reason.
 */
class PercentageCompilationTest : FunSpec({

    test("a BigDecimal receiver is accepted by increasedBy") {
        compile("250.toBigDecimal().increasedBy(20.percent)").exitCode shouldBe KotlinCompilation.ExitCode.OK
    }

    test("an Int receiver is rejected by increasedBy") {
        val result = compile("250.increasedBy(20.percent)")

        result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
        result.messages shouldContain "increasedBy"
    }

    test("a BigDecimal argument is accepted by of") {
        compile("20.percent.of(250.toBigDecimal())").exitCode shouldBe KotlinCompilation.ExitCode.OK
    }

    test("an Int argument is rejected by of") {
        compile("20.percent.of(250)").exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
    }

    test("a percentage may be added to another percentage before it is applied") {
        compile("250.toBigDecimal().increasedBy(20.percent + 5.percent)").exitCode shouldBe
            KotlinCompilation.ExitCode.OK
    }

    test("a percentage may not be added to the number it was applied to") {
        compile("250.toBigDecimal().increasedBy(20.percent) + 5.percent").exitCode shouldBe
            KotlinCompilation.ExitCode.COMPILATION_ERROR
    }

    test("applying a percentage yields a plain number, which has no of to chain onto") {
        val result = compile("20.percent.of(250.toBigDecimal()).of(100.toBigDecimal())")

        result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
        result.messages shouldContain "of"
    }

    test("of is not infix, so it cannot be spelled without parentheses") {
        compile("20.percent of 250.toBigDecimal()").exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
    }

    test("increasedBy is not infix, so it cannot be spelled without parentheses") {
        compile("250.toBigDecimal() increasedBy 20.percent").exitCode shouldBe
            KotlinCompilation.ExitCode.COMPILATION_ERROR
    }
})

/** Compiles [expression] against this library, returning the compiler's verdict. */
private fun compile(expression: String) =
    KotlinCompilation()
        .apply {
            sources =
                listOf(
                    SourceFile.kotlin(
                        "Snippet.kt",
                        """
                        import io.github.silicontaiga.percentage.*

                        fun snippet(): Any? = $expression
                        """.trimIndent(),
                    ),
                )
            inheritClassPath = true
            messageOutputStream = OutputStream.nullOutputStream()
        }.compile()
