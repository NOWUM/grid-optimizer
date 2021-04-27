package de.fhac.ewi.util

fun <R> catchAndThrowIllegalArgument(block: () -> R): R {
    return try {
        block()
    } catch (e: IllegalArgumentException) {
        throw e
    } catch (e: Exception) {
        throw IllegalArgumentException("Error parsing input. ${e.message}", e)
    }
}