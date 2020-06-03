package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.time.Instant
import java.time.ZoneId

class BeskjedEventService(private val database: Database) {

    private val log = LoggerFactory.getLogger(BeskjedEventService::class.java)

    suspend fun getActiveCachedEventsForUser(bruker: InnloggetBruker): List<Beskjed> {
        return getEvents { getAktivBeskjedForInnloggetBruker(bruker) }
                .filter { beskjed -> !beskjed.isExpired() }
    }

    suspend fun getInactiveCachedEventsForUser(bruker: InnloggetBruker): List<Beskjed> {
        val all = getAllEventsFromCacheForUser(bruker)
        val inactive = all.filter { beskjed -> !beskjed.aktiv }
        val expired = all.filter { beskjed -> beskjed.isExpired() }
        return inactive + expired
    }

    suspend fun getAllEventsFromCacheForUser(bruker: InnloggetBruker): List<Beskjed> {
        return getEvents { getAllBeskjedForInnloggetBruker(bruker) }
    }

    suspend fun getAllBeskjedEventsInCach(): List<Beskjed> {
        return getEvents { getAllBeskjedEvents() }
    }

    suspend fun getAllInactiveBeskjedEventsInCach(): List<Beskjed> {
        return getEvents { getAllInactiveBeskjed() }
    }

    private fun Beskjed.isExpired() : Boolean = synligFremTil?.isBefore(Instant.now().atZone(ZoneId.of("Europe/Oslo")))?: false

    private suspend fun getEvents(operationToExecute: Connection.() -> List<Beskjed>): List<Beskjed> {
        val events = database.queryWithExceptionTranslation {
            operationToExecute()
        }
        if(produsentIsEmpty(events)) {
            log.warn("Returnerer beskjed-eventer med tom produsent til frontend. Kanskje er ikke systembrukeren lagt inn i systembruker-tabellen?")
        }
        return events
    }

    private fun produsentIsEmpty(events: List<Beskjed>): Boolean {
        return events.any { beskjed -> beskjed.produsent.isNullOrEmpty() }
    }
}
