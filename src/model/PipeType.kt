package de.fhac.ewi.model

data class PipeType(
    val diameter: Double, // in m
    val costPerMeter: Double // in €
){
    companion object {
        val UNDEFINED = PipeType(0.02, 999_999.0)
    }
}


