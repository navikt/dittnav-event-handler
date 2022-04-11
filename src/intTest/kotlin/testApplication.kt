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
import no.nav.personbruker.dittnav.eventhandler.statusoppdatering.StatusoppdateringEventService
import no.nav.tms.token.support.authentication.installer.mock.installMockedAuthenticators

fun mockEventHandlerApi(
    healthService: HealthService = mockk(relaxed = true),
    beskjedEventService: BeskjedEventService = mockk(relaxed = true),
    oppgaveEventService: OppgaveEventService = mockk(relaxed = true),
    innboksEventService: InnboksEventService = mockk(relaxed = true),
    doneEventService: DoneEventService = mockk(relaxed = true),
    statusoppdateringEventService: StatusoppdateringEventService = mockk(relaxed = true),
    eventRepository: EventRepository = mockk(relaxed = true),
    eventStatisticsService: EventStatisticsService = mockk(relaxed = true),
    database: Database = mockk(relaxed = true),
    doneProducer: DoneProducer = mockk(relaxed = true),
    installAuthenticatorsFunction: Application.() -> Unit = {
        installMockedAuthenticators {
            installTokenXAuthMock {
                setAsDefault = true
                alwaysAuthenticated = true
                staticJwtOverride = dummyJwt
            }
            installAzureAuthMock {  }
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
            statusoppdateringEventService = statusoppdateringEventService,
            eventRepository = eventRepository,
            eventStatisticsService = eventStatisticsService,
            database = database,
            doneProducer = doneProducer,
            installAuthenticatorsFunction = installAuthenticatorsFunction
        )
    }
}

/* dummyJwt:
{
"acr_values":"Level4",
"acr":"Level4",
"pid":"123",
"exp":1649678305,
"iat":1649674705,
"jti":"STUB"
}
 */
private const val dummyJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhY3JfdmFsdWVzIjoiTGV2ZWw0IiwiYWNyIjoiTGV2ZWw0IiwicGlkIjoiMTIzIiwiZXhwIjoxNjQ5Njc4MzA1LCJpYXQiOjE2NDk2NzQ3MDUsImp0aSI6IlNUVUIifQ.NttjqH45-oM-VFimail6K-1T7MnDBNo-m0bFcktfqnQ"