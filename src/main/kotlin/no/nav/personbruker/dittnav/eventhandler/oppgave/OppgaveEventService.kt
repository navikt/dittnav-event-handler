package no.nav.personbruker.dittnav.eventhandler.oppgave

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

    suspend fun produceOppgaveEventsForAllOppgaveEventsInCach(): List<Oppgave> {
        val allOppgaveEvents = getEvents { getAllOppgaveEvents() }
        if (allOppgaveEvents.isNotEmpty()) {
            oppgaveProducer.produceAllOppgaveEventsFromList(allOppgaveEvents)
        }
        return allOppgaveEvents
    }

    suspend fun produceDoneEventsFromAllInactiveOppgaveEvents(): List<Oppgave> {
        var allInactiveOppgaveEvents = getEvents { getAllInactiveOppgaveEvents() }
        if (allInactiveOppgaveEvents.isNotEmpty()) {
            oppgaveProducer.produceDoneEventFromInactiveOppgaveEvents(allInactiveOppgaveEvents)
        }
        return allInactiveOppgaveEvents
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
