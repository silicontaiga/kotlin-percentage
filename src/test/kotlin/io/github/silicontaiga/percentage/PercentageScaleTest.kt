package io.github.silicontaiga.percentage

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll

class PercentageScaleTest : FunSpec({

    test("ratio reads the quantity as a plain multiplier") {
        Percentage.of(50).ratio shouldBe "0.5".toBigDecimal()
    }

    test("ratio of a fractional percentage") {
        Percentage.of(12.5).ratio shouldBe "0.125".toBigDecimal()
    }

    test("fromRatio(Int) reads a plain multiplier") {
        Percentage.fromRatio(1) shouldBe Percentage.of(100)
    }

    test("fromRatio(Long) reads a plain multiplier") {
        Percentage.fromRatio(2L) shouldBe Percentage.of(200)
    }

    test("fromRatio(Double) reads a plain multiplier") {
        Percentage.fromRatio(0.5) shouldBe Percentage.of(50)
    }

    test("fromRatio(BigDecimal) reads a plain multiplier") {
        Percentage.fromRatio("0.125".toBigDecimal()) shouldBe Percentage.of(12.5)
    }

    test("fromRatio(Double) rejects a value with no exact decimal") {
        shouldThrow<IllegalArgumentException> { Percentage.fromRatio(Double.NaN) }
    }

    test("the ratio is the stored scale, so it round-trips with no conversion at all") {
        checkAll(Arb.bigDecimalPercentages) { value ->
            Percentage.fromRatio(value).ratio shouldBe value.normalized()
        }
    }

    test("per mille reads parts per thousand") {
        Percentage.of(50).perMille shouldBe 500.toBigDecimal()
    }

    test("permyriad reads parts per ten thousand") {
        Percentage.of(50).permyriad shouldBe 5000.toBigDecimal()
    }

    test("per cent mille reads parts per hundred thousand") {
        Percentage.of(50).perCentMille shouldBe 50_000.toBigDecimal()
    }

    test("fromPerMille reads parts per thousand, so 25 per mille is 2.5 percent") {
        Percentage.fromPerMille(25) shouldBe Percentage.of(2.5)
        Percentage.fromPerMille(25L) shouldBe Percentage.of(2.5)
        Percentage.fromPerMille(25.0) shouldBe Percentage.of(2.5)
        Percentage.fromPerMille(25.toBigDecimal()) shouldBe Percentage.of(2.5)
    }

    test("fromPermyriad reads parts per ten thousand, so 250 permyriad is 2.5 percent") {
        Percentage.fromPermyriad(250) shouldBe Percentage.of(2.5)
        Percentage.fromPermyriad(250L) shouldBe Percentage.of(2.5)
        Percentage.fromPermyriad(250.0) shouldBe Percentage.of(2.5)
        Percentage.fromPermyriad(250.toBigDecimal()) shouldBe Percentage.of(2.5)
    }

    test("fromPerCentMille reads parts per hundred thousand, so 2500 pcm is 2.5 percent") {
        Percentage.fromPerCentMille(2500) shouldBe Percentage.of(2.5)
        Percentage.fromPerCentMille(2500L) shouldBe Percentage.of(2.5)
        Percentage.fromPerCentMille(2500.0) shouldBe Percentage.of(2.5)
        Percentage.fromPerCentMille(2500.toBigDecimal()) shouldBe Percentage.of(2.5)
    }

    test("a permyriad is a basis point, so 250 basis points is 2.5 percent") {
        250.permyriad.percentValue shouldBe "2.5".toBigDecimal()
    }

    test("every scale reads back numerically equal to what it was built with") {
        250.permyriad.permyriad shouldBe 250.toBigDecimal()
        25.perMille.perMille shouldBe 25.toBigDecimal()
        2500.perCentMille.perCentMille shouldBe 2500.toBigDecimal()
    }

    test("the finer scales are decimal-point shifts of one another") {
        checkAll(Arb.percentages) { percentage ->
            percentage.perMille shouldBe (percentage.percentValue * 10.toBigDecimal()).normalized()
            percentage.permyriad shouldBe (percentage.percentValue * 100.toBigDecimal()).normalized()
            percentage.perCentMille shouldBe (percentage.percentValue * 1000.toBigDecimal()).normalized()
        }
    }

    test("every scale round-trips through its own factory") {
        checkAll(Arb.percentages) { percentage ->
            Percentage.fromPerMille(percentage.perMille) shouldBe percentage
            Percentage.fromPermyriad(percentage.permyriad) shouldBe percentage
            Percentage.fromPerCentMille(percentage.perCentMille) shouldBe percentage
        }
    }

    test("the finer factories reject a Double with no exact decimal") {
        shouldThrow<IllegalArgumentException> { Percentage.fromPerMille(Double.NaN) }
        shouldThrow<IllegalArgumentException> { Percentage.fromPermyriad(Double.NaN) }
        shouldThrow<IllegalArgumentException> { Percentage.fromPerCentMille(Double.NaN) }
    }
})
