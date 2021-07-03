package de.fhac.ewi.dto

data class OptimizationStatusResponse(
    val id: String,
    val completed: Boolean,
    val numberOfChecks: Int,
    val numberOfUpdates: Int,
    val criticalPath: List<String>,
    val longestPath: List<String>,
    val totalEnergyDemand: Double,
    val totalHeatLoss: Double,
    val neededPumpPower: Double,
    val totalPressureLoss: Double,
    val pressureLossCritical: Double,
    val pressureLossLongest: Double,
    val massenstromInput: Double,
)
