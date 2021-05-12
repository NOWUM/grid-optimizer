package de.fhac.ewi.util

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import de.fhac.ewi.model.HeatDemandCurve
import de.fhac.ewi.model.LoadProfile
import de.fhac.ewi.model.TemperatureTimeSeries

private fun getResourceAsStream(resource: String) =
    TemperatureTimeSeries::class.java.classLoader.getResourceAsStream(resource)!!

fun loadTemperatureTimeSeries(): List<TemperatureTimeSeries> {
    return csvReader { delimiter = ';' }
        .readAll(getResourceAsStream("temperature-time-series.csv"))
        .transpose()
        .map { TemperatureTimeSeries(it.first(), it.drop(1).map(String::toDouble)) }
}

fun loadStandardLoadProfiles(): List<LoadProfile> {
    return csvReader { delimiter = ';' }
        .readAll(getResourceAsStream("standard-load-profiles.csv"))
        .transpose()
        .map { LoadProfile(it.first(), HeatDemandCurve(it.drop(1).map(String::toDouble))) }
}