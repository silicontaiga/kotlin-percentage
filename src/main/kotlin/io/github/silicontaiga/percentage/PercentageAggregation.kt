package io.github.silicontaiga.percentage

import java.math.BigDecimal
import java.math.MathContext

/**
 * Returns the sum of these percentages, so `listOf(10.percent, 20.percent, 30.percent).sum()` is `60%`.
 *
 * Exact. Summing nothing gives `0%`, which is the identity for addition.
 */
public fun Iterable<Percentage>.sum(): Percentage = fold(Percentage.of(BigDecimal.ZERO)) { total, percentage -> total + percentage }

/**
 * Returns the unweighted arithmetic mean of these percentages, so
 * `listOf(10.percent, 20.percent, 30.percent).average()` is `20%`.
 *
 * A genuine division, so it rounds to 34 significant digits, half-even — `MathContext.DECIMAL128`.
 *
 * @throws ArithmeticException if there are no percentages; the mean of nothing is mathematically
 * undefined, and exact decimal arithmetic has no `NaN` to return instead.
 */
public fun Iterable<Percentage>.average(): Percentage = average(Percentage.DEFAULT_CONTEXT)

/**
 * Returns the unweighted arithmetic mean of these percentages, rounding to [context].
 *
 * @throws ArithmeticException if there are no percentages, or if [context] cannot represent the result.
 */
public fun Iterable<Percentage>.average(context: MathContext): Percentage {
    // Materialized once: an Iterable may be single-pass, and the mean needs both the total and the count.
    val percentages = toList()
    if (percentages.isEmpty()) {
        throw ArithmeticException("The average of no percentages is undefined")
    }
    return percentages.sum().div(percentages.size, context)
}
