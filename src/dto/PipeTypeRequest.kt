package de.fhac.ewi.dto

data class PipeTypeRequest(
    val diameter: Int, // in mm
    val costPerMeter: Double // in €
)