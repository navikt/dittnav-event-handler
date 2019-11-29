package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.personbruker.dittnav.eventhandler.common.database.Database

class OppgaveEventService(
        private val database: Database
) {

    suspend fun getEventsFromCacheForUser(fodselsnummer: String): List<Oppgave> {
        return database.dbQuery { getActiveOppgaveByFodselsnummer(fodselsnummer) }
    }

    suspend fun getAllEventsFromCacheForUser(fodselsnummer: String): List<Oppgave> {
        return database.dbQuery { getAllOppgaveByFodselsnummer(fodselsnummer) }
    }

}
