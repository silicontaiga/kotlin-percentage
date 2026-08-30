package io.github.silicontaiga.percentage

import java.math.BigDecimal
import java.math.MathContext

/**
 * Returns this number's share of [base], so `50.toBigDecimal().asPercentageOf(250.toBigDecimal())`
 * is `20%`.
 *
 * A genuine division, so it rounds to 34 significant digits, half-even — `MathContext.DECIMAL128`.
 *
 * @throws ArithmeticException if [base] is zero; a share of nothing is mathematically undefined, and
 * exact decimal arithmetic has no `NaN` to return instead.
 */
public fun BigDecimal.asPercentageOf(base: BigDecimal): Percentage = asPercentageOf(base, Percentage.DEFAULT_CONTEXT)

/**
 * Returns this number's share of [base], rounding to [context].
 *
 * @throws ArithmeticException if [base] is zero, or if [context] cannot represent the result.
 */
public fun BigDecimal.asPercentageOf(
    base: BigDecimal,
    context: MathContext,
): Percentage = Percentage.fromRatio(divide(base, context))
