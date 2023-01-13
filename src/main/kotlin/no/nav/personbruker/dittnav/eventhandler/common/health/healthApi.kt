package no.nav.personbruker.dittnav.eventhandler.common.health

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.micrometer.prometheus.PrometheusMeterRegistry

fun Route.healthApi(healthService: HealthService, prometheusMeterRegistry: PrometheusMeterRegistry) {

    val pingJsonResponse = """{"ping": "pong"}"""

    get("/internal/isAlive") {
        call.respondText(text = "ALIVE", contentType = ContentType.Text.Plain)
    }

    get("/internal/isReady") {
        if (isReady()) {
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

private fun isReady(): Boolean {
    return true
}
