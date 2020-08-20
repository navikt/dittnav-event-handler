package no.nav.personbruker.dittnav.eventhandler.statusOppdatering

import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import org.slf4j.LoggerFactory
import java.sql.Connection

class StatusOppdateringEventService(private val database: Database) {

    private val log = LoggerFactory.getLogger(StatusOppdateringEventService::class.java)

    suspend fun getAllEventsFromCacheForUser(bruker: InnloggetBruker): List<StatusOppdatering> {
        return getEvents { getAllStatusOppdateringForInnloggetBruker(bruker) }
    }

    private suspend fun getEvents(operationToExecute: Connection.() -> List<StatusOppdatering>): List<StatusOppdatering> {
        val events = database.queryWithExceptionTranslation {
            operationToExecute()
        }
        if (produsentIsEmpty(events)) {
            log.warn("Returnerer statusOppdatering-eventer med tom produsent til frontend. Kanskje er ikke systembrukeren lagt inn i systembruker-tabellen?")
        }
        return events
    }

    private fun produsentIsEmpty(events: List<StatusOppdatering>): Boolean {
        return events.any { statusOppdatering -> statusOppdatering.produsent.isNullOrEmpty() }
    }
}
