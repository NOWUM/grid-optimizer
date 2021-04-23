package de.fhac.ewi.util

import javax.script.ScriptEngineManager

private val engine = ScriptEngineManager().getEngineByExtension("js")!!
private val functions = mutableMapOf<String, ((Double) -> Double)>()

fun parseDoubleFunction(template: String): ((Double) -> Double) {
    if (functions.containsKey(template))
        return functions[template]!!


    return functions.getOrPut(template) {

        val functionName = "doubleFun${functions.size}"

        // Inserts the function
        engine.eval("function $functionName(x) { return $template; }");

        { x: Double -> engine.eval("$functionName($x);") as Double }
    }
}


