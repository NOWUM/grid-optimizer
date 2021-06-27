package de.fhac.ewi.dto

data class OptimizationStatusResponse(
    val id: String,
    val completed: Boolean,
    val numberOfChecks: Int,
    val numberOfUpdates: Int
)
