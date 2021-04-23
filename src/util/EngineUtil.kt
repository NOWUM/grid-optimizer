package de.fhac.ewi.util

import javax.script.ScriptEngineManager

private val engine = ScriptEngineManager().getEngineByExtension("js")!!
private val functions = mutableMapOf<String, ((Double) -> Double)>()
private val TEMPLATE_PATTERN = "^(\\d+(\\.\\d+)?| |[-+/*]|\\(|\\)|x)+$".toRegex(RegexOption.IGNORE_CASE)

fun parseDoubleFunction(template: String): ((Double) -> Double) {
    if (!template.matches(TEMPLATE_PATTERN))
        throw IllegalArgumentException("Template does not match pattern ${TEMPLATE_PATTERN.pattern}.")

    return functions.getOrPut(template) {

        val functionName = "doubleFun${functions.size}"

        // Inserts the function
        engine.eval("function $functionName(x) { return $template; }");

        { x: Double -> engine.eval("$functionName($x);") as Double }
    }
}


