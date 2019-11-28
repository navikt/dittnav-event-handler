package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.Database

class BeskjedEventService(
        private val database: Database
) {

    fun getEventsFromCacheForUser(aktorId: String): List<Beskjed> {
        return runBlocking {
            database.dbQuery { getActiveBeskjedByAktorId(aktorId) }
        }
    }

    fun getAllEventsFromCacheForUser(aktorId: String): List<Beskjed> {
        return runBlocking {
            database.dbQuery { getAllBeskjedByAktorId(aktorId) }
        }
    }

}
