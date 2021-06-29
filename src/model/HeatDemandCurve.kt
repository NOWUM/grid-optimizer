package de.fhac.ewi.model

import de.fhac.ewi.util.mapIndicesParallel
import de.fhac.ewi.util.requireNoNaNs

/**
 * Beinhaltet für jede Stunde im Jahr den Energiebedarf.
 *
 * @property curve List<Double> - Wh per hour in year
 * @property total Double
 * @constructor
 */
class HeatDemandCurve(val curve: List<Double>) {

    val total = curve.sum()

    operator fun get(index: Int) = curve[index]

    operator fun plus(other: HeatDemandCurve) =
        HeatDemandCurve(curve.mapIndicesParallel { curve[it] + other.curve[it] })

    operator fun plus(other: List<Double>) = HeatDemandCurve(curve.mapIndicesParallel { curve[it] + other[it] })

    companion object {
        val ZERO = HeatDemandCurve(List(8760) { 0.0 })
    }

    init {
        if (curve.size != 8760) throw IllegalArgumentException("The curve must contain exactly 8760 elements. For each hour of year one.")
        if (curve.any { it < 0.0 }) throw IllegalArgumentException("The curve must contain only positive values.")
        curve.requireNoNaNs()
    }
}