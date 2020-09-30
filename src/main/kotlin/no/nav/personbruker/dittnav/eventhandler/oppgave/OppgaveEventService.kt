package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import org.slf4j.LoggerFactory
import java.sql.Connection

class OppgaveEventService(private val database: Database) {

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

    suspend fun getAllOppgaveEventsInCach(): List<Oppgave> {
        return getEvents { getAllOppgaveEvents() }
    }

    suspend fun getAllInactiveOppgaveEventsInCach(): List<Oppgave> {
        return getEvents { getAllInactiveOppgaveEvents() }
    }

    private suspend fun getEvents(operationToExecute: Connection.() -> List<Oppgave>): List<Oppgave> {
        val events = database.queryWithExceptionTranslation {
            operationToExecute()
        }
        val eventsWithEmptyProdusent = events.filter { oppgave -> oppgave.produsent.isNullOrBlank() }

        if (eventsWithEmptyProdusent.isNotEmpty()) {
            logEventsWithEmptyProdusent(eventsWithEmptyProdusent)
        }
        return events
    }

    fun logEventsWithEmptyProdusent(events: List<Oppgave>) {
        events.forEach { oppgave ->
            log.warn("Returnerer oppgave-eventer med tom produsent til frontend. Kanskje er ikke systembrukeren lagt inn i systembruker-tabellen? ${oppgave.toString()}")
        }
    }
}