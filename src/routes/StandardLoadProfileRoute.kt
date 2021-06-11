package de.fhac.ewi.routes

import de.fhac.ewi.services.LoadProfileService
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.profiles(service: LoadProfileService) {
    route("/profiles") {
        get("/names") {
            call.respond(service.getAllHProfileNames())
        }
        get("/{profileName}/parameters") {
            val profileName =
                call.parameters["profileName"] ?: throw IllegalArgumentException("No profile name provided.")
            call.respond(service.getHProfile(profileName))
        }
    }
}