package no.nav.personbruker.dittnav.eventhandler.informasjon

import Informasjon
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.Database

class InformasjonEventService(
        private val database: Database
) {

    fun getEventsFromCacheForUser(aktorId: String): List<Informasjon> {
        return runBlocking {
            database.dbQuery { getActiveInformasjonByAktorId(aktorId) }
        }
    }

    fun getAllEventsFromCacheForUser(aktorId: String): List<Informasjon> {
        return runBlocking {
            database.dbQuery { getAllInformasjonByAktorId(aktorId) }
        }
    }

}
