package io.github.silicontaiga.percentage

import java.math.BigDecimal
import java.math.MathContext

/**
 * A quantity expressed per hundred: `50%`.
 *
 * The value carried is the [ratio] — fifty percent is `0.5`. Every other scale is a named factory
 * going in and an accessor of the same name coming out, so no number crosses this boundary without
 * saying which scale it is on.
 *
 * Instances are immutable and thread-safe.
 */
public class Percentage private constructor(
    ratio: BigDecimal,
) : Comparable<Percentage> {
    /**
     * This quantity as a plain multiplier: `0.5` for `50%`.
     *
     * The stored scale, so reading it converts nothing.
     */
    public val ratio: BigDecimal = ratio.normalized()

    /** This quantity as the number in front of the sign: `50` for `50%`. */
    public val percentValue: BigDecimal
        get() = ratio.movePointRight(PERCENT_SHIFT).normalized()

    /** This quantity in parts per thousand: `500` for `50%`. */
    public val perMille: BigDecimal
        get() = ratio.movePointRight(PER_MILLE_SHIFT).normalized()

    /** This quantity in parts per ten thousand, the unit finance calls a basis point: `5000` for `50%`. */
    public val permyriad: BigDecimal
        get() = ratio.movePointRight(PERMYRIAD_SHIFT).normalized()

    /** This quantity in parts per hundred thousand: `50000` for `50%`. */
    public val perCentMille: BigDecimal
        get() = ratio.movePointRight(PER_CENT_MILLE_SHIFT).normalized()

    /** Returns the sum of this percentage and [other], so `30.percent + 20.percent` is `50%`. Exact. */
    public operator fun plus(other: Percentage): Percentage = Percentage(ratio + other.ratio)

    /** Returns the difference of this percentage and [other], so `30.percent - 20.percent` is `10%`. Exact. */
    public operator fun minus(other: Percentage): Percentage = Percentage(ratio - other.ratio)

    /** Returns this percentage with its sign flipped, so `-(20.percent)` is `-20%`. Exact. */
    public operator fun unaryMinus(): Percentage = Percentage(ratio.negate())

    /** Returns this percentage scaled by [factor], so `20.percent * 2` is `40%`. Exact. */
    public operator fun times(factor: Int): Percentage = times(factor.toBigDecimal())

    /** Returns this percentage scaled by [factor], so `20.percent * 2L` is `40%`. Exact. */
    public operator fun times(factor: Long): Percentage = times(factor.toBigDecimal())

    /**
     * Returns this percentage scaled by [factor], so `20.percent * 1.5` is `30%`. Exact.
     *
     * @throws IllegalArgumentException if [factor] is `NaN` or infinite.
     */
    public operator fun times(factor: Double): Percentage = times(factor.toExactBigDecimal())

    /** Returns this percentage scaled by [factor], so `20.percent * 2.toBigDecimal()` is `40%`. Exact. */
    public operator fun times(factor: BigDecimal): Percentage = Percentage(ratio * factor)

    /**
     * Returns the product of this percentage and [other], so `50.percent * 50.percent` is `25%`. Exact.
     *
     * Mathematics defines this on the ratio scale — `0.5 x 0.5 = 0.25` — which is the stored scale, so
     * this is a single multiply. A ratio times a ratio is again a ratio, which is why the result is a
     * [Percentage] rather than a plain number.
     */
    public operator fun times(other: Percentage): Percentage = Percentage(ratio * other.ratio)

    /** Returns this percentage divided by [divisor], so `20.percent / 2` is `10%`. Rounds per the default context. */
    public operator fun div(divisor: Int): Percentage = div(divisor.toBigDecimal())

    /** Returns this percentage divided by [divisor], rounding to [context]. */
    public fun div(
        divisor: Int,
        context: MathContext,
    ): Percentage = div(divisor.toBigDecimal(), context)

    /** Returns this percentage divided by [divisor], so `20.percent / 2L` is `10%`. Rounds per the default context. */
    public operator fun div(divisor: Long): Percentage = div(divisor.toBigDecimal())

    /** Returns this percentage divided by [divisor], rounding to [context]. */
    public fun div(
        divisor: Long,
        context: MathContext,
    ): Percentage = div(divisor.toBigDecimal(), context)

    /**
     * Returns this percentage divided by [divisor], so `20.percent / 0.5` is `40%`. Rounds per the default context.
     *
     * @throws IllegalArgumentException if [divisor] is `NaN` or infinite.
     */
    public operator fun div(divisor: Double): Percentage = div(divisor.toExactBigDecimal())

    /**
     * Returns this percentage divided by [divisor], rounding to [context].
     *
     * @throws IllegalArgumentException if [divisor] is `NaN` or infinite.
     */
    public fun div(
        divisor: Double,
        context: MathContext,
    ): Percentage = div(divisor.toExactBigDecimal(), context)

    /**
     * Returns this percentage divided by [divisor], so `20.percent / 2.toBigDecimal()` is `10%`.
     *
     * A division can meet a non-terminating decimal expansion, so this one rounds to 34 significant
     * digits, half-even — `MathContext.DECIMAL128`.
     *
     * @throws ArithmeticException if [divisor] is zero.
     */
    public operator fun div(divisor: BigDecimal): Percentage = div(divisor, DEFAULT_CONTEXT)

    /**
     * Returns this percentage divided by [divisor], rounding to [context].
     *
     * @throws ArithmeticException if [divisor] is zero, or if [context] cannot represent the result.
     */
    public fun div(
        divisor: BigDecimal,
        context: MathContext,
    ): Percentage = Percentage(ratio.divide(divisor, context))

    /**
     * Returns how many times [other] fits into this percentage, so `50.percent / 25.percent` is `2`.
     *
     * Mathematics defines this on the ratio scale — the stored scale, so this is a single divide — and
     * a ratio divided by a ratio is dimensionless, which is why the result is a plain [BigDecimal]
     * rather than a [Percentage]. Rounds to 34 significant digits, half-even — `MathContext.DECIMAL128`.
     *
     * @throws ArithmeticException if [other] is zero.
     */
    public operator fun div(other: Percentage): BigDecimal = div(other, DEFAULT_CONTEXT)

    /**
     * Returns how many times [other] fits into this percentage, rounding to [context].
     *
     * @throws ArithmeticException if [other] is zero, or if [context] cannot represent the result.
     */
    public fun div(
        other: Percentage,
        context: MathContext,
    ): BigDecimal = ratio.divide(other.ratio, context).normalized()

    /**
     * Returns this share of [base], so `20.percent.of(250.toBigDecimal())` is an amount of `50`.
     *
     * Exact: the base is multiplied by the stored [ratio], with no rescaling and nothing rounded, so
     * no rounding parameter is offered.
     *
     * [base] is a [BigDecimal] and nothing else, deliberately — the caller converts, in view, so no
     * quantity ever changes representation without the reader seeing it.
     */
    public fun of(base: BigDecimal): BigDecimal = (base * ratio).normalized()

    /**
     * Returns this percentage one percentage point higher, so `10.percent.inc()` is `11%`. Exact.
     *
     * A percentage *point* is the unit of an increase or decrease of a percentage — `0.01` on the
     * stored ratio — and matches Kotlin's own `BigDecimal.inc()`, which adds one.
     */
    public operator fun inc(): Percentage = Percentage(ratio + ONE_PERCENTAGE_POINT)

    /**
     * Returns this percentage one percentage point lower, so `10.percent.dec()` is `9%`. Exact.
     *
     * The mirror of [inc]; the step is a percentage point, not one percent of the value.
     */
    public operator fun dec(): Percentage = Percentage(ratio - ONE_PERCENTAGE_POINT)

    /**
     * Compares this percentage with [other] by mathematical value.
     *
     * Consistent with [equals]: this returns zero exactly when the two are equal.
     */
    override fun compareTo(other: Percentage): Int = ratio.compareTo(other.ratio)

    /**
     * Returns `true` when [other] is a [Percentage] of the same mathematical value.
     *
     * Because the stored ratio is normalized, percentages built differently — from `50`, from `50.00`,
     * from the ratio `0.5`, or from `5000` permyriad — are indistinguishable here.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Percentage) return false
        return ratio == other.ratio
    }

    /** Returns a hash code consistent with [equals]. */
    override fun hashCode(): Int = ratio.hashCode()

    /**
     * Returns the percent value followed by `%`, for example `"50%"` — never the ratio `0.5`.
     *
     * Debug output: the rendering is locale-independent and never abbreviates, so it is reproducible
     * on any machine. Use `java.text` for anything a user reads.
     */
    override fun toString(): String = "${percentValue.toPlainString()}%"

    /** Factories for building a [Percentage] from a number on any of the supported scales. */
    public companion object {
        /** Decimal places between the stored ratio and the percent value. */
        private const val PERCENT_SHIFT = 2

        /** Decimal places between the stored ratio and parts per thousand. */
        private const val PER_MILLE_SHIFT = 3

        /** Decimal places between the stored ratio and parts per ten thousand. */
        private const val PERMYRIAD_SHIFT = 4

        /** Decimal places between the stored ratio and parts per hundred thousand. */
        private const val PER_CENT_MILLE_SHIFT = 5

        /** One percentage point on the stored ratio scale: the step taken by [inc] and [dec]. */
        private val ONE_PERCENTAGE_POINT: BigDecimal = BigDecimal.ONE.movePointLeft(PERCENT_SHIFT)

        /**
         * The rounding applied wherever a genuine division could meet a non-terminating expansion.
         *
         * 34 significant digits, half-even — the IEEE 754 decimal128 standard. Every operation that
         * uses it also offers an overload taking an explicit `MathContext`, and every operation that
         * does not name it is exact.
         */
        internal val DEFAULT_CONTEXT: MathContext = MathContext.DECIMAL128

        /** Returns the [Percentage] whose percent value is [percentValue], so `of(50)` is `50%`. */
        public fun of(percentValue: Int): Percentage = of(percentValue.toBigDecimal())

        /** Returns the [Percentage] whose percent value is [percentValue], so `of(50L)` is `50%`. */
        public fun of(percentValue: Long): Percentage = of(percentValue.toBigDecimal())

        /**
         * Returns the [Percentage] whose percent value is [percentValue], so `of(12.5)` is `12.5%`.
         *
         * [percentValue] is read as the decimal it was written as, not as its binary expansion, so
         * `of(0.1)` is exactly `0.1%`.
         *
         * @throws IllegalArgumentException if [percentValue] is `NaN` or infinite.
         */
        public fun of(percentValue: Double): Percentage = of(percentValue.toExactBigDecimal())

        /** Returns the [Percentage] whose percent value is [percentValue]. Exact: a decimal-point shift. */
        public fun of(percentValue: BigDecimal): Percentage = Percentage(percentValue.movePointLeft(PERCENT_SHIFT))

        /** Returns the [Percentage] whose ratio is [ratio], so `fromRatio(1)` is `100%`. */
        public fun fromRatio(ratio: Int): Percentage = fromRatio(ratio.toBigDecimal())

        /** Returns the [Percentage] whose ratio is [ratio], so `fromRatio(2L)` is `200%`. */
        public fun fromRatio(ratio: Long): Percentage = fromRatio(ratio.toBigDecimal())

        /**
         * Returns the [Percentage] whose ratio is [ratio], so `fromRatio(0.5)` is `50%`.
         *
         * [ratio] is read as the decimal it was written as, not as its binary expansion.
         *
         * @throws IllegalArgumentException if [ratio] is `NaN` or infinite.
         */
        public fun fromRatio(ratio: Double): Percentage = fromRatio(ratio.toExactBigDecimal())

        /** Returns the [Percentage] whose ratio is [ratio]. This is the stored scale, so nothing is converted. */
        public fun fromRatio(ratio: BigDecimal): Percentage = Percentage(ratio)

        /** Returns the [Percentage] of [perMille] parts per thousand, so `fromPerMille(25)` is `2.5%`. */
        public fun fromPerMille(perMille: Int): Percentage = fromPerMille(perMille.toBigDecimal())

        /** Returns the [Percentage] of [perMille] parts per thousand, so `fromPerMille(25L)` is `2.5%`. */
        public fun fromPerMille(perMille: Long): Percentage = fromPerMille(perMille.toBigDecimal())

        /**
         * Returns the [Percentage] of [perMille] parts per thousand, so `fromPerMille(25.0)` is `2.5%`.
         *
         * @throws IllegalArgumentException if [perMille] is `NaN` or infinite.
         */
        public fun fromPerMille(perMille: Double): Percentage = fromPerMille(perMille.toExactBigDecimal())

        /** Returns the [Percentage] of [perMille] parts per thousand. Exact: a decimal-point shift. */
        public fun fromPerMille(perMille: BigDecimal): Percentage = Percentage(perMille.movePointLeft(PER_MILLE_SHIFT))

        /**
         * Returns the [Percentage] of [permyriad] parts per ten thousand, so `fromPermyriad(250)` is
         * `2.5%` — the same unit finance calls 250 basis points.
         */
        public fun fromPermyriad(permyriad: Int): Percentage = fromPermyriad(permyriad.toBigDecimal())

        /** Returns the [Percentage] of [permyriad] parts per ten thousand, equivalently basis points. */
        public fun fromPermyriad(permyriad: Long): Percentage = fromPermyriad(permyriad.toBigDecimal())

        /**
         * Returns the [Percentage] of [permyriad] parts per ten thousand, equivalently basis points.
         *
         * @throws IllegalArgumentException if [permyriad] is `NaN` or infinite.
         */
        public fun fromPermyriad(permyriad: Double): Percentage = fromPermyriad(permyriad.toExactBigDecimal())

        /** Returns the [Percentage] of [permyriad] parts per ten thousand. Exact: a decimal-point shift. */
        public fun fromPermyriad(permyriad: BigDecimal): Percentage = Percentage(permyriad.movePointLeft(PERMYRIAD_SHIFT))

        /**
         * Returns the [Percentage] of [perCentMille] parts per hundred thousand, so
         * `fromPerCentMille(2500)` is `2.5%`.
         */
        public fun fromPerCentMille(perCentMille: Int): Percentage = fromPerCentMille(perCentMille.toBigDecimal())

        /** Returns the [Percentage] of [perCentMille] parts per hundred thousand. */
        public fun fromPerCentMille(perCentMille: Long): Percentage = fromPerCentMille(perCentMille.toBigDecimal())

        /**
         * Returns the [Percentage] of [perCentMille] parts per hundred thousand.
         *
         * @throws IllegalArgumentException if [perCentMille] is `NaN` or infinite.
         */
        public fun fromPerCentMille(perCentMille: Double): Percentage = fromPerCentMille(perCentMille.toExactBigDecimal())

        /** Returns the [Percentage] of [perCentMille] parts per hundred thousand. Exact: a decimal-point shift. */
        public fun fromPerCentMille(perCentMille: BigDecimal): Percentage = Percentage(perCentMille.movePointLeft(PER_CENT_MILLE_SHIFT))

        /**
         * Returns the change from [from] to [to], so `changeBetween(from = 80, to = 100)` is `25%` and
         * `changeBetween(from = 100, to = 80)` is `-20%`.
         *
         * The meaning depends on the argument order, so call this with the named arguments `from` and
         * `to` and the direction stays visible at the call site.
         *
         * A genuine division, so it rounds to 34 significant digits, half-even — `MathContext.DECIMAL128`.
         *
         * @throws ArithmeticException if [from] is zero; change from nothing is mathematically
         * undefined, and exact decimal arithmetic has no `NaN` to return instead.
         */
        public fun changeBetween(
            from: BigDecimal,
            to: BigDecimal,
        ): Percentage = changeBetween(from, to, DEFAULT_CONTEXT)

        /**
         * Returns the change from [from] to [to], rounding to [context].
         *
         * @throws ArithmeticException if [from] is zero, or if [context] cannot represent the result.
         */
        public fun changeBetween(
            from: BigDecimal,
            to: BigDecimal,
            context: MathContext,
        ): Percentage = fromRatio((to - from).divide(from, context))
    }
}

/**
 * Returns the canonical representation of this value: trailing zeros stripped, and never in
 * scientific notation.
 *
 * `stripTrailingZeros` alone would turn `50.00` into `5E+1` — mathematically right, but it makes
 * the scale negative, which [BigDecimal.toPlainString] then has to undo. Clamping the scale at zero
 * keeps one representative per mathematical value, so `equals`, `hashCode` and `compareTo` agree.
 */
internal fun BigDecimal.normalized(): BigDecimal {
    val stripped = stripTrailingZeros()
    return if (stripped.scale() < 0) stripped.setScale(0) else stripped
}

/**
 * Returns this `Double` as the decimal it was written as — `0.1` rather than
 * `0.1000000000000000055511151231257827…`.
 *
 * This is `BigDecimal.valueOf` semantics, deliberately not `BigDecimal(Double)`: a literal in source
 * is a decimal the programmer chose, and reading it any other way would smuggle a binary-floating-point
 * artifact into a library whose premise is exactness.
 *
 * @throws IllegalArgumentException if this is `NaN` or infinite; there is no exact decimal for either.
 */
internal fun Double.toExactBigDecimal(): BigDecimal {
    require(isFinite()) { "A Percentage must be a finite number, but was $this" }
    return BigDecimal.valueOf(this)
}
