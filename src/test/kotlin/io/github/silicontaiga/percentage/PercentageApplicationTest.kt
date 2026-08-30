package io.github.silicontaiga.percentage

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll

class PercentageApplicationTest : FunSpec({

    test("applying a percentage to a number takes that share of it") {
        20.percent.of(250.toBigDecimal()) shouldBe 50.toBigDecimal()
    }

    test("application is exact, carrying every decimal place through") {
        7.percent.of("100.50".toBigDecimal()) shouldBe "7.035".toBigDecimal()
    }

    test("increasing a number adds that share of it") {
        250.toBigDecimal().increasedBy(20.percent) shouldBe 300.toBigDecimal()
    }

    test("decreasing a number subtracts that share of it") {
        250.toBigDecimal().decreasedBy(20.percent) shouldBe 200.toBigDecimal()
    }

    test("a decrease and an increase by the same percentage do not cancel") {
        100.toBigDecimal().decreasedBy(20.percent).increasedBy(20.percent) shouldBe 96.toBigDecimal()
    }

    test("increasing by a negative percentage decreases") {
        250.toBigDecimal().increasedBy(-(20.percent)) shouldBe 200.toBigDecimal()
    }

    test("application agrees with multiplying by the ratio") {
        checkAll(Arb.percentages, Arb.bigDecimalPercentages) { percentage, number ->
            percentage.of(number) shouldBe (number * percentage.ratio).normalized()
        }
    }

    test("increasing is the number plus its share") {
        checkAll(Arb.percentages, Arb.bigDecimalPercentages) { percentage, number ->
            number.increasedBy(percentage) shouldBe (number + percentage.of(number)).normalized()
        }
    }

    test("decreasing is the number minus its share") {
        checkAll(Arb.percentages, Arb.bigDecimalPercentages) { percentage, number ->
            number.decreasedBy(percentage) shouldBe (number - percentage.of(number)).normalized()
        }
    }

    test("increasing then decreasing by the negated percentage is exact and reversible") {
        checkAll(Arb.percentages, Arb.bigDecimalPercentages) { percentage, number ->
            number.increasedBy(percentage) shouldBe number.decreasedBy(-percentage)
        }
    }

    test("combining the percentages first is not the same as applying them in turn") {
        250.toBigDecimal().increasedBy(20.percent + 5.percent) shouldBe "312.5".toBigDecimal()
        250.toBigDecimal().increasedBy(20.percent).increasedBy(5.percent) shouldBe 315.toBigDecimal()
    }

    test("both readings of an ambiguous intent stay expressible and say which they are") {
        20.percent.of(250.toBigDecimal()) + 50.toBigDecimal() shouldBe 100.toBigDecimal()
        20.percent.of((250 + 50).toBigDecimal()) shouldBe 60.toBigDecimal()
    }

    test("comparison binds loosest, so an applied percentage compares as a number") {
        (20.percent.of(250.toBigDecimal()) < 100.toBigDecimal()) shouldBe true
    }

    test("a post-increment in the argument applies the old value") {
        var percentage = 10.percent

        val result = 100.toBigDecimal().increasedBy(percentage++)

        result shouldBe 110.toBigDecimal()
        percentage shouldBe 11.percent
    }

    test("a pre-increment in the argument applies the new value") {
        var percentage = 10.percent

        val result = 100.toBigDecimal().increasedBy(++percentage)

        result shouldBe 111.toBigDecimal()
        percentage shouldBe 11.percent
    }
})
