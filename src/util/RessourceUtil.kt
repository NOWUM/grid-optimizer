package de.fhac.ewi.util

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import de.fhac.ewi.model.TemperatureTimeSeries
import de.fhac.ewi.model.heatprofile.HProfile
import de.fhac.ewi.model.heatprofile.HourDistribution

private fun getResourceAsStream(resource: String) =
    TemperatureTimeSeries::class.java.classLoader.getResourceAsStream(resource)

fun getHProfileNames() = listOf("EFH", "MFH")

fun loadTemperatureTimeSeries(): List<TemperatureTimeSeries> {
    return csvReader { delimiter = ';' }
        .readAll(getResourceAsStream("temperature-time-series.csv")!!)
        .transpose()
        .map { TemperatureTimeSeries(it.first(), it.drop(1).map(String::toDouble)) }
}

fun loadHProfiles(): List<HProfile> {
    val reader = csvReader { delimiter = ';' }
    return getHProfileNames().map { profile ->
        val ressource = getResourceAsStream("loadprofiles/$profile.csv")!!
        val input = reader.readAll(ressource)
        val parameters = input.first().take(9).map { it.toDouble() }
        val hourDistribution = input.drop(1).map { row -> row.take(26).map { it.toDouble() } }
            .associate { list -> list.component1()..list.component2() to list.drop(2) }
            .run { HourDistribution(this) }
        HProfile(
            profile, parameters[0], parameters[1], parameters[2],
            parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], parameters[8],
            hourDistribution
        )
    }
}