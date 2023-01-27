package no.nav.personbruker.dittnav.eventhandler.common.exceptions


import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedNotFoundException
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.database.RetriableDatabaseException
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.database.UnretriableDatabaseException
import no.nav.personbruker.dittnav.eventhandler.config.log
import org.slf4j.Logger

suspend fun respondWithError(call: ApplicationCall, log: Logger, exception: Exception) {
    when (exception) {

        is RetriableDatabaseException -> {
            call.respond(HttpStatusCode.ServiceUnavailable)
            val msg =
                "Periodisk feil mot databasen, klarte ikke hente eventer fra cache. Returnerer feilkode til frontend. {}"
            log.error(msg, exception.toString(), exception)
        }

        is UnretriableDatabaseException -> {
            call.respond(HttpStatusCode.ServiceUnavailable)
            val msg =
                "Fikk ikke kontakt med databasen, klarte ikke hente eventer fra cache. Returnerer feilkode til frontend. {}"
            log.error(msg, exception.toString(), exception)
        }

        is BackupEventException -> {
            call.respond(HttpStatusCode.FailedDependency)
            val msg = "Fikk feil når vi prøvde å skrive til backup-topic-en. Returnerer feilkode. {}"
            log.error(msg, exception.toString(), exception)
        }

        else -> {
            call.respond(HttpStatusCode.InternalServerError)
            log.error(
                "Ukjent feil oppstod ved henting av eventer fra cache. Returnerer feilkode til frontend",
                exception
            )
        }
    }
}

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

            is BeskjedNotFoundException -> {
                log.warn { cause.message }
                call.respond(status = HttpStatusCode.BadRequest, message = cause.message.toString())
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
