package de.fhac.ewi

import de.fhac.ewi.routes.grid
import de.fhac.ewi.routes.profiles
import de.fhac.ewi.routes.temperature
import de.fhac.ewi.routes.version
import de.fhac.ewi.services.GridService
import de.fhac.ewi.services.HeatDemandService
import de.fhac.ewi.services.LoadProfileService
import de.fhac.ewi.services.TemperatureTimeSeriesService
import de.fhac.ewi.util.loadHProfiles
import de.fhac.ewi.util.loadTemperatureTimeSeries
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.get
import org.slf4j.event.Level
import java.text.DateFormat
import javax.script.ScriptException

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Sessions) {
        // Add session cookies here
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }

    if (isDev) {
        install(CORS) {
            // Allow everything for development
            method(HttpMethod.Options)
            allowXHttpMethodOverride()
            allowHeaders { true }
            exposeHeader(HttpHeaders.AccessControlAllowOrigin)
            anyHost()
            allowCredentials = true
            allowSameOrigin = true
            allowNonSimpleContentTypes = true
        }
    }

    install(StatusPages) {
        exception<IllegalArgumentException> { cause ->
            cause.printStackTrace()
            call.respond(HttpStatusCode.BadRequest, "${cause.message}")
        }
        exception<ScriptException> { cause ->
            call.respond(HttpStatusCode.BadRequest, "${cause.message}")
        }

        // Catch Exceptions and provide better responses then 500
        exception<Throwable> { cause ->
            call.respond(HttpStatusCode.InternalServerError, "Oops! An internal error occurred: ${cause.message}")
        }
    }


    val apiConfig = environment.config.config("gridoptimizer.api") // read application.conf

    install(Koin) {
        modules(org.koin.dsl.module {
            // Add services for injection usage
            single { TemperatureTimeSeriesService(loadTemperatureTimeSeries()) }
            single { LoadProfileService(loadHProfiles()) }
            single { HeatDemandService(get(), get()) }
            single { GridService(get(), get()) }
        })
    }

    routing {
        route("api") {
            // add api endpoints here
            grid(get())
            temperature(get())
            profiles(get())
            version(apiConfig)
        }

        when {
            isDev -> get("/") {
                // redirect to frontend dev server
                call.respondRedirect("http://localhost:3000")
            }
            isProd -> static("/") {
                // serve frontend with Ktor
                resources("build")
                resource("/", "build/index.html")
            }
        }
    }


    // Print all Roots
    val root = feature(Routing)
    val allRoutes = allRoutes(root)
    val allRoutesWithMethod = allRoutes.filter { it.selector is HttpMethodRouteSelector }
    allRoutesWithMethod.forEach {
        environment.log.info("route: $it")
    }

    environment.log.info("Testing-Mode: $testing")
}

val Application.envKind get() = environment.config.propertyOrNull("ktor.environment")?.getString()
val Application.isDev get() = envKind != null && envKind == "dev"
val Application.isProd get() = envKind != null && envKind != "dev"

fun allRoutes(root: Route): List<Route> {
    return listOf(root) + root.children.flatMap { allRoutes(it) }
}

