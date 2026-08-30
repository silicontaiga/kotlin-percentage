package io.github.silicontaiga.percentage

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PercentageConstructionTest : FunSpec({

    test("of(Int) carries the percent value") {
        Percentage.of(50).percentValue shouldBe 50.toBigDecimal()
    }

    test("of(Long) carries the percent value") {
        Percentage.of(50L).percentValue shouldBe 50.toBigDecimal()
    }

    test("of(Double) carries the percent value") {
        Percentage.of(12.5).percentValue shouldBe "12.5".toBigDecimal()
    }

    test("of(BigDecimal) carries the percent value") {
        Percentage.of(50.toBigDecimal()).percentValue shouldBe 50.toBigDecimal()
    }

    test("of(Double) takes the decimal the programmer wrote, not its binary expansion") {
        Percentage.of(0.1).percentValue shouldBe "0.1".toBigDecimal()
    }

    test("of(Double) rejects NaN") {
        shouldThrow<IllegalArgumentException> { Percentage.of(Double.NaN) }
    }

    test("of(Double) rejects positive infinity") {
        shouldThrow<IllegalArgumentException> { Percentage.of(Double.POSITIVE_INFINITY) }
    }

    test("of(Double) rejects negative infinity") {
        shouldThrow<IllegalArgumentException> { Percentage.of(Double.NEGATIVE_INFINITY) }
    }

    test("negative percentages are valid") {
        Percentage.of(-10).percentValue shouldBe (-10).toBigDecimal()
    }

    test("percentages above one hundred are valid") {
        Percentage.of(250).percentValue shouldBe 250.toBigDecimal()
    }
})
