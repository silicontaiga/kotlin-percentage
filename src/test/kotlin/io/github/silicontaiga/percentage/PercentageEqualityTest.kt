package io.github.silicontaiga.percentage

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class PercentageEqualityTest : FunSpec({

    test("percentages of equal mathematical value are equal however they were written") {
        Percentage.of("50.00".toBigDecimal()) shouldBe Percentage.of(50)
    }

    test("percentages of equal mathematical value share a hash code") {
        Percentage.of("50.00".toBigDecimal()).hashCode() shouldBe Percentage.of(50).hashCode()
    }

    test("percentages of different mathematical value are not equal") {
        Percentage.of(50) shouldNotBe Percentage.of(20)
    }

    test("a percentage equals itself") {
        val fifty = Percentage.of(50)

        fifty shouldBe fifty
    }

    test("a percentage never equals a value of another type") {
        Percentage.of(50) shouldNotBe "50%"
    }

    test("an Int and a Double of the same value build equal percentages") {
        Percentage.of(50) shouldBe Percentage.of(50.0)
    }

    test("fifty percent is the ratio one half") {
        Percentage.of(50) shouldBe Percentage.fromRatio(0.5)
    }

    test("the percent value accessor strips trailing zeros") {
        Percentage.of("50.00".toBigDecimal()).percentValue shouldBe 50.toBigDecimal()
    }

    test("normalization never falls back to scientific notation") {
        Percentage.of("50.0".toBigDecimal()).toString() shouldBe "50%"
    }
})
