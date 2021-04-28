package de.fhac.ewi.services

import de.fhac.ewi.model.HeatDemandCurve
import de.fhac.ewi.model.LoadProfile

class LoadProfileService(val profiles: List<LoadProfile>) {

    fun getAllProfileNames() = profiles.map { it.profileName }

    fun getProfile(profileName: String) = profiles.firstOrNull { it.profileName == profileName }
        ?: throw IllegalArgumentException("No standard load profile found for name $profileName.")

    fun distribute(profileName: String, kwh: Double): HeatDemandCurve {
        val profile = getProfile(profileName)
        return profile.curve.copy(kwh)
    }
}