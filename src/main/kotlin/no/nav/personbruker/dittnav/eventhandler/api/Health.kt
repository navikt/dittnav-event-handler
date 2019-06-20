package no.nav.personbruker.dittnav.eventhandler.api

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get

fun Routing.healthApi() {

    val pingJsonResponse = """{"ping": "pong"}"""
    var vaultEnvironmentName : String = System.getenv("hemmelig_key") ?: "default_value"


    get("/isAlive") {
        call.respondText(text = "ALIVE", contentType = ContentType.Text.Plain)
    }

    get("/isReady") {
        call.respondText(text = "READY", contentType = ContentType.Text.Plain)
    }

    get("/ping") {
        call.respondText(pingJsonResponse, ContentType.Application.Json)
    }

    get("/vault_env") {
        call.respondText(text = vaultEnvironmentName, contentType = ContentType.Text.Plain)
    }

}
