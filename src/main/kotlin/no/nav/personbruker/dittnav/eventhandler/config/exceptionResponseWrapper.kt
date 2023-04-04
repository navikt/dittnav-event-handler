package no.nav.personbruker.dittnav.eventhandler.config


import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond
import org.slf4j.Logger

fun StatusPagesConfig.configureErrorResponses() {
    exception<Throwable> { call, cause ->
        when (cause) {
            is RetriableDatabaseException -> {
                call.respond(HttpStatusCode.ServiceUnavailable)
                val msg =
                    "Periodisk feil mot databasen, klarte ikke hente eventer fra cache. Returnerer feilkode til frontend. {}"
                log.error(msg, cause.toString(), cause)
            }

            is UnretriableDatabaseException -> {
                call.respond(HttpStatusCode.ServiceUnavailable)
                val msg =
                    "Fikk ikke kontakt med databasen, klarte ikke hente eventer fra cache. Returnerer feilkode til frontend. {}"
                log.error(msg, cause.toString(), cause)
            }

            is BackupEventException -> {
                call.respond(HttpStatusCode.FailedDependency)
                val msg = "Fikk feil når vi prøvde å skrive til backup-topic-en. Returnerer feilkode. {}"
                log.error(msg, cause.toString(), cause)
            }

            else -> {
                call.respond(HttpStatusCode.InternalServerError)
                log.error(
                    "Ukjent feil oppstod ved henting av eventer fra cache. Returnerer feilkode til frontend",
                    cause.toString(), cause
                )
            }
        }
    }
}
