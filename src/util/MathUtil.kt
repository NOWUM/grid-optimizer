package de.fhac.ewi.util

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
 * @return Double - Massenstrom in kg/h
 */
fun massenstrom(flowIn: Double, flowOut: Double, heatDemand: Double, c: Double = 4.187) =
    (heatDemand * 10_000) / (c * (flowIn - flowOut))

/**
 * Druckverlustberechnung in Rohrleitungen nach VL 2020 09 Wärmeverteilung Folie 4, Jungbluth
 *
 * @param flowSpeed Double - Strömungsgeschwindigkeit in m/s
 * @param length Double - Länge der Rohrleitung in m
 * @param diameter Double - Rohrinnendurchmesser in m
 * @param lambda Double - Rohrwiederstandsbeiwert oder Rohrreibungszahl (TODO 0.15 von Wikipedia https://de.wikipedia.org/wiki/Rohrreibungszahl)
 * @param p Double - Dichte des Mediums in kg/m^3
 * @return Double - Druckverlust in kg/(m*s^2)
 */
fun pipePressureLoss(flowSpeed: Double, length: Double, diameter: Double, lambda: Double = 0.15, p: Double = WATER_DICHTE) =
    lambda * length / diameter * p / 2 * flowSpeed * flowSpeed

/**
 * Berechnet T_Allokation für Tagesmittelwerte
 *
 * @receiver List<Double>
 * @return List<Double>
 */
fun List<Double>.toAllocationTemperature() =
    indices.map { d -> t(d) + 0.5 * t(d - 1) + 0.25 * t(d - 2) + 0.125 * t(d - 3) / (1 + 0.5 + 0.25 + 0.125) }

private fun List<Double>.t(d: Int) = get((d+size) % size)