package io.github.silicontaiga.percentage

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PercentageLiteralsTest : FunSpec({

    test("Int.percent builds the same percentage as of(Int)") {
        50.percent shouldBe Percentage.of(50)
    }

    test("Long.percent builds the same percentage as of(Long)") {
        50L.percent shouldBe Percentage.of(50L)
    }

    test("Double.percent builds the same percentage as of(Double)") {
        12.5.percent shouldBe Percentage.of(12.5)
    }

    test("BigDecimal.percent builds the same percentage as of(BigDecimal)") {
        "12.5".toBigDecimal().percent shouldBe Percentage.of("12.5".toBigDecimal())
    }

    test("perMille builds the same percentage as fromPerMille") {
        25.perMille shouldBe Percentage.fromPerMille(25)
        25L.perMille shouldBe Percentage.fromPerMille(25)
        25.0.perMille shouldBe Percentage.fromPerMille(25)
        25.toBigDecimal().perMille shouldBe Percentage.fromPerMille(25)
    }

    test("permyriad builds the same percentage as fromPermyriad") {
        250.permyriad shouldBe Percentage.fromPermyriad(250)
        250L.permyriad shouldBe Percentage.fromPermyriad(250)
        250.0.permyriad shouldBe Percentage.fromPermyriad(250)
        250.toBigDecimal().permyriad shouldBe Percentage.fromPermyriad(250)
    }

    test("perCentMille builds the same percentage as fromPerCentMille") {
        2500.perCentMille shouldBe Percentage.fromPerCentMille(2500)
        2500L.perCentMille shouldBe Percentage.fromPerCentMille(2500)
        2500.0.perCentMille shouldBe Percentage.fromPerCentMille(2500)
        2500.toBigDecimal().perCentMille shouldBe Percentage.fromPerCentMille(2500)
    }

    test("the finer literals all name the same quantity") {
        25.perMille shouldBe 2.5.percent
        250.permyriad shouldBe 2.5.percent
        2500.perCentMille shouldBe 2.5.percent
    }
})
