package no.nav.personbruker.dittnav.eventhandler.oppgave

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.Database

class OppgaveEventService(
        private val database: Database
) {

    fun getEventsFromCacheForUser(aktorId: String): List<Oppgave> {
        var fetchedRows = emptyList<Oppgave>()

        runBlocking {
            fetchedRows = database.dbQuery { getActiveOppgaveByAktorId(aktorId) }
        }

        return fetchedRows
    }

    fun getAllEventsFromCacheForUser(aktorId: String): List<Oppgave> {
        var fetchedRows = emptyList<Oppgave>()

        runBlocking {
            fetchedRows = database.dbQuery { getAllOppgaveByAktorId(aktorId) }
        }

        return fetchedRows
    }

}
