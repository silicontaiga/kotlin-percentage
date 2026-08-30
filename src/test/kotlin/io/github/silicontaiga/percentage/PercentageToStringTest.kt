package io.github.silicontaiga.percentage

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PercentageToStringTest : FunSpec({

    test("renders the percent value followed by a percent sign") {
        Percentage.of(50).toString() shouldBe "50%"
    }

    test("renders a fractional value") {
        12.5.percent.toString() shouldBe "12.5%"
    }

    test("renders a negative value") {
        Percentage.of(-10).toString() shouldBe "-10%"
    }

    test("renders a large value without abbreviating") {
        Percentage.of(1_000_000).toString() shouldBe "1000000%"
    }

    test("renders a very small value without falling back to scientific notation") {
        Percentage.of("0.00000001".toBigDecimal()).toString() shouldBe "0.00000001%"
    }
})
