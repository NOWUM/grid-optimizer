package de.fhac.ewi.util

import javax.script.ScriptEngineManager

typealias DoubleFunction = (Double) -> Double

private val TEMPLATE_PATTERN = "^(\\d+(\\.\\d+)?| |[-+/*]|\\(|\\)|x)+$".toRegex(RegexOption.IGNORE_CASE)
private val engine = ScriptEngineManager().getEngineByExtension("js")!!
private val functions = mutableMapOf<String, DoubleFunction>()

fun String.toDoubleFunction(): DoubleFunction {
    if (!matches(TEMPLATE_PATTERN))
        throw IllegalArgumentException("Template f(x)=$this does not match pattern ${TEMPLATE_PATTERN.pattern}.")

    return functions.getOrPut(this) {

        val functionName = "doubleFun${functions.size}"

        // Inserts the function
        engine.eval("function $functionName(x) { return parseFloat($this); }");

        { x: Double -> engine.eval("$functionName($x);").toString().toDouble() }
    }
}


