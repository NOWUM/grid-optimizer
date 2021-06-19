package de.fhac.ewi.dto

data class OptimizedNodeResponse(
    val nodeId: String,
    val thermalEnergyDemand: DoubleArray,
    val connectedPressureLoss: DoubleArray,
    val neededPumpPower: DoubleArray
)