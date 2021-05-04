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
fun massenstrom(flowIn: Double, flowOut: Double, heatDemand: Double, c: Double = 4.187) = (heatDemand * 10_000) / (c * (flowIn - flowOut))