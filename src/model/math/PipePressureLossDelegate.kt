package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.PipeType
import de.fhac.ewi.model.delegate.CalculableDelegate
import de.fhac.ewi.util.flowRate
import de.fhac.ewi.util.pipePressureLoss
import de.fhac.ewi.util.subscribeIfChanged


class PipePressureLossDelegate<T>(private val pipe: Pipe) :
    CalculableDelegate<T>() {

    init {
        pipe::flowRate.subscribeIfChanged(this::updateValue)
        pipe::type.subscribeIfChanged(this::updateValue)
    }

    override fun recalculateIndexed(index: Int) = with(pipe) {
        // TODO Hier direkt Formel für pressure loss einsetzen?
        pipePressureLoss(flowRate[index], length, type.diameter) * 2
    }

}