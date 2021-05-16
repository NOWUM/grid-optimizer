package de.fhac.ewi.model.heatprofile

/**
 * h-Profil aus Excel von Jungbluth.
 *
 *
 * @property profileName String
 * @property a Double
 * @property b Double
 * @property c Double
 * @property d Double
 * @property zero Double
 * @property mH Double
 * @property bH Double
 * @property mW Double
 * @property bW Double
 * @constructor
 */
data class HProfile(
    val profileName: String,
    val a: Double,
    val b: Double,
    val c: Double,
    val d: Double,
    val zero: Double,
    val mH: Double,
    val bH: Double,
    val mW: Double,
    val bW: Double,
    val hourDistribution: HourDistribution
)
