package no.nav.personbruker.dittnav.eventhandler

import io.ktor.application.Application
import io.mockk.mockk
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedEventService
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.health.HealthService
import no.nav.personbruker.dittnav.eventhandler.config.eventHandlerApi
import no.nav.personbruker.dittnav.eventhandler.done.DoneEventService
import no.nav.personbruker.dittnav.eventhandler.done.DoneProducer
import no.nav.personbruker.dittnav.eventhandler.event.EventRepository
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksEventService
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveEventService
import no.nav.personbruker.dittnav.eventhandler.statistics.EventStatisticsService
import no.nav.tms.token.support.authentication.installer.mock.installMockedAuthenticators
import no.nav.tms.token.support.tokenx.validation.mock.SecurityLevel

fun mockEventHandlerApi(
    healthService: HealthService = mockk(relaxed = true),
    beskjedEventService: BeskjedEventService = mockk(relaxed = true),
    oppgaveEventService: OppgaveEventService = mockk(relaxed = true),
    innboksEventService: InnboksEventService = mockk(relaxed = true),
    doneEventService: DoneEventService = mockk(relaxed = true),
    eventRepository: EventRepository = mockk(relaxed = true),
    eventStatisticsService: EventStatisticsService = mockk(relaxed = true),
    database: Database = mockk(relaxed = true),
    doneProducer: DoneProducer = mockk(relaxed = true),
    installAuthenticatorsFunction: Application.() -> Unit = {
        installMockedAuthenticators {
            installTokenXAuthMock {
                setAsDefault = true

                alwaysAuthenticated = true
                staticUserPid = "123"
                staticSecurityLevel = SecurityLevel.LEVEL_4
            }
            installAzureAuthMock { }
        }
    }
): Application.() -> Unit {
    return fun Application.() {
        eventHandlerApi(
            healthService = healthService,
            beskjedEventService = beskjedEventService,
            oppgaveEventService = oppgaveEventService,
            innboksEventService = innboksEventService,
            doneEventService = doneEventService,
            eventRepository = eventRepository,
            eventStatisticsService = eventStatisticsService,
            database = database,
            doneProducer = doneProducer,
            installAuthenticatorsFunction = installAuthenticatorsFunction
        )
    }
}
