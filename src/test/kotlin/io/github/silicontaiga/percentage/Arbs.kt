package io.github.silicontaiga.percentage

import io.kotest.property.Arb
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import java.math.BigDecimal

/**
 * Percent values spanning the range this library is used over — negative, zero and above one
 * hundred — at a mix of scales, so the generated set exercises normalization rather than
 * base numbers only.
 */
internal val Arb.Companion.bigDecimalPercentages: Arb<BigDecimal>
    get() =
        int(0..6).flatMap { scale ->
            long(-1_000_000L..1_000_000L).map { unscaled -> BigDecimal.valueOf(unscaled, scale) }
        }

/** [Percentage] values built from [bigDecimalPercentages]. */
internal val Arb.Companion.percentages: Arb<Percentage>
    get() = bigDecimalPercentages.map { Percentage.of(it) }

/** [Percentage] values built from [nonZeroBigDecimals], for use as divisors. */
internal val Arb.Companion.nonZeroPercentages: Arb<Percentage>
    get() = nonZeroBigDecimals.map { Percentage.of(it) }

/** Like [bigDecimalPercentages], but never zero — for divisors and for wholes. */
internal val Arb.Companion.nonZeroBigDecimals: Arb<BigDecimal>
    get() =
        int(0..6).flatMap { scale ->
            long(1L..1_000_000L).flatMap { magnitude ->
                int(0..1).map { sign ->
                    BigDecimal.valueOf(if (sign == 0) magnitude else -magnitude, scale)
                }
            }
        }
