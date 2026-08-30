package io.github.silicontaiga.percentage

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class PercentageDerivationTest : FunSpec({

    test("a part of a base is derived as a percentage") {
        50.toBigDecimal().asPercentageOf(250.toBigDecimal()) shouldBe 20.percent
    }

    test("a non-terminating share rounds to DECIMAL128") {
        1.toBigDecimal().asPercentageOf(3.toBigDecimal()) shouldBe
            "33.33333333333333333333333333333333".toBigDecimal().percent
    }

    test("an explicit context narrows a non-terminating share") {
        1.toBigDecimal().asPercentageOf(3.toBigDecimal(), MathContext(4, RoundingMode.HALF_EVEN)) shouldBe
            "33.33".toBigDecimal().percent
    }

    test("a share of a zero base is undefined and throws") {
        shouldThrow<ArithmeticException> { 50.toBigDecimal().asPercentageOf(BigDecimal.ZERO) }
    }

    test("a share of a zero base throws under an explicit context too") {
        shouldThrow<ArithmeticException> {
            50.toBigDecimal().asPercentageOf(BigDecimal.ZERO, MathContext(4, RoundingMode.HALF_EVEN))
        }
    }

    test("anything is one hundred percent of itself") {
        checkAll(Arb.nonZeroBigDecimals) { number ->
            number.asPercentageOf(number) shouldBe 100.percent
        }
    }

    test("an increase is derived as a positive change") {
        Percentage.changeBetween(from = 80.toBigDecimal(), to = 100.toBigDecimal()) shouldBe 25.percent
    }

    test("a decrease is derived as a negative change") {
        Percentage.changeBetween(from = 100.toBigDecimal(), to = 80.toBigDecimal()) shouldBe (-20).percent
    }

    test("an explicit context narrows a non-terminating change") {
        Percentage.changeBetween(
            from = 3.toBigDecimal(),
            to = 4.toBigDecimal(),
            context = MathContext(4, RoundingMode.HALF_EVEN),
        ) shouldBe "33.33".toBigDecimal().percent
    }

    test("a change from zero is undefined and throws") {
        shouldThrow<ArithmeticException> {
            Percentage.changeBetween(from = BigDecimal.ZERO, to = 100.toBigDecimal())
        }
    }

    test("a change from zero throws under an explicit context too") {
        shouldThrow<ArithmeticException> {
            Percentage.changeBetween(
                from = BigDecimal.ZERO,
                to = 100.toBigDecimal(),
                context = MathContext(4, RoundingMode.HALF_EVEN),
            )
        }
    }

    test("no change between equal numbers") {
        checkAll(Arb.nonZeroBigDecimals) { number ->
            Percentage.changeBetween(from = number, to = number) shouldBe 0.percent
        }
    }

    test("a derived change, applied back to the starting number, reaches the target") {
        checkAll(Arb.nonZeroBigDecimals) { from ->
            val change = Percentage.changeBetween(from = from, to = from * 2.toBigDecimal())

            change shouldBe 100.percent
            from.increasedBy(change) shouldBe (from * 2.toBigDecimal()).normalized()
        }
    }
})
