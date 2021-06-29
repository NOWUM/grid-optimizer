package de.fhac.ewi.model

data class PipeType(
    val diameter: Double, // in m
    val costPerMeter: Double, // in €
    val isolationThickness: Double, // in m
    val distanceBetweenPipes: Double // in m
) {
    companion object {
        val UNDEFINED = PipeType(1337.0, 9_999_999.0, Double.POSITIVE_INFINITY, 0.5)
    }
}


