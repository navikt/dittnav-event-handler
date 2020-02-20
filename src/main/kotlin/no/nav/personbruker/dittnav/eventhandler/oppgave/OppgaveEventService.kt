package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.Database

class OppgaveEventService(
        private val database: Database
) {

    suspend fun getEventsFromCacheForUser(bruker: InnloggetBruker): List<Oppgave> {
        return database.dbQuery { getActiveOppgaveByUser(bruker) }
    }

    suspend fun getAllEventsFromCacheForUser(bruker: InnloggetBruker): List<Oppgave> {
        return database.dbQuery { getAllOppgaveByUser(bruker) }
    }

}
