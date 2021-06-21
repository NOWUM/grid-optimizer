package de.fhac.ewi.exceptions

class IllegalRequestException(message: String, exception: Exception? = null) :
    IllegalArgumentException(message, exception)