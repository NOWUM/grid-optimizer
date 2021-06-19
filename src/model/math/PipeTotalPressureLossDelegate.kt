package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.PipeType
import de.fhac.ewi.model.delegate.CalculableDelegate
import de.fhac.ewi.util.flowRate
import de.fhac.ewi.util.pipePressureLoss
import de.fhac.ewi.util.subscribeIfChanged


class PipeTotalPressureLossDelegate<T>(private val pipe: Pipe) :
    CalculableDelegate<T>() {

    init {
        pipe::pipePressureLoss.subscribeIfChanged(this::updateValue)
        pipe.target::pressureLoss.subscribeIfChanged(this::updateValue)
    }

    override fun recalculateIndexed(index: Int) = with(pipe) {
        pipePressureLoss[index] + target.pressureLoss[index]
    }

}