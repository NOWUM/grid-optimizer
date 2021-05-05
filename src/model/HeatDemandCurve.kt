package de.fhac.ewi.model

class HeatDemandCurve(val curve: List<Double>) {
    val total = curve.sum()

    init {
        if (curve.size != 365) throw IllegalArgumentException("The curve must contain exactly 365 elements.")
        if (curve.any { it < 0.0 }) throw IllegalArgumentException("The curve must contain only positive values.")
    }

    operator fun get(index: Int) = curve[index]

    operator fun plus(other: HeatDemandCurve) = HeatDemandCurve(curve.zip(other.curve).map { it.first + it.second })

    fun copy(heatDemand: Double) = HeatDemandCurve(curve.map { it * heatDemand / total })

    companion object {
        val ZERO = HeatDemandCurve(List(365) { 0.0 })
        val ONES = HeatDemandCurve(List(365) { 1.0 })
    }
}