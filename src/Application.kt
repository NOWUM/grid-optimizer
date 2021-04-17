package de.fhac.ewi

import de.fhac.ewi.routes.version
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
import org.slf4j.event.Level
import java.text.DateFormat

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

    install(StatusPages) {
        // Catch Exceptions and provide better responses then 500
        exception<Exception> { cause ->
            call.respond(HttpStatusCode.InternalServerError, "Oops! An internal error occurred: ${cause.message}")
        }
    }


    val apiConfig = environment.config.config("gridoptimizer.api") // read application.conf

    install(Koin) {
        modules(org.koin.dsl.module {
            // Add services for injection usage
        })
    }

    routing {
        when {
            isDev -> get("/") {
                // redirect to frontend dev server
                call.respondRedirect("http://localhost:8081")
            }
            isProd -> static("/") {
                // serve frontend with Ktor
                resources("dist")
                resource("/", "dist/index.html")
            }
        }

        route("api") {
            // add api endpoints here
            version(apiConfig)
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
