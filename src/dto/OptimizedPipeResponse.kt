package de.fhac.ewi.dto

data class OptimizedPipeResponse(
    val pipeId: String,
    val diameter: Double,
    val pipePressureLoss: List<Double>
)