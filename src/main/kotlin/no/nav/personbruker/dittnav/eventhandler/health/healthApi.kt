package no.nav.personbruker.dittnav.eventhandler.health

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.response.respondTextWriter
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat
import no.nav.personbruker.dittnav.eventhandler.common.health.HealthStatus
import no.nav.personbruker.dittnav.eventhandler.common.health.Status
import no.nav.personbruker.dittnav.eventhandler.config.ApplicationContext
import org.slf4j.LoggerFactory

fun Routing.healthApi(appContext: ApplicationContext, collectorRegistry: CollectorRegistry = CollectorRegistry.defaultRegistry) {

    val log = LoggerFactory.getLogger(HealthStatus::class.java)

    val pingJsonResponse = """{"ping": "pong"}"""

    get("/internal/isAlive") {
        call.respondText(text = "ALIVE", contentType = ContentType.Text.Plain)
    }

    get("/internal/isReady") {
        val healthChecks: List<HealthStatus> = listOf(appContext.database.status(), appContext.doneProducer.status())
        val hasFailedChecks = healthChecks.any { healthStatus -> Status.ERROR == healthStatus.status }
        if(hasFailedChecks) {
            call.respondText(text = "READY", contentType = ContentType.Text.Plain)
        } else {
            log.warn("En eller flere helsesjekker feilet, returnerer feilkode på /isReady.")
            call.response.status(HttpStatusCode.ServiceUnavailable)
        }
    }

    get("/internal/ping") {
        call.respondText(pingJsonResponse, ContentType.Application.Json)
    }

    get("/internal/selftest") {
        call.pingDependencies(appContext)
    }

    get("/metrics") {
        val names = call.request.queryParameters.getAll("name[]")?.toSet() ?: emptySet()
        call.respondTextWriter(ContentType.parse(TextFormat.CONTENT_TYPE_004)) {
            TextFormat.write004(this, collectorRegistry.filteredMetricFamilySamples(names))
        }
    }
}
