package de.fhac.ewi.dto

data class OptimizationStatusResponse(
    val completed: Boolean,
    val numberOfChecks: Int,
    val numberOfUpdates: Int
)
