package de.fhac.ewi.util

import kotlin.math.ln
import kotlin.math.pow

const val WATER_DICHTE = 997.0

/**
 * Berechnet den Massenstrom aus Temperaturdifferenz, Wärmebedarf und der spezifischen Wärmekapazität des Mediums.
 *
 * @param flowIn Double - Vorlauftemperatur in °C
 * @param flowOut Double - Rücklauftemperatur in °C
 * @param heatDemand Double - Benötigte Wärmeenergie in Wh
 * @param c Double - spezifische Wärmekapazität (default Wert für Wasser) in kJ/kgK
 * @return Double - Massenstrom in kg/s
 */
fun massenstrom(flowIn: Double, flowOut: Double, heatDemand: Double, c: Double = 4.187) =
    (heatDemand) / (c * (flowIn - flowOut))

/**
 * Berechnet den Volumenstrom aus Temperaturdifferenz, Wärmebedarf und der Dichte des Mediums.
 *
 * Quelle: https://www.ingenieur.de/fachmedien/bwk/energieversorgung/dimensionierung-von-fernwaermenetzen/
 *
 * @param flowIn Double - Vorlauftemperatur in °C
 * @param flowOut Double - Rücklauftemperatur in °C
 * @param heatDemand Double - Benötigte Wärmeenergie in Wh
 * @param p Double - Dichte des Mediums in kg/m^3
 * @return Double - Volumenstrom in m^3/s
 */
fun volumeFlow(flowIn: Double, flowOut: Double, heatDemand: Double, p: Double = WATER_DICHTE): Double =
    massenstrom(flowIn, flowOut, heatDemand) / p

/**
 * Berechnet die Strömungsgeschwindigkeit auf Basis des Rohrdurchmessers und dem Volumenstrom.
 *
 * @param diameter Double - Rohrdurchmesser in m
 * @param volumeFlow Double - Volumenstrom in m^3/s
 * @return Double - Strömungsgeschwindigkeit in m/s
 */
fun flowRate(diameter: Double, volumeFlow: Double): Double {
    return volumeFlow / ((diameter / 2).pow(2) * Math.PI)
}

/**
 * Druckverlustberechnung in Rohrleitungen.
 *
 * Quelle: Jungbluth, 09 Uebung Waermeverteilung.pdf, Folie 4
 * Hinweis: Formel ist für Pa ausgelegt - umrechnung in Bar
 *
 * @param flowSpeed Double - Strömungsgeschwindigkeit in m/s
 * @param length Double - Länge der Rohrleitung in m
 * @param diameter Double - Rohrinnendurchmesser in m
 * @param lambda Double - Rohrwiederstandsbeiwert oder Rohrreibungszahl (maybe 0.15 von Wikipedia https://de.wikipedia.org/wiki/Rohrreibungszahl)
 * @param p Double - Dichte des Mediums in kg/m^3
 * @return Double - Druckverlust in Bar
 */
fun pipePressureLoss(
    flowSpeed: Double,
    length: Double,
    diameter: Double,
    lambda: Double = 0.15,
    p: Double = WATER_DICHTE
) =
    lambda * length / diameter * p / 2 * flowSpeed.pow(2) / 100_000

/**
 * Berechnet die benötigte Pumpleistung.
 *
 * @param pressureLoss Double - Druckverlust in Bar
 * @param volumeFlow Double - Volumenstrom in m^3/s
 * @return Double - benötigte Pumpleistung in Watt
 */
fun neededPumpPower(pressureLoss: Double, volumeFlow: Double): Double =
    pressureLoss * 100_000 * volumeFlow

/**
 * Berechnet den Wärmeverlust in einem Rohr.
 *
 * @param flowIn Double - Vorlauftemperatur in °C
 * @param flowOut Double - Rücklauftemperatur in °C
 * @param ground Double - Bodentemperatur in °C
 * @param diameter Double - Durchmesser Rohrleitung in m
 * @param isolationThickness Double - Dicke Isolierung in m
 * @param coverageHeight Double - Mittlere Überdeckungshöhe der vergrabenen Rohrleitungen in m
 * @param distance Double - Abstand zwischen Vorlauf und Rücklauf Leitung in m
 * @param length Double - Länge der Leitung in m
 * @param lambdaPipe Double - Wärmeleitfähigkeit Dämmmaterial in W / (m*K)
 * @param lambdaGround Double - Wärmeleitfähigkeit Boden in W / (m*K)
 * @return Double Wärmeverluststrom in W
 */
fun pipeHeatLoss(
    flowIn: Double,
    flowOut: Double,
    ground: Double,
    diameter: Double,
    isolationThickness: Double,
    coverageHeight: Double,
    distance: Double,
    length: Double,
    lambdaPipe: Double = 0.03,
    lambdaGround: Double = 1.2
): Double {
    val innerRadius = diameter / 2
    val outerRadius = innerRadius + isolationThickness
    val numerator = 4 * Math.PI * length * ((flowIn + flowOut) / 2 - ground)
    val denominator = 1 / lambdaPipe * ln(outerRadius / innerRadius)
    + 1 / lambdaGround * ln( 4 * (outerRadius + coverageHeight) / outerRadius)
    + 1 / lambdaGround * ln( ((2 * (outerRadius + coverageHeight) / (distance + 2 * outerRadius)).pow(2) + 1).pow(0.5))
    return numerator / denominator
}


/**
 * Berechnet T_Allokation für Tagesmittelwerte
 *
 * @receiver List<Double>
 * @return List<Double>
 */
fun List<Double>.toAllocationTemperature() =
    indices.map { d -> (t(d) + 0.5 * t(d - 1) + 0.25 * t(d - 2) + 0.125 * t(d - 3)) / (1 + 0.5 + 0.25 + 0.125) }

private fun List<Double>.t(d: Int) = get((d+size) % size)