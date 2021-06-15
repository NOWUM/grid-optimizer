package de.fhac.ewi.model

data class Costs(
    val pipeInvestCostTotal: Double, // Investitionskosten Netz total
    val pipeInvestCostAnnuity: Double, // Investitionskosten Netz per year
    val pipeOperationCost: Double, // Betriebskosten Netz per year
    val pumpInvestCostTotal: Double, // Investitionskosten Pumpe total
    val pumpInvestCostAnnuity: Double, // Investitionskosten Pumpe per year
    val pumpOperationCost: Double, // Betriebskosten Pumpe per year
    val totalPerYear: Double // Gesamtkosten per year
)