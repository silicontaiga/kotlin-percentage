package io.github.silicontaiga.percentage

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class PercentageArithmeticTest : FunSpec({

    test("adding two percentages adds their percent values") {
        30.percent + 20.percent shouldBe 50.percent
    }

    test("subtracting two percentages subtracts their percent values") {
        30.percent - 20.percent shouldBe 10.percent
    }

    test("negating a percentage flips its sign") {
        -20.percent shouldBe (-20).percent
    }

    test("addition is commutative") {
        checkAll(Arb.percentages, Arb.percentages) { left, right ->
            left + right shouldBe right + left
        }
    }

    test("addition is exact, so subtracting the addend restores the original") {
        checkAll(Arb.percentages, Arb.percentages) { left, right ->
            (left + right) - right shouldBe left
        }
    }

    test("negation is its own inverse") {
        checkAll(Arb.percentages) { percentage ->
            -(-percentage) shouldBe percentage
        }
    }

    test("multiplying by an Int scales the percent value") {
        20.percent * 2 shouldBe 40.percent
    }

    test("multiplying by a Long scales the percent value") {
        20.percent * 2L shouldBe 40.percent
    }

    test("multiplying by a Double scales the percent value") {
        20.percent * 1.5 shouldBe 30.percent
    }

    test("multiplying by a BigDecimal scales the percent value") {
        20.percent * 2.toBigDecimal() shouldBe 40.percent
    }

    test("an Int on the left multiplies the same way") {
        2 * 20.percent shouldBe 40.percent
    }

    test("a Long on the left multiplies the same way") {
        2L * 20.percent shouldBe 40.percent
    }

    test("a Double on the left multiplies the same way") {
        1.5 * 20.percent shouldBe 30.percent
    }

    test("a BigDecimal on the left multiplies the same way") {
        2.toBigDecimal() * 20.percent shouldBe 40.percent
    }

    test("scalar multiplication is exact and mirrors on both sides") {
        checkAll(Arb.percentages, Arb.bigDecimalPercentages) { percentage, factor ->
            percentage * factor shouldBe factor * percentage
        }
    }

    test("multiplying two percentages multiplies them on the ratio scale") {
        50.percent * 50.percent shouldBe 25.percent
    }

    test("one hundred percent is the identity for multiplying two percentages") {
        checkAll(Arb.percentages) { percentage ->
            percentage * 100.percent shouldBe percentage
        }
    }

    test("multiplying two percentages agrees with multiplying their ratios") {
        checkAll(Arb.percentages, Arb.percentages) { left, right ->
            (left * right).ratio shouldBe (left.ratio * right.ratio).normalized()
        }
    }

    test("dividing by an Int scales the percent value down") {
        20.percent / 2 shouldBe 10.percent
    }

    test("dividing by a Long scales the percent value down") {
        20.percent / 2L shouldBe 10.percent
    }

    test("dividing by a Double scales the percent value down") {
        20.percent / 0.5 shouldBe 40.percent
    }

    test("dividing by a BigDecimal scales the percent value down") {
        20.percent / 2.toBigDecimal() shouldBe 10.percent
    }

    test("a non-terminating division carries DECIMAL128's thirty-four significant digits") {
        (10.percent / 3).percentValue.precision() shouldBe 34
    }

    test("an explicit context narrows a non-terminating division") {
        10.percent.div(3, MathContext(3, RoundingMode.HALF_EVEN)) shouldBe "3.33".toBigDecimal().percent
    }

    test("an explicit context chooses the rounding mode, so a tie can go to even") {
        25.percent.div(2, MathContext(2, RoundingMode.HALF_EVEN)) shouldBe 12.percent
    }

    test("an explicit context applies to a Long divisor") {
        10.percent.div(3L, MathContext(3, RoundingMode.HALF_EVEN)) shouldBe "3.33".toBigDecimal().percent
    }

    test("an explicit context applies to a Double divisor") {
        10.percent.div(3.0, MathContext(3, RoundingMode.HALF_EVEN)) shouldBe "3.33".toBigDecimal().percent
    }

    test("an explicit context applies to a BigDecimal divisor") {
        10.percent.div(3.toBigDecimal(), MathContext(3, RoundingMode.HALF_EVEN)) shouldBe "3.33".toBigDecimal().percent
    }

    test("dividing by zero is undefined and throws") {
        shouldThrow<ArithmeticException> { 20.percent / 0 }
    }

    test("dividing two percentages gives a dimensionless number") {
        50.percent / 25.percent shouldBe 2.toBigDecimal()
    }

    test("dividing two percentages accepts an explicit context") {
        1.percent.div(3.percent, MathContext(3, RoundingMode.HALF_EVEN)) shouldBe "0.333".toBigDecimal()
    }

    test("dividing by a zero percentage is undefined and throws") {
        shouldThrow<ArithmeticException> { 20.percent / 0.percent }
    }

    test("a percentage divided by itself is one") {
        checkAll(Arb.nonZeroPercentages) { percentage ->
            percentage / percentage shouldBe BigDecimal.ONE
        }
    }

    test("post-increment steps up by one percentage point and yields the old value") {
        var percentage = 10.percent

        val before = percentage++

        before shouldBe 10.percent
        percentage shouldBe 11.percent
    }

    test("pre-increment steps up by one percentage point and yields the new value") {
        var percentage = 10.percent

        val after = ++percentage

        after shouldBe 11.percent
        percentage shouldBe 11.percent
    }

    test("decrement steps down by one percentage point") {
        var percentage = 10.percent

        percentage--

        percentage shouldBe 9.percent
    }

    test("a step is one percentage point, not one percent of the value") {
        checkAll(Arb.percentages) { percentage ->
            var stepped = percentage
            stepped++

            stepped shouldBe percentage + 1.percent
        }
    }
})
