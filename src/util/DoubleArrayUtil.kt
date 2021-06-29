package de.fhac.ewi.util

fun DoubleArray.maxOrElse(otherwise: Double = 0.0) = maxOrNull()?:otherwise