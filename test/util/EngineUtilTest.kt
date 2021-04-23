package de.fhac.ewi.util

import org.junit.Test
import kotlin.test.assertEquals

class EngineUtilTest {

    @Test
    fun testSimpleDoubleFunction() {
        val template = "x+3"
        val func = parseDoubleFunction(template)
        assertEquals(3.0, func(0.0))
        assertEquals(4.0, func(1.0))
        assertEquals(5.0, func(2.0))
    }

    @Test
    fun testSqrtDoubleFunction() {
        val template = "x*x + 3"
        val func = parseDoubleFunction(template)
        assertEquals(4.0, func(-1.0))
        assertEquals(4.0, func(1.0))
        assertEquals(7.0, func(2.0))
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnIllegalCharacter() {
        val template = "x+3 + y"
        parseDoubleFunction(template)
    }
}