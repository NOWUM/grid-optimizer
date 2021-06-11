package de.fhac.ewi.util

import kotlin.math.pow

const val WATER_DICHTE = 997.0

/**
 * Berechnet den Massenstrom aus Temperaturdifferenz, Wärmebedarf und der spezifischen Wärmekapazität des Mediums.
 *
 * @param flowIn Double - Vorlauftemperatur in °C
 * @param flowOut Double - Rücklauftemperatur in °C
 * @param heatDemand Double - Benötigte Wärmeenergie in kW
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
 * @param heatDemand Double - Benötigte Wärmeenergie in kW
 * @param p Double - Dichte des Mediums in kg/m^3
 * @return Double - Volumenstrom in ??
 */
fun volumeFlow(flowIn: Double, flowOut: Double, heatDemand: Double, p: Double = WATER_DICHTE): Double =
    massenstrom(flowIn, flowOut, heatDemand) / p

/**
 * Berechnet die Strömungsgeschwindigkeit auf Basis des Rohrdurchmessers und dem Volumenstrom.
 *
 * @param diameter Double - Rohrdurchmesser in ??
 * @param volumeFlow Double - Volumenstrom in ??
 * @return Double - Strömungsgeschwindigkeit in ??
 */
fun flowRate(diameter: Double, volumeFlow: Double): Double {
    return volumeFlow / ((diameter / 2).pow(2) * Math.PI)
}

/**
 * Druckverlustberechnung in Rohrleitungen.
 *
 * Quelle: https://www.schweizer-fn.de/stroemung/druckverlust/druckverlust.php#druckverlustrohr
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
    (lambda * length * p * flowSpeed.pow(2)) / (diameter * 2) / 100_000

/**
 * Berechnet die benötigte Pumpleistung.
 *
 * @param pressureLoss Double - Druckverlust in Bar
 * @param volumeFlow Double - Volumenstrom in ??
 * @return Double - benötigte Pumpleistung in ??
 */
fun neededPumpPower(pressureLoss: Double, volumeFlow: Double): Double =
    pressureLoss * 100_000 * volumeFlow


/**
 * Berechnet T_Allokation für Tagesmittelwerte
 *
 * @receiver List<Double>
 * @return List<Double>
 */
fun List<Double>.toAllocationTemperature() =
    indices.map { d -> (t(d) + 0.5 * t(d - 1) + 0.25 * t(d - 2) + 0.125 * t(d - 3)) / (1 + 0.5 + 0.25 + 0.125) }

private fun List<Double>.t(d: Int) = get((d+size) % size)