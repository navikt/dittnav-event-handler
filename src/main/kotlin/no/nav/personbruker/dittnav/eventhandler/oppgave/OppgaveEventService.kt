package no.nav.personbruker.dittnav.eventhandler.oppgave

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import org.slf4j.LoggerFactory
import java.sql.Connection

class OppgaveEventService(
        private val database: Database,
        private val oppgaveProducer: OppgaveProducer
) {

    private val log = LoggerFactory.getLogger(OppgaveEventService::class.java)

    suspend fun getActiveCachedEventsForUser(bruker: InnloggetBruker): List<Oppgave> {
        return getEvents { getAktivOppgaveForInnloggetBruker(bruker) }
    }

    suspend fun getInactiveCachedEventsForUser(bruker: InnloggetBruker): List<Oppgave> {
        return getEvents { getInaktivOppgaveForInnloggetBruker(bruker) }
    }

    suspend fun getAllCachedEventsForUser(bruker: InnloggetBruker): List<Oppgave> {
        return getEvents { getAllOppgaveForInnloggetBruker(bruker) }
    }

    suspend fun produceOppgaveEventsForAllOppgaveEventsInCache(): Int {
        val allOppgaveEvents = getEvents { getAllOppgaveEvents() }
        var numberOfProcessedEvents = 0
        var batchNumber = 0
        if (allOppgaveEvents.isNotEmpty()) {
            allOppgaveEvents.chunked(10000) { allOppgaveChunk ->
                    numberOfProcessedEvents += oppgaveProducer.produceAllOppgaveEventsFromList(++batchNumber, allOppgaveChunk)
                    log.info("Prosesserte oppgave-backup, batch nr $batchNumber")
            }
        }
        return numberOfProcessedEvents
    }

    suspend fun produceDoneEventsFromAllInactiveOppgaveEvents(): Int {
        var allInactiveOppgaveEvents = getEvents { getAllInactiveOppgaveEvents() }
        var numberOfProcessedEvents = 0
        var batchNumber = 0
        if (allInactiveOppgaveEvents.isNotEmpty()) {
            allInactiveOppgaveEvents.chunked(10000) { allInactiveOppgaveChunk ->
                    numberOfProcessedEvents = oppgaveProducer.produceDoneEventFromInactiveOppgaveEvents(++batchNumber, allInactiveOppgaveEvents)
                    log.info("Prosesserte oppgave-done-backup, batch nr $batchNumber")
            }
        }
        return numberOfProcessedEvents
    }

    private suspend fun getEvents(operationToExecute: Connection.() -> List<Oppgave>): List<Oppgave> {
        val events = database.queryWithExceptionTranslation {
            operationToExecute()
        }
        if (produsentIsEmpty(events)) {
            log.warn("Returnerer oppgave-eventer med tom produsent til frontend. Kanskje er ikke systembrukeren lagt inn i systembruker-tabellen?")
        }
        return events
    }

    private fun produsentIsEmpty(events: List<Oppgave>): Boolean {
        return events.any { oppgave -> oppgave.produsent.isNullOrEmpty() }
    }
}
