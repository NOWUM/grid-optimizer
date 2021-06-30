package de.fhac.ewi.util

import kotlin.math.pow

fun Double.round(n: Int): Double {
    val factor = (10.0).pow(n)
    return kotlin.math.round(this * factor) / factor
}

fun List<Double>.requireNoNaNs(): List<Double> {
    require(none { it.isNaN() }) { "There is at least one NaN element in list. Index ${indexOfFirst { it.isNaN() }}" }
    return this
}

fun Double.ifNaN(block: () -> Double) = if (isNaN()) block() else this

fun Double.toEURString() = this.round(2).toString().padStart(10) + " €"

fun Double.toMW() = (this / 1_000_000).round(3)

fun Double.toKW() = (this / 1_000).round(3)