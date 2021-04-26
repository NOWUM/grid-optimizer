package de.fhac.ewi.util

import org.junit.Test
import javax.script.ScriptException
import kotlin.test.assertEquals

class EngineUtilTest {

    @Test
    fun testSimpleDoubleFunction() {
        val func = "x+3".toDoubleFunction()
        assertEquals(3.0, func(0.0))
        assertEquals(4.0, func(1.0))
        assertEquals(5.0, func(2.0))
    }

    @Test
    fun testSqrtDoubleFunction() {
        val func = "x*x + 3".toDoubleFunction()
        assertEquals(4.0, func(-1.0))
        assertEquals(4.0, func(1.0))
        assertEquals(7.0, func(2.0))
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnIllegalCharacter() {
        "x+3 + y".toDoubleFunction()
    }


    @Test(expected = ScriptException::class)
    fun shouldFailOnWrongSyntax() {
        "x+3 +".toDoubleFunction()
    }
}