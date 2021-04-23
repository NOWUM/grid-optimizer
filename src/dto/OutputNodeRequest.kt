package de.fhac.ewi.dto

import de.fhac.ewi.model.LoadProfile

data class OutputNodeRequest(
    val id: String,
    val hotWater: Double,
    val area: Double,
    val loadProfile: LoadProfile
)
