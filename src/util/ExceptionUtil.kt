package de.fhac.ewi.util

fun <R> catchAndThrowIllegalArgument(errorMessage: String = "Error parsing input.", block: () -> R): R {
    return try {
        block()
    } catch (e: IllegalArgumentException) {
        throw e
    } catch (e: Exception) {
        throw IllegalArgumentException("$errorMessage ${e.message}", e)
    }
}