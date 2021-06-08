package de.fhac.ewi.dto

data class HeatDemandRequest(
    val temperatureSeries: String,
    val loadProfileName: String,
    val thermalEnergyDemand: Double // kwh per year
)
