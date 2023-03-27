package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import java.sql.Connection

class OppgaveEventService(private val database: Database) {

    suspend fun getActiveEventsForFodselsnummer(fodselsnummer: String): List<Oppgave> {
        return getEvents {
            getAktivOppgaveForFodselsnummer(fodselsnummer)
        }
    }

    suspend fun getInactiveEventsForFodselsnummer(fodselsnummer: String): List<Oppgave> {
        return getEvents {
            getInaktivOppgaveForFodselsnummer(fodselsnummer)
        }
    }

    suspend fun getAllEventsForFodselsnummer(fodselsnummer: String): List<Oppgave> {
        return getEvents {
            getAllOppgaveForFodselsnummer(fodselsnummer)
        }
    }

    suspend fun getAllGroupedEventsByProducerFromCache(): List<EventCountForProducer> {
        return database.queryWithExceptionTranslation { getAllGroupedOppgaveEventsByProducer() }
    }

    private suspend fun getEvents(operationToExecute: Connection.() -> List<Oppgave>): List<Oppgave> {
        val events = database.queryWithExceptionTranslation {
            operationToExecute()
        }
        return events
    }
}
