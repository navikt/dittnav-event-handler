package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import org.slf4j.LoggerFactory
import java.sql.Connection

class StatusoppdateringEventService(private val database: Database) {

    private val log = LoggerFactory.getLogger(StatusoppdateringEventService::class.java)

    suspend fun getAllEventsFromCacheForUser(bruker: InnloggetBruker): List<Statusoppdatering> {
        return getEvents { getAllStatusoppdateringForInnloggetBruker(bruker) }
    }

    private suspend fun getEvents(operationToExecute: Connection.() -> List<Statusoppdatering>): List<Statusoppdatering> {
        val events = database.queryWithExceptionTranslation {
            operationToExecute()
        }
        if (produsentIsEmpty(events)) {
            log.warn("Returnerer statusoppdatering-eventer med tom produsent til frontend. Kanskje er ikke systembrukeren lagt inn i systembruker-tabellen?")
        }
        return events
    }

    private fun produsentIsEmpty(events: List<Statusoppdatering>): Boolean {
        return events.any { statusoppdatering -> statusoppdatering.produsent.isNullOrEmpty() }
    }
}
