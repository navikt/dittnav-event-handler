package no.nav.personbruker.dittnav.eventhandler.common.health

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.micrometer.prometheus.PrometheusMeterRegistry

fun Route.healthApi(healthService: HealthService, prometheusMeterRegistry: PrometheusMeterRegistry) {

    val pingJsonResponse = """{"ping": "pong"}"""

    get("/internal/isAlive") {
        call.respondText(text = "ALIVE", contentType = ContentType.Text.Plain)
    }

    get("/internal/isReady") {
        if (isReady(healthService)) {
            call.respondText(text = "READY", contentType = ContentType.Text.Plain)
        } else {
            call.respondText(text = "NOTREADY", contentType = ContentType.Text.Plain, status = HttpStatusCode.FailedDependency)
        }
    }

    get("/internal/ping") {
        call.respondText(pingJsonResponse, ContentType.Application.Json)
    }

    get("/internal/selftest") {
        call.buildSelftestPage(healthService)
    }

    get("/metrics") {
        call.respond(prometheusMeterRegistry.scrape())
    }
}

private fun isReady(healthService: HealthService): Boolean {
    // utvid eventuelt med logikk for å inkludere helsesjekker som skal påvirke om appen er ready eller ikke.
    return true
}
