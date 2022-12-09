package no.nav.personbruker.dittnav.eventhandler.config

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedEventService
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.health.HealthService
import no.nav.personbruker.dittnav.eventhandler.varsel.VarselRepository
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksEventService
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveEventService
import no.nav.personbruker.dittnav.eventhandler.statistics.EventStatisticsService

fun main() {

    val environment = Environment()

    val database: Database = PostgresDatabase(environment)

    val beskjedEventService = BeskjedEventService(database)
    val oppgaveEventService = OppgaveEventService(database)
    val innboksEventService = InnboksEventService(database)
    val eventStatisticsService = EventStatisticsService(database)
    val varselRepository = VarselRepository(database)

    val healthService = HealthService(database)

    embeddedServer(Netty, port = 8080) {
        eventHandlerApi(
            healthService = healthService,
            beskjedEventService = beskjedEventService,
            oppgaveEventService = oppgaveEventService,
            innboksEventService = innboksEventService,
            varselRepository = varselRepository,
            eventStatisticsService = eventStatisticsService,
            database = database
        )
    }.start(wait = true)
}
