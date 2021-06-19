package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.PipeType
import de.fhac.ewi.model.delegate.CalculableDelegate
import de.fhac.ewi.util.flowRate
import de.fhac.ewi.util.neededPumpPower
import de.fhac.ewi.util.pipePressureLoss
import de.fhac.ewi.util.subscribeIfChanged


class PipePumpPowerDelegate<T>(private val pipe: Pipe) :
    CalculableDelegate<T>() {

    init {
        pipe::totalPressureLoss.subscribeIfChanged(this::updateValue)
        // volumeFlow does not need subscription, because it is in totalPressureLoss included
    }

    override fun recalculateIndexed(index: Int) = with(pipe) {
        neededPumpPower(totalPressureLoss[index], volumeFlow[index])
    }

}