package de.fhac.ewi.util

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
 * Berechnet T_Allokation für Tagesmittelwerte
 *
 * @receiver List<Double>
 * @return List<Double>
 */
fun List<Double>.toAllocationTemperature() =
    indices.map { d -> t(d) + 0.5 * t(d - 1) + 0.25 * t(d - 2) + 0.125 * t(d - 3) / (1 + 0.5 + 0.25 + 0.125) }

private fun List<Double>.t(d: Int) = get(d % size)