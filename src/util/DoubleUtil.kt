package de.fhac.ewi.util

import kotlin.math.pow

fun Double.round(n: Int): Double {
    val factor = (10.0).pow(n)
    return kotlin.math.round(this * factor) / factor
}
