package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer

interface Strategy {
    fun apply(optimizer: Optimizer)
}