package de.fhac.ewi.model.heatprofile

import de.fhac.ewi.util.round
import de.fhac.ewi.util.singleValue

data class HourDistribution(val distribution: Map<ClosedFloatingPointRange<Double>, List<Double>>) {
    fun get(hour: Int, temperature: Double) =
        distribution.filterKeys { it.contains(temperature.round(2)) }
            .ifEmpty { throw IllegalArgumentException("Temperature $temperature degree not found in distribution ${distribution.keys}.") }
            .singleValue()[(hour - 6 + 24) % 24]

    fun splitDay(temperature: Double, energy: Double) = IntRange(0, 23).map { get(it, temperature) * energy }
}
