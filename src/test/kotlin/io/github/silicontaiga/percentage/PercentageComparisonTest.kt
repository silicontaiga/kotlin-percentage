package io.github.silicontaiga.percentage

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll

class PercentageComparisonTest : FunSpec({

    test("a smaller percentage orders before a larger one") {
        (10.percent < 20.percent) shouldBe true
    }

    test("a larger percentage orders after a smaller one") {
        (20.percent > 10.percent) shouldBe true
    }

    test("percentages order by mathematical value, so maxOf picks the larger") {
        maxOf(10.percent, 20.percent) shouldBe 20.percent
    }

    test("negative percentages order below zero") {
        ((-10).percent < 0.percent) shouldBe true
    }

    test("equality and ordering agree on mathematical value") {
        checkAll(Arb.percentages, Arb.percentages) { left, right ->
            (left == right) shouldBe (left.compareTo(right) == 0)
        }
    }

    test("ordering is consistent with the percent values") {
        checkAll(Arb.percentages, Arb.percentages) { left, right ->
            left.compareTo(right) shouldBe left.percentValue.compareTo(right.percentValue)
        }
    }
})
