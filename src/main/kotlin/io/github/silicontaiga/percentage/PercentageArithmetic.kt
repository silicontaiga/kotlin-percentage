package io.github.silicontaiga.percentage

import java.math.BigDecimal

/**
 * Returns [percentage] scaled by this number, so `2 * 20.percent` is `40%`. Exact.
 *
 * The mirror of [Percentage.times], so a scalar reads naturally on either side.
 */
public operator fun Int.times(percentage: Percentage): Percentage = percentage * this

/**
 * Returns [percentage] scaled by this number, so `2L * 20.percent` is `40%`. Exact.
 *
 * The mirror of [Percentage.times], so a scalar reads naturally on either side.
 */
public operator fun Long.times(percentage: Percentage): Percentage = percentage * this

/**
 * Returns [percentage] scaled by this number, so `1.5 * 20.percent` is `30%`. Exact.
 *
 * The mirror of [Percentage.times], so a scalar reads naturally on either side.
 *
 * @throws IllegalArgumentException if this is `NaN` or infinite.
 */
public operator fun Double.times(percentage: Percentage): Percentage = percentage * this

/**
 * Returns [percentage] scaled by this number, so `2.toBigDecimal() * 20.percent` is `40%`. Exact.
 *
 * The mirror of [Percentage.times], so a scalar reads naturally on either side.
 */
public operator fun BigDecimal.times(percentage: Percentage): Percentage = percentage * this
