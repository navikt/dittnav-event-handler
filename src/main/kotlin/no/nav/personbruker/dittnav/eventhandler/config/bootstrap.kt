package no.nav.personbruker.dittnav.eventhandler.config

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationStopPreparing
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.authenticate
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.util.pipeline.PipelineContext
import io.prometheus.client.hotspot.DefaultExports
import no.nav.personbruker.dittnav.common.util.config.StringEnvVar
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedEventService
import no.nav.personbruker.dittnav.eventhandler.beskjed.beskjedApi
import no.nav.personbruker.dittnav.eventhandler.beskjed.beskjedSystemClientApi
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.health.HealthService
import no.nav.personbruker.dittnav.eventhandler.common.health.healthApi
import no.nav.personbruker.dittnav.eventhandler.done.DoneEventService
import no.nav.personbruker.dittnav.eventhandler.done.DoneProducer
import no.nav.personbruker.dittnav.eventhandler.done.doneApi
import no.nav.personbruker.dittnav.eventhandler.done.doneSystemClientApi
import no.nav.personbruker.dittnav.eventhandler.event.EventRepository
import no.nav.personbruker.dittnav.eventhandler.event.eventApi
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksEventService
import no.nav.personbruker.dittnav.eventhandler.innboks.innboksApi
import no.nav.personbruker.dittnav.eventhandler.innboks.innboksSystemClientApi
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveEventService
import no.nav.personbruker.dittnav.eventhandler.oppgave.oppgaveApi
import no.nav.personbruker.dittnav.eventhandler.oppgave.oppgaveSystemClientApi
import no.nav.personbruker.dittnav.eventhandler.statistics.EventStatisticsService
import no.nav.personbruker.dittnav.eventhandler.statistics.statisticsSystemClientApi
import no.nav.tms.token.support.authentication.installer.installAuthenticators
import no.nav.tms.token.support.azure.validation.AzureAuthenticator
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import no.nav.tms.token.support.tokenx.validation.user.TokenXUserFactory

fun Application.eventHandlerApi(
    healthService: HealthService,
    beskjedEventService: BeskjedEventService,
    oppgaveEventService: OppgaveEventService,
    innboksEventService: InnboksEventService,
    doneEventService: DoneEventService,
    eventRepository: EventRepository,
    eventStatisticsService: EventStatisticsService,
    database: Database,
    doneProducer: DoneProducer,
    installAuthenticatorsFunction: Application.() -> Unit = installAuth()
) {

    DefaultExports.initialize()
    install(DefaultHeaders)

    install(ContentNegotiation) {
        json()
    }

    installAuthenticatorsFunction()

    routing {
        route("/dittnav-event-handler") {
            healthApi(healthService)
            authenticate {
                doneApi(doneEventService)
                beskjedApi(beskjedEventService)
                innboksApi(innboksEventService)
                oppgaveApi(oppgaveEventService)
                eventApi(eventRepository)
            }
            authenticate(AzureAuthenticator.name) {
                doneSystemClientApi(doneEventService)
                beskjedSystemClientApi(beskjedEventService)
                innboksSystemClientApi(innboksEventService)
                oppgaveSystemClientApi(oppgaveEventService)

                statisticsSystemClientApi(eventStatisticsService)
            }
        }
    }
    configureShutdownHook(database, doneProducer)
}

private fun installAuth(): Application.() -> Unit = {
    installAuthenticators {
        installTokenXAuth {
            setAsDefault = true
        }
        installAzureAuth {
            setAsDefault = false
        }
    }
}

private fun Application.configureShutdownHook(database: Database, doneProducer: DoneProducer) {
    environment.monitor.subscribe(ApplicationStopPreparing) {
        closeTheDatabaseConectionPool(database)
        doneProducer.flushAndClose()
    }
}

private fun closeTheDatabaseConectionPool(database: Database) {
    database.close()
}

val PipelineContext<Unit, ApplicationCall>.innloggetBruker: TokenXUser
    get(): TokenXUser {
        val claimName = StringEnvVar.getOptionalEnvVar("OIDC_CLAIM_CONTAINING_THE_IDENTITY")
        return if (claimName.isNullOrEmpty()) {
            TokenXUserFactory.createTokenXUser(call)
        } else {
            TokenXUserFactory.createTokenXUser(call, claimName)
        }
    }
