package de.fhac.ewi.util

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import de.fhac.ewi.model.TemperatureTimeSeries
import java.io.File

private fun getRessourceFile(file: String) = File(TemperatureTimeSeries::class.java.classLoader.getResource(file)!!.toURI())

fun loadTemperatureTimeSeries(): List<TemperatureTimeSeries> {
    return csvReader { delimiter = ';' }
        .readAll(getRessourceFile("temperature-time-series.csv"))
        .transpose()
        .map { TemperatureTimeSeries(it.first(), it.drop(1).map(String::toDouble)) }
}