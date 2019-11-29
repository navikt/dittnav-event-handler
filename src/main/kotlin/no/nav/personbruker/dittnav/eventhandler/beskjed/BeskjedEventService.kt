package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.Database

class BeskjedEventService(
        private val database: Database
) {

    fun getEventsFromCacheForUser(fodselsnummer: String): List<Beskjed> {
        return runBlocking {
            database.dbQuery { getActiveBeskjedByFodselsnummer(fodselsnummer) }
        }
    }

    fun getAllEventsFromCacheForUser(fodselsnummer: String): List<Beskjed> {
        return runBlocking {
            database.dbQuery { getAllBeskjedByFodselsnummer(fodselsnummer) }
        }
    }

}
