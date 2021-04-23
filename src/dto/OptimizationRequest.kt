package de.fhac.ewi.dto

data class OptimizationRequest(
    val grid: GridRequest,

    // other Parameters
    val insulationThickness: Double,
    val pressureDropPerOutput: Double
)
