package no.nav.personbruker.dittnav.eventhandler.config


import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import mu.KotlinLogging




fun StatusPagesConfig.configureErrorResponses() {
    val log = KotlinLogging.logger {}
    val securelog = KotlinLogging.logger("secureLog")

    exception<Throwable> { call, cause ->
        val prefix = "Feil i kall til i kall til ${call.request.uri}:"


        when (cause) {
            is RetriableDatabaseException -> {
                log.error("$prefix: Periodisk feil mot databasen", cause.toString(), cause)
                securelog.error { "$prefix: ${cause.securelogMessage()}" }
                call.respond(HttpStatusCode.ServiceUnavailable)
            }

            is UnretriableDatabaseException -> {
                log.error("$prefix: Fikk ikke kontakt med databasen", cause.toString(), cause)
                securelog.error { "$prefix: ${cause.securelogMessage()}" }

                call.respond(HttpStatusCode.ServiceUnavailable)
            }

            else -> {
                log.error(
                    "$prefix: ukjent feil",
                    cause.toString(), cause
                )
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}
