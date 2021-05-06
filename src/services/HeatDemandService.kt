package de.fhac.ewi.services

import de.fhac.ewi.model.HeatDemandCurve
import de.fhac.ewi.model.heatprofile.HProfile

class HeatDemandService(
    private val temperatureService: TemperatureTimeSeriesService,
    private val loadProfileService: LoadProfileService
) {

    fun createCurve(thermalEnergyDemand: Double, profileName: String, tempSeriesName: String): HeatDemandCurve {
        val tempSeries = temperatureService.getSeries(tempSeriesName)
        val profile = loadProfileService.getLoadProfile(profileName, tempSeries)
        return profile.createHeatDemandCurve(thermalEnergyDemand)
    }
}