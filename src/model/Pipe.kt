package de.fhac.ewi.model

import de.fhac.ewi.util.WATER_DICHTE
import de.fhac.ewi.util.massenstrom
import de.fhac.ewi.util.pipePressureLoss
import kotlin.math.pow

data class Pipe(
    val id: String,
    val source: Node,
    val target: Node,
    val length: Double
) {

    var diameter: Double = 0.0

    // TODO Keine Fixen werte für Vorlauf und Rücklauf verwenden. Thats wrong!
    // Volumenstrom = Massenstrom / Dichte von Wasser
    // https://www.ingenieur.de/fachmedien/bwk/energieversorgung/dimensionierung-von-fernwaermenetzen/
    val volumeFlow: List<Double>
        get() = target.connectedThermalEnergyDemand.curve.map { massenstrom(80.0, 60.0, it) / WATER_DICHTE }

    // Strömungsgeschwindigkeit = Volumenstrom / Rohrquerschnittsfläche
    val flowRate: List<Double>
        get() {
            val a = (diameter / 2).pow(2) * Math.PI
            return volumeFlow.map { it / a }
        }

    val pipePressureLoss: List<Double>
        get() = flowRate.map { pipePressureLoss(it, length, diameter) }

    init {
        if (id.isBlank())
            throw IllegalArgumentException("Id of pipe must be filled.")

        if (source == target)
            throw IllegalArgumentException("Source and target node of pipe can not be the same.")

        if (length <= 0.0)
            throw IllegalArgumentException("Length of pipe can not be negative or zero.")
    }
}
