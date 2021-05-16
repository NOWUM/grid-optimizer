package de.fhac.ewi.services

import de.fhac.ewi.model.TemperatureTimeSeries
import de.fhac.ewi.model.heatprofile.HProfile
import de.fhac.ewi.model.heatprofile.LoadProfile

class LoadProfileService(private val hProfiles: List<HProfile>) {

    fun getAllHProfileNames() = hProfiles.map { it.profileName }

    fun getHProfile(profileName: String) = hProfiles.firstOrNull { it.profileName == profileName }
        ?: throw IllegalArgumentException("No hProfile found for name $profileName.")

    fun getLoadProfile(profileName: String, temperatureSeries: TemperatureTimeSeries) =
        LoadProfile(temperatureSeries, getHProfile(profileName))
}