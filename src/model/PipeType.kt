package de.fhac.ewi.model

data class PipeType(
    val diameter: Double, // in m
    val costPerMeter: Double, // in €
    val isolationThickness: Double, // in m
    val distanceBetweenPipes: Double // in m
){
    companion object {
        val UNDEFINED = PipeType(0.0001, 999_999.0, 1.0, 0.5)
    }
}


