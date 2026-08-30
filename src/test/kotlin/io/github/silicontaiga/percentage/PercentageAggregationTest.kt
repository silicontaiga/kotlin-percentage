package io.github.silicontaiga.percentage

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import java.math.MathContext
import java.math.RoundingMode

class PercentageAggregationTest : FunSpec({

    test("summing adds the percent values") {
        listOf(10.percent, 20.percent, 30.percent).sum() shouldBe 60.percent
    }

    test("summing nothing gives zero percent") {
        emptyList<Percentage>().sum() shouldBe 0.percent
    }

    test("averaging takes the plain arithmetic mean of the values") {
        listOf(10.percent, 20.percent, 30.percent).average() shouldBe 20.percent
    }

    test("averaging nothing is undefined and throws") {
        shouldThrow<ArithmeticException> { emptyList<Percentage>().average() }
    }

    test("averaging nothing throws under an explicit context too") {
        shouldThrow<ArithmeticException> {
            emptyList<Percentage>().average(MathContext(4, RoundingMode.HALF_EVEN))
        }
    }

    test("a non-terminating average rounds to DECIMAL128") {
        listOf(10.percent, 0.percent, 0.percent).average().percentValue.precision() shouldBe 34
    }

    test("an explicit context narrows a non-terminating average") {
        listOf(10.percent, 0.percent, 0.percent).average(MathContext(3, RoundingMode.HALF_EVEN)) shouldBe
            "3.33".toBigDecimal().percent
    }

    test("the average of a single percentage is that percentage") {
        checkAll(Arb.percentages) { percentage ->
            listOf(percentage).average() shouldBe percentage
        }
    }

    test("summing is exact and independent of order") {
        checkAll(Arb.list(Arb.percentages, 0..12)) { percentages ->
            percentages.sum() shouldBe percentages.reversed().sum()
        }
    }

    test("summing agrees with folding addition over the elements") {
        checkAll(Arb.list(Arb.percentages, 0..12)) { percentages ->
            percentages.sum() shouldBe percentages.fold(0.percent) { total, next -> total + next }
        }
    }
})
