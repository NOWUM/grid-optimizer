package de.fhac.ewi.util

import de.fhac.ewi.model.Grid
import de.fhac.ewi.model.Node
import de.fhac.ewi.model.Pipe

fun Grid.gridTreeString() = buildString {
    appendLine("Einspeisepunkt ${input.id}")
    appendLine("|=== Pumpe mit ${(neededPumpPower / 1000).round(3)} kW (für ${input.pressureLoss.maxOrNull()?.round(2)} Bar)")
    for (child in input.connectedChildNodes) {
        nodeTreeString(child)
    }
}


private fun StringBuilder.nodeTreeString(node: Node, indent: Int = 0) {
    append("|    ${" ".repeat(20)}     ".repeat(indent))
    append(
        "+--- ${
            node.pathToSource.first().run { "$id Ø ${(type.diameter * 1000).toInt()} mm (${length.round(1)} m)".padEnd(20) }
        } --- "
    )
    appendLine("${node.javaClass.simpleName} ${node.id}")
    for (child in node.connectedChildNodes) {
        nodeTreeString(child, indent + 1)
    }

}

fun Array<Pipe>.pathLength(): Double = sumOf(Pipe::length)

/**
 *
 * @receiver Array<Pipe>
 * @return Double - Druckverlust entlang des Pfades [Bar]
 */
fun Array<Pipe>.maxPipePressureLoss(): Double = sumOf { it.pipePressureLoss.maxOrElse() }

/**
 * Druckverlust im Pfad.
 *
 * @receiver Array<Pipe>
 * @return Double - Druckverlust pro Meter entlang des Pfades [Pa/m]
 */
fun Array<Pipe>.maxPipePressureLossPerMeter(): Double = maxPipePressureLoss() / pathLength() * 100_000