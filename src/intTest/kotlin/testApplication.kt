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

fun mockEventHandlerApi(
    healthService: HealthService = mockk(),
    beskjedEventService: BeskjedEventService = mockk(),
    oppgaveEventService: OppgaveEventService = mockk(),
    innboksEventService: InnboksEventService = mockk(),
    doneEventService: DoneEventService = mockk(),
    statusoppdateringEventService: StatusoppdateringEventService = mockk(),
    eventRepository: EventRepository = mockk(),
    eventStatisticsService: EventStatisticsService = mockk(),
    database: Database = mockk(),
    doneProducer: DoneProducer = mockk(),
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
            doneProducer = doneProducer
        )
    }
}