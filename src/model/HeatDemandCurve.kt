package de.fhac.ewi.model

/**
 * Beinhaltet für jede Stunde im Jahr den Energiebedarf.
 *
 * @property curve List<Double>
 * @property total Double
 * @constructor
 */
class HeatDemandCurve(val curve: List<Double>) {

    val total = curve.sum()

    operator fun get(index: Int) = curve[index]

    operator fun plus(other: HeatDemandCurve) = HeatDemandCurve(curve.zip(other.curve).map { it.first + it.second })

    companion object {
        val ZERO = HeatDemandCurve(List(8760) { 0.0 })
    }

    init {
        if (curve.size != 8760) throw IllegalArgumentException("The curve must contain exactly 8760 elements. For each hour of year one.")
        if (curve.any { it < 0.0 }) throw IllegalArgumentException("The curve must contain only positive values.")
    }
}