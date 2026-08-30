package io.github.silicontaiga.percentage

import java.math.BigDecimal

/**
 * This number read as a percent value, so `50.percent` is `50%`.
 *
 * The literal spelling of [Percentage.of].
 */
public val Int.percent: Percentage
    get() = Percentage.of(this)

/**
 * This number read as a percent value, so `50L.percent` is `50%`.
 *
 * The literal spelling of [Percentage.of].
 */
public val Long.percent: Percentage
    get() = Percentage.of(this)

/**
 * This number read as a percent value, so `12.5.percent` is `12.5%`.
 *
 * The literal spelling of [Percentage.of]. The value is read as the decimal it was written as, not
 * as its binary expansion.
 *
 * @throws IllegalArgumentException if this is `NaN` or infinite.
 */
public val Double.percent: Percentage
    get() = Percentage.of(this)

/**
 * This number read as a percent value, so `"12.5".toBigDecimal().percent` is `12.5%`.
 *
 * The literal spelling of [Percentage.of].
 */
public val BigDecimal.percent: Percentage
    get() = Percentage.of(this)

/**
 * This number read as parts per thousand, so `25.perMille` is `2.5%`.
 *
 * The literal spelling of [Percentage.fromPerMille].
 */
public val Int.perMille: Percentage
    get() = Percentage.fromPerMille(this)

/**
 * This number read as parts per thousand, so `25L.perMille` is `2.5%`.
 *
 * The literal spelling of [Percentage.fromPerMille].
 */
public val Long.perMille: Percentage
    get() = Percentage.fromPerMille(this)

/**
 * This number read as parts per thousand, so `25.0.perMille` is `2.5%`.
 *
 * The literal spelling of [Percentage.fromPerMille].
 *
 * @throws IllegalArgumentException if this is `NaN` or infinite.
 */
public val Double.perMille: Percentage
    get() = Percentage.fromPerMille(this)

/**
 * This number read as parts per thousand, so `25.toBigDecimal().perMille` is `2.5%`.
 *
 * The literal spelling of [Percentage.fromPerMille].
 */
public val BigDecimal.perMille: Percentage
    get() = Percentage.fromPerMille(this)

/**
 * This number read as parts per ten thousand, so `250.permyriad` is `2.5%` — 250 basis points.
 *
 * The literal spelling of [Percentage.fromPermyriad].
 */
public val Int.permyriad: Percentage
    get() = Percentage.fromPermyriad(this)

/**
 * This number read as parts per ten thousand, so `250L.permyriad` is `2.5%` — 250 basis points.
 *
 * The literal spelling of [Percentage.fromPermyriad].
 */
public val Long.permyriad: Percentage
    get() = Percentage.fromPermyriad(this)

/**
 * This number read as parts per ten thousand, so `250.0.permyriad` is `2.5%` — 250 basis points.
 *
 * The literal spelling of [Percentage.fromPermyriad].
 *
 * @throws IllegalArgumentException if this is `NaN` or infinite.
 */
public val Double.permyriad: Percentage
    get() = Percentage.fromPermyriad(this)

/**
 * This number read as parts per ten thousand, so `250.toBigDecimal().permyriad` is `2.5%`.
 *
 * The literal spelling of [Percentage.fromPermyriad].
 */
public val BigDecimal.permyriad: Percentage
    get() = Percentage.fromPermyriad(this)

/**
 * This number read as parts per hundred thousand, so `2500.perCentMille` is `2.5%`.
 *
 * The literal spelling of [Percentage.fromPerCentMille].
 */
public val Int.perCentMille: Percentage
    get() = Percentage.fromPerCentMille(this)

/**
 * This number read as parts per hundred thousand, so `2500L.perCentMille` is `2.5%`.
 *
 * The literal spelling of [Percentage.fromPerCentMille].
 */
public val Long.perCentMille: Percentage
    get() = Percentage.fromPerCentMille(this)

/**
 * This number read as parts per hundred thousand, so `2500.0.perCentMille` is `2.5%`.
 *
 * The literal spelling of [Percentage.fromPerCentMille].
 *
 * @throws IllegalArgumentException if this is `NaN` or infinite.
 */
public val Double.perCentMille: Percentage
    get() = Percentage.fromPerCentMille(this)

/**
 * This number read as parts per hundred thousand, so `2500.toBigDecimal().perCentMille` is `2.5%`.
 *
 * The literal spelling of [Percentage.fromPerCentMille].
 */
public val BigDecimal.perCentMille: Percentage
    get() = Percentage.fromPerCentMille(this)
