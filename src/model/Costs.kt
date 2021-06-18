package de.fhac.ewi.model

data class Costs(
    val pipeInvestCost: Double, // Investitionskosten Netz
    val pipeOperationCost: Double, // Betriebskosten Netz per year
    val pumpInvestCost: Double, // Investitionskosten Pumpe
    val pumpOperationCost: Double, // Betriebskosten Pumpe per year
    val heatLossCost: Double, // Wärmeverlust Rohre per year
    val total: Double // Gesamtkosten
)