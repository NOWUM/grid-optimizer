package de.fhac.ewi.model.heatprofile

import de.fhac.ewi.model.HeatDemandCurve
import de.fhac.ewi.model.TemperatureTimeSeries
import de.fhac.ewi.util.ifNaN
import de.fhac.ewi.util.requireNoNaNs
import de.fhac.ewi.util.toAllocationTemperature
import kotlin.math.max
import kotlin.math.pow

class LoadProfile(temperatureTimeSeries: TemperatureTimeSeries, val hProfile: HProfile) {


    val allokation = temperatureTimeSeries.temperatures.toAllocationTemperature()

    private val dailyHeatCurve =
        allokation.map { ta ->
            val sigmoid = hProfile.a / (1 + (hProfile.b / (ta - hProfile.zero)).pow(hProfile.c).ifNaN{ 0.0 } ) + hProfile.d
            val linH = hProfile.mH + hProfile.bH + ta
            val linW = hProfile.mW + hProfile.bW + ta
            return@map max(sigmoid + max(linH, linW), 0.0)
        }.requireNoNaNs()

    private val heatCurveSum = dailyHeatCurve.sum()

    init {
        require(heatCurveSum > 0.0) { "The sum of heat over the curve must be positive." }
    }

    fun createHeatDemandCurve(thermalEnergyDemand: Double): HeatDemandCurve {
        val multiplier = thermalEnergyDemand / heatCurveSum
        val curve = allokation.zip(dailyHeatCurve).flatMap { (ta, h) ->
            hProfile.hourDistribution.splitDay(ta, h * multiplier)
        }
        return HeatDemandCurve(curve)
    }
}
