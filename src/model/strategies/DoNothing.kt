package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer

/**
 * Dummy Strategie. Nimmt keine Veränderung vor.
 */
object DoNothing : Strategy {
    override fun apply(optimizer: Optimizer) { /* This strategies does nothing */ }
}