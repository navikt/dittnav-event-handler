package no.nav.personbruker.dittnav.eventhandler.service

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.config.Environment
import no.nav.personbruker.dittnav.eventhandler.database.Database
import no.nav.personbruker.dittnav.eventhandler.database.entity.Event
import no.nav.personbruker.dittnav.eventhandler.database.entity.getOppgaveByAktorid

class OppgaveEventService(
        val database: Database = Database(Environment())
) {

    fun getEventsFromCacheForUser(aktorid: String): List<Event> {
        var fetchedRows = emptyList<Event>()

        runBlocking {
            fetchedRows = database.dbQuery {getOppgaveByAktorid(aktorid)}
        }

        return fetchedRows
    }

}
