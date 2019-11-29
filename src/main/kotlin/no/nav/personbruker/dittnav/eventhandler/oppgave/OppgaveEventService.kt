package no.nav.personbruker.dittnav.eventhandler.oppgave

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.Database

class OppgaveEventService(
        private val database: Database
) {

    fun getEventsFromCacheForUser(fodselsnummer: String): List<Oppgave> {
        var fetchedRows = emptyList<Oppgave>()

        runBlocking {
            fetchedRows = database.dbQuery { getActiveOppgaveByFodselsnummer(fodselsnummer) }
        }

        return fetchedRows
    }

    fun getAllEventsFromCacheForUser(fodselsnummer: String): List<Oppgave> {
        var fetchedRows = emptyList<Oppgave>()

        runBlocking {
            fetchedRows = database.dbQuery { getAllOppgaveByFodselsnummer(fodselsnummer) }
        }

        return fetchedRows
    }

}
