package no.nav.personbruker.dittnav.eventhandler.oppgave

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.PostgresDatabase

class OppgaveEventService(
        private val database: PostgresDatabase
) {

    fun getEventsFromCacheForUser(aktorId: String): List<Oppgave> {
        var fetchedRows = emptyList<Oppgave>()

        runBlocking {
            fetchedRows = database.dbQuery { getOppgaveByAktorId(aktorId) }
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
