package de.fhac.ewi

import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/api/version").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }
}
