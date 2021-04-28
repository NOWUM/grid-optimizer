package de.fhac.ewi.dto

data class OutputNodeRequest(
    val id: String,
    val loadProfileName: String,
    val thermalEnergyDemand: Double, // kwh per year
    val pressureLoss: Double // Bar
)
