package de.fhac.ewi.dto

data class OptimizedPipeResponse(
    val pipeId: String,
    val diameter: Double,
    val massenstrom: DoubleArray,
    val volumeFlow: DoubleArray,
    val pipeHeatLoss: DoubleArray,
    val pipePressureLoss: DoubleArray,
    val totalPressureLoss: DoubleArray,
    val totalPumpPower: DoubleArray
)