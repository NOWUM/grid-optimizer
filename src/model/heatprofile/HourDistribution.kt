package de.fhac.ewi.model.heatprofile

import de.fhac.ewi.util.singleValue

data class HourDistribution(val distribution: Map<ClosedFloatingPointRange<Double>, List<Double>>) {
    fun get(hour: Int, temperature: Double) =
        distribution.filterKeys { it.contains(temperature) }
            .singleValue()[(hour - 6 + 24) % 24]

    fun splitDay(temperature: Double, energy: Double) = IntRange(0, 23).map { get(it, temperature) * energy }
}
