package de.fhac.ewi.dto

data class OptimizedNodeResponse(
    val nodeId: String,
    val thermalEnergyDemand: DoubleArray,
    val massenstrom: DoubleArray,
    val connectedPressureLoss: DoubleArray,
    val neededPumpPower: DoubleArray,
    val flowInTemperature: List<Double>,
    val flowOutTemperature: List<Double>,

    val annualEnergyDemand: Double,
    val maximalNeededPumpPower: Double,
    val maximalPressureLoss: Double
)