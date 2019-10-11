package no.nav.personbruker.dittnav.eventhandler.informasjon

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.Brukernotifikasjon
import no.nav.personbruker.dittnav.eventhandler.common.database.Database

class InformasjonEventService(
        private val database: Database
) {

    fun getEventsFromCacheForUser(aktorId: String): List<Brukernotifikasjon> {
        var fetchedRows = emptyList<Brukernotifikasjon>()

        runBlocking {
            fetchedRows = database.dbQuery { getInformasjonByAktorId(aktorId) }
        }

        return fetchedRows
    }

}
