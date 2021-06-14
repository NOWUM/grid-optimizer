package de.fhac.ewi.util

import de.fhac.ewi.exceptions.IllegalRequestException

fun <R> catchParseError(errorMessage: String = "Error parsing input.", block: () -> R): R {
    return try {
        block()
    } catch (e: IllegalRequestException) {
        throw e
    } catch (e: Exception) {
        throw IllegalRequestException("$errorMessage ${e.message}", e)
    }
}