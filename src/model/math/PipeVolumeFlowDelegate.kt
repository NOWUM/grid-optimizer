package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.delegate.LazyCalculableDoubleArray
import de.fhac.ewi.util.subscribeIfChanged
import de.fhac.ewi.util.updateIfNeeded
import de.fhac.ewi.util.volumeFlow


/**
 * ### Volumenstrom in Rohrleitung
 * ... wird aus dem Massenstrom bestimmt.
 *
 * @param T - For Delegate
 * @property pipe Pipe - Die Rohrleitung des Delegates
 * @constructor
 */
class PipeVolumeFlowDelegate<T>(val pipe: Pipe) : LazyCalculableDoubleArray<T>() {

    init {
        pipe::massenstrom.subscribeIfChanged(this)
    }

    override fun recalculate(): DoubleArray {
        val massenstrom = pipe.massenstrom
        return DoubleArray(8760) { index -> volumeFlow(massenstrom[index]) }
    }

    override fun checkForChanges() {
        pipe::massenstrom.updateIfNeeded() // on change it will trigger recalculation of this property
    }

}