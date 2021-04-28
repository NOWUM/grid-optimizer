package de.fhac.ewi.util

/**
 * Berechnet den Massenstrom.
 *
 * Formel: `m = Q * 860 / temp_diff`
 * Mit
 *  - Q = Heizleistung (bzw benötigte Energie)
 *  - temp_diff = Spreizung Vorlauf/Rücklauftemperatur
 *
 * Quelle: https://www.ibo-plan.de/rohrnetzberechnung-formeln.html
 *
 * @param flowIn Double - Vorlauftemperatur in °C
 * @param flowOut Double - Rücklauftemperatur in °C
 * @param heatDemand Double - Benötigte Wärmeenergie in kW
 * @return Double - Massenstrom in kg/h
 */
fun massenstrom(flowIn: Double, flowOut: Double, heatDemand: Double) = heatDemand * 860 / (flowIn - flowOut)