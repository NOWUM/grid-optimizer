package de.fhac.ewi.dto

data class OptimizedNodeResponse(
    val nodeId: String,
    val thermalEnergyDemand: List<Double>
)