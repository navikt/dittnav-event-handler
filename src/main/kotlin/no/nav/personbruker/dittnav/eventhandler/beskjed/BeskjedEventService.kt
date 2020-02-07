package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import java.time.Instant
import java.time.ZoneId

class BeskjedEventService(
        private val database: Database
) {

    suspend fun getEventsFromCacheForUser(fodselsnummer: String): List<Beskjed> {
        return database.dbQuery {
            getActiveBeskjedByFodselsnummer(fodselsnummer)
        }.filter { beskjed -> !beskjed.isExpired() }
    }

    suspend fun getAllEventsFromCacheForUser(fodselsnummer: String): List<Beskjed> {
        return database.dbQuery { getAllBeskjedByFodselsnummer(fodselsnummer) }
    }

    fun Beskjed.isExpired() : Boolean = synligFremTil?.isBefore(Instant.now().atZone(ZoneId.of("Europe/Oslo")))?: false
}
