package de.fhac.ewi.dto

data class InputNodeRequest(
    val id: String,
    val flowTemperatureTemplate: String, // mathematical expression like `x+5` with x as outside temperature
    val returnTemperatureTemplate: String // mathematical expression like `x+5` with x as outside temperature
)
