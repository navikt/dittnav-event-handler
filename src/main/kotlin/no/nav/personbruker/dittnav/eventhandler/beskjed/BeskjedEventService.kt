package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.common.database.Database

class BeskjedEventService(
        private val database: Database
) {

    suspend fun getEventsFromCacheForUser(fodselsnummer: String): List<Beskjed> {
        return database.dbQuery { getActiveBeskjedByFodselsnummer(fodselsnummer) }
    }

    suspend fun getAllEventsFromCacheForUser(fodselsnummer: String): List<Beskjed> {
        return database.dbQuery { getAllBeskjedByFodselsnummer(fodselsnummer) }
    }

}
