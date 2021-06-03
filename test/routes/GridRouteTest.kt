package de.fhac.ewi.routes

import de.fhac.ewi.module
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.assertEquals

class GridRouteTest {

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


    @Test
    fun complexInputOnValidate() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/api/grid/validate") {
                addHeader("content-type", "application/json")
                setBody("{\"pipes\":[{\"source\":\"v1-1622708366768-1006684176359\",\"sourceHandle\":\"b\",\"target\":\"v1-1622708370768-5632974449971\",\"targetHandle\":null,\"animated\":true,\"type\":\"step\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v1-1622708675763-1327210173111\",\"length\":1},{\"source\":\"v1-1622708366768-1006684176359\",\"sourceHandle\":\"b\",\"target\":\"v1-1622708386023-3733855197372\",\"targetHandle\":null,\"animated\":true,\"type\":\"step\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v1-1622708682321-3733205370702\",\"length\":1},{\"source\":\"v1-1622708329492-8763896479697\",\"sourceHandle\":\"a\",\"target\":\"v1-1622708366768-1006684176359\",\"targetHandle\":null,\"animated\":true,\"type\":\"step\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v1-1622708690821-9945043114377\",\"length\":1}],\"inputNodes\":[{\"flowTemperatureTemplate\":\"x*0+70\",\"returnTemperatureTemplate\":\"x*0+60\",\"data\":{\"label\":\"Default Einspeisepunkt\"},\"position\":{\"x\":651,\"y\":194},\"type\":\"INPUT_NODE\",\"id\":\"v1-1622708329492-8763896479697\"}],\"intermediateNodes\":[{\"id\":\"v1-1622708366768-1006684176359\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":927,\"y\":409},\"data\":{\"label\":\"Default Kreuzungspunkt\"}}],\"outputNodes\":[{\"thermalEnergyDemand\":20000,\"pressureLoss\":1,\"data\":{\"label\":\"Haus 1\"},\"position\":{\"x\":868,\"y\":581},\"type\":\"OUTPUT_NODE\",\"id\":\"v1-1622708370768-5632974449971\",\"loadProfileName\":\"EFH\"},{\"thermalEnergyDemand\":30000,\"pressureLoss\":1,\"data\":{\"label\":\"Haus 2\"},\"position\":{\"x\":1014,\"y\":573},\"type\":\"OUTPUT_NODE\",\"id\":\"v1-1622708386023-3733855197372\",\"loadProfileName\":\"EFH\"}],\"temperatureSeries\":\"Schemm 2018\"}")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }

        }
    }

    @Test
    fun complexInputOnMassenstrom() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/api/grid/maxmassenstrom") {
                addHeader("content-type", "application/json")
                setBody("{\"pipes\":[{\"source\":\"v1-1622708366768-1006684176359\",\"sourceHandle\":\"b\",\"target\":\"v1-1622708370768-5632974449971\",\"targetHandle\":null,\"animated\":true,\"type\":\"step\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v1-1622708675763-1327210173111\",\"length\":1},{\"source\":\"v1-1622708366768-1006684176359\",\"sourceHandle\":\"b\",\"target\":\"v1-1622708386023-3733855197372\",\"targetHandle\":null,\"animated\":true,\"type\":\"step\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v1-1622708682321-3733205370702\",\"length\":1},{\"source\":\"v1-1622708329492-8763896479697\",\"sourceHandle\":\"a\",\"target\":\"v1-1622708366768-1006684176359\",\"targetHandle\":null,\"animated\":true,\"type\":\"step\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v1-1622708690821-9945043114377\",\"length\":1}],\"inputNodes\":[{\"flowTemperatureTemplate\":\"x*0+70\",\"returnTemperatureTemplate\":\"x*0+60\",\"data\":{\"label\":\"Default Einspeisepunkt\"},\"position\":{\"x\":651,\"y\":194},\"type\":\"INPUT_NODE\",\"id\":\"v1-1622708329492-8763896479697\"}],\"intermediateNodes\":[{\"id\":\"v1-1622708366768-1006684176359\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":927,\"y\":409},\"data\":{\"label\":\"Default Kreuzungspunkt\"}}],\"outputNodes\":[{\"thermalEnergyDemand\":20000,\"pressureLoss\":1,\"data\":{\"label\":\"Haus 1\"},\"position\":{\"x\":868,\"y\":581},\"type\":\"OUTPUT_NODE\",\"id\":\"v1-1622708370768-5632974449971\",\"loadProfileName\":\"EFH\"},{\"thermalEnergyDemand\":30000,\"pressureLoss\":1,\"data\":{\"label\":\"Haus 2\"},\"position\":{\"x\":1014,\"y\":573},\"type\":\"OUTPUT_NODE\",\"id\":\"v1-1622708386023-3733855197372\",\"loadProfileName\":\"EFH\"}],\"temperatureSeries\":\"Schemm 2018\"}")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }

        }
    }

}