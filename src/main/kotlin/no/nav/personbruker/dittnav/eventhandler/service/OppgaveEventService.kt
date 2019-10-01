package no.nav.personbruker.dittnav.eventhandler.service

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.config.Environment
import no.nav.personbruker.dittnav.eventhandler.database.Database
import no.nav.personbruker.dittnav.eventhandler.database.entity.Brukernotifikasjon
import no.nav.personbruker.dittnav.eventhandler.database.entity.oppgave.getOppgaveByAktorId

class OppgaveEventService(
        val database: Database = Database(Environment())
) {

    fun getEventsFromCacheForUser(aktorId: String): List<Brukernotifikasjon> {
        var fetchedRows = emptyList<Brukernotifikasjon>()

        runBlocking {
            fetchedRows = database.dbQuery {getOppgaveByAktorId(aktorId)}
        }

        return fetchedRows
    }

}
