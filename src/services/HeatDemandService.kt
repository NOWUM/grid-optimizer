package de.fhac.ewi.services

import de.fhac.ewi.model.HeatDemandCurve
import de.fhac.ewi.model.heatprofile.LoadProfile

class HeatDemandService(
    private val temperatureService: TemperatureTimeSeriesService,
    private val loadProfileService: LoadProfileService
) {

    fun createCurve(thermalEnergyDemand: Double, profileName: String, tempSeriesName: String): HeatDemandCurve {
        return retrieveProfile(profileName, tempSeriesName).createHeatDemandCurve(thermalEnergyDemand)
    }

    fun retrieveProfile(profileName: String, tempSeriesName: String): LoadProfile {
        val tempSeries = temperatureService.getSeries(tempSeriesName)
        return loadProfileService.getLoadProfile(profileName, tempSeries)
    }
}