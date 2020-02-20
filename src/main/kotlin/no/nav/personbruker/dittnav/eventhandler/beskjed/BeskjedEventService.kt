package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import java.time.Instant
import java.time.ZoneId

class BeskjedEventService(
        private val database: Database
) {

    suspend fun getEventsFromCacheForUser(bruker: InnloggetBruker): List<Beskjed> {
        return database.dbQuery {
            getActiveBeskjedByFodselsnummer(bruker)
        }.filter { beskjed -> !beskjed.isExpired() }
    }

    suspend fun getAllEventsFromCacheForUser(bruker: InnloggetBruker): List<Beskjed> {
        return database.dbQuery { getAllBeskjedByFodselsnummer(bruker) }
    }

    fun Beskjed.isExpired() : Boolean = synligFremTil?.isBefore(Instant.now().atZone(ZoneId.of("Europe/Oslo")))?: false
}
