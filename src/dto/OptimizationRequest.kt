package de.fhac.ewi.dto

data class OptimizationRequest(
    val grid: GridRequest,

    // other Parameters
    val insulationThickness: Double,

    // cost functions
    val gridInvestCostTemplate: String, // f(Durchmesser) = y [€/m]
    val gridOperatingCostTemplate: String, // f(gridInvestCost) = y [€/year]
    val pumpInvestCostTemplate: String, // f(Leistung) = y [€/kW]
    val heatGenerationCost: Double, // €/kWh [for calculating heat loss]
    val lifespanOfResources: Double, // Jahre
    val wacc: Double, // Weighted Average Cost of Capital in %
    val electricityCost: Double, // ct/kWh [for pump station]
    val electricalEfficiency: Double, // for pump
    val hydraulicEfficiency: Double, // for pump
)
