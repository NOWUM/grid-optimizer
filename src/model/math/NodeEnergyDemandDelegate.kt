package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.delegate.CalculableDelegate
import de.fhac.ewi.model.delegate.PipeBasedCalculableDelegate
import de.fhac.ewi.util.subscribeIfChanged

/**
 * Der Energiebedarf in einem Knotenpunkt setzt sich aus der Summe des Energiebedarfs der angeschlossenen Leitungen zusammen.
 *
 * Sollte es sich um einen OutputNode handeln, hat dieser einen direkten Energiebedarf. Dieser ist nicht über den Delegate abgebildet.
 *
 * TODO Kommentar neu generieren
 */
class NodeEnergyDemandDelegate<T>: PipeBasedCalculableDelegate<T>() {

    override fun recalculateIndexed(index: Int, pipes: List<Pipe>): Double {
        return pipes.sumOf { it.energyDemand[index] }
    }

    override fun onPipeConnect(pipe: Pipe) {
        pipe::energyDemand.subscribeIfChanged(this::updateValue)
    }

}