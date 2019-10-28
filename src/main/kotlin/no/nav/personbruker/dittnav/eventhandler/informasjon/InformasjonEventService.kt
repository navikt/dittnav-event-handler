package no.nav.personbruker.dittnav.eventhandler.informasjon

import Informasjon
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.Database

class InformasjonEventService(
        private val database: Database
) {

    fun getEventsFromCacheForUser(aktorId: String): List<Informasjon> {
        var fetchedRows = emptyList<Informasjon>()

        runBlocking {
            fetchedRows = database.dbQuery { getInformasjonByAktorId(aktorId) }
        }

        return fetchedRows
    }

    fun getAllEventsFromCacheForUser(aktorId: String): List<Informasjon> {
        var fetchedRows = emptyList<Informasjon>()

        runBlocking {
            fetchedRows = database.dbQuery { getAllInformasjonByAktorId(aktorId) }
        }

        return fetchedRows
    }

}