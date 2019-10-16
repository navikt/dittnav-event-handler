package no.nav.personbruker.dittnav.eventhandler.config

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.*
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.routing.routing
import io.prometheus.client.hotspot.DefaultExports
import no.nav.personbruker.dittnav.eventhandler.common.healthApi
import no.nav.personbruker.dittnav.eventhandler.done.doneApi
import no.nav.personbruker.dittnav.eventhandler.informasjon.informasjonApi
import no.nav.personbruker.dittnav.eventhandler.oppgave.oppgaveApi

fun Application.mainModule(appContext: ApplicationContext = ApplicationContext()) {
    doDatabaseMigrationsIfApplicable(appContext)
    DefaultExports.initialize()
    install(DefaultHeaders)

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            registerModule(JavaTimeModule())
        }
    }

    install(Authentication) {
        jwt {
            setupOidcAuthentication(appContext.environment)
        }
    }

    routing {
        healthApi()
        authenticate {
            oppgaveApi(appContext.oppgaveEventService)
            informasjonApi(appContext.informasjonEventService)
            doneApi(appContext.doneEventService)
        }
    }

}

private fun doDatabaseMigrationsIfApplicable(appContext: ApplicationContext) {
    if (isRunningOnLocalhost()) {
        Flyway.runFlywayMigrations(appContext.environment)
    }
}

private fun isRunningOnLocalhost() = !ConfigUtil.isCurrentlyRunningOnNais()
