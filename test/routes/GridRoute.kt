package de.fhac.ewi.routes

import de.fhac.ewi.module
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertEquals

class GridRoute {

    @Test
    fun wrongMethodOnValidate() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/api/grid/validate").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun wrongMediaTypeOnValidate() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/api/grid/validate").apply {
                assertEquals(HttpStatusCode.UnsupportedMediaType, response.status())
            }
        }
    }

    @Test
    fun emptyInputOnValidate() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/api/grid/validate") {
                addHeader("content-type", "application/json")
                setBody("")
            }.apply {
                assertEquals(HttpStatusCode.InternalServerError, response.status())
            }

        }
    }

}