package de.fhac.ewi.util

import org.junit.Test
import kotlin.streams.toList
import kotlin.system.measureTimeMillis

class PerformanceTest {

    private val ARRAY_SIZE = 3000
    private val TIMES = 2

    @Test
    fun calculateWithList() {
        val dFunc = "x*4+5".toDoubleFunction()
        val a = List(ARRAY_SIZE) {it.toDouble()}
        val b = List(ARRAY_SIZE) {it}
        val c = List(ARRAY_SIZE) {it.toDouble()}
        val time = measureTimeMillis {
            repeat(TIMES) {
                a.indices.map { a[it] * b[it] + dFunc(c[it]) }
            }
        }
        println("Test List took $time ms.")
    }

    @Test
    fun calculateWithArray() {
        val dFunc = "x*4+5".toDoubleFunction()
        val a = Array(ARRAY_SIZE) {it.toDouble()}
        val b = Array(ARRAY_SIZE) {it}
        val c = Array(ARRAY_SIZE) {it.toDouble()}
        val time = measureTimeMillis {
            repeat(TIMES) {
                a.indices.map { a[it] * b[it] + dFunc(c[it]) }
            }
        }
        println("Test Array took $time ms.")
    }


    @Test
    fun calculateWithDoubleArray() {
        val dFunc = "x*4+5".toDoubleFunction()
        val a = DoubleArray(ARRAY_SIZE) {it.toDouble()}
        val b = IntArray(ARRAY_SIZE) {it}
        val c = DoubleArray(ARRAY_SIZE) {it.toDouble()}
        val time = measureTimeMillis {
            repeat(TIMES) {
                a.indices.map { a[it] * b[it] + dFunc(c[it]) }
            }
        }
        println("Test Double Array took $time ms.")
    }

    @Test
    fun calculateWithArrayParallel() {
        val dFunc = "x*4+5".toDoubleFunction()
        val a = Array(ARRAY_SIZE) {it.toDouble()}
        val b = Array(ARRAY_SIZE) {it}
        val c = Array(ARRAY_SIZE) {it.toDouble()}
        val time = measureTimeMillis {
            repeat(TIMES) {
                a.indices.toList().parallelStream().map { a[it] * b[it] + dFunc(c[it]) }.toList()
            }
        }
        println("Test Array took $time ms.")
    }

}