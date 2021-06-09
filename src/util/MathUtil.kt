package de.fhac.ewi.util

import kotlin.math.pow

const val WATER_DICHTE = 997.0

/**
 * Berechnet den Massenstrom.
 *
 * Formel `_m° = Q°/(c * ∆v) [kg/h]` mit folgenden Parametern:
 * - Q° = Wärmeleistung [W]
 * - c = spezifische Wärmekapazität [kWs/kg * K] (das K steht für Kelvin) (mit Wasser als Transportmedium ist c=4,187)
 * - ∆v = Temperaturdifferenz [K] (Anfangstemperatur - Endtemperatur)
 *
 *
 * @param flowIn Double - Vorlauftemperatur in °C
 * @param flowOut Double - Rücklauftemperatur in °C
 * @param heatDemand Double - Benötigte Wärmeenergie in kW
 * @param c Double - spezifische Wärmekapazität (default Wert für Wasser)
 * @return Double - Massenstrom in kg/s
 */
fun massenstrom(flowIn: Double, flowOut: Double, heatDemand: Double, c: Double = 4.187) =
    (heatDemand * 10_000) / (c * (flowIn - flowOut)) / 3600

/**
 * Druckverlustberechnung in Rohrleitungen nach https://www.schweizer-fn.de/stroemung/druckverlust/druckverlust.php#druckverlustrohr
 * Formel ist für Pa ausgelegt - umrechnung in Bar
 *
 * @param flowSpeed Double - Strömungsgeschwindigkeit in m/s
 * @param length Double - Länge der Rohrleitung in m
 * @param diameter Double - Rohrinnendurchmesser in m
 * @param lambda Double - Rohrwiederstandsbeiwert oder Rohrreibungszahl (TODO 0.15 von Wikipedia https://de.wikipedia.org/wiki/Rohrreibungszahl)
 * @param p Double - Dichte des Mediums in kg/m^3
 * @return Double - Druckverlust in Bar
 */
fun pipePressureLoss(flowSpeed: Double, length: Double, diameter: Double, lambda: Double = 0.15, p: Double = WATER_DICHTE) =
    (lambda * length * p * flowSpeed.pow(2)) / (diameter * 2) / 100_000

/**
 * Berechnet T_Allokation für Tagesmittelwerte
 *
 * @receiver List<Double>
 * @return List<Double>
 */
fun List<Double>.toAllocationTemperature() =
    indices.map { d -> t(d) + 0.5 * t(d - 1) + 0.25 * t(d - 2) + 0.125 * t(d - 3) / (1 + 0.5 + 0.25 + 0.125) }

private fun List<Double>.t(d: Int) = get((d+size) % size)