package de.fhac.ewi.dto

data class MassenstromResponse(
    val temperatures: List<Double>,
    val flowInTemperatures: List<Double>,
    val flowOutTemperatures: List<Double>,
    val energyHeatDemand: DoubleArray,
    val massenstrom: List<Double>
)
