package de.fhac.ewi.model.heatprofile

import de.fhac.ewi.model.HeatDemandCurve
import de.fhac.ewi.model.TemperatureTimeSeries
import de.fhac.ewi.util.toAllocationTemperature
import kotlin.math.max
import kotlin.math.pow

class LoadProfile(temperatureTimeSeries: TemperatureTimeSeries, val hProfile: HProfile) {

    val allokation = temperatureTimeSeries.temperatures.toAllocationTemperature()

    private val dailyHeatCurve =
        allokation.map { ta ->
            val sigmoid = hProfile.a / (1 + (hProfile.b / (ta - hProfile.zero)).pow(hProfile.c)) + hProfile.d
            val linH = hProfile.mH + hProfile.bH + ta
            val linW = hProfile.mW + hProfile.bW + ta
            return@map sigmoid + max(linH, linW)
        }

    private val heatCurveSum = dailyHeatCurve.sum()

    fun createHeatDemandCurve(thermalEnergyDemand: Double): HeatDemandCurve {
        val multiplier = thermalEnergyDemand / heatCurveSum
        val curve = allokation.zip(dailyHeatCurve).flatMap { (ta, h) ->
            hProfile.hourDistribution.splitDay(ta, h * multiplier)
        }
        return HeatDemandCurve(curve)
    }
}
