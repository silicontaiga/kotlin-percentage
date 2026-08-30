package io.github.silicontaiga.percentage

import java.math.BigDecimal

/**
 * Returns this number increased by [percentage], so `250.toBigDecimal().increasedBy(20.percent)` is `300`.
 *
 * Exact, and deliberately not the inverse of [decreasedBy]: decreasing `100` by twenty percent and
 * increasing the result by twenty percent again gives `96`, because the second percentage is taken of
 * a different number. Chaining the calls is what makes that visible.
 */
public fun BigDecimal.increasedBy(percentage: Percentage): BigDecimal = (this + percentage.of(this)).normalized()

/**
 * Returns this number decreased by [percentage], so `250.toBigDecimal().decreasedBy(20.percent)` is `200`.
 *
 * Exact. See [increasedBy] for why the two do not cancel.
 */
public fun BigDecimal.decreasedBy(percentage: Percentage): BigDecimal = (this - percentage.of(this)).normalized()
