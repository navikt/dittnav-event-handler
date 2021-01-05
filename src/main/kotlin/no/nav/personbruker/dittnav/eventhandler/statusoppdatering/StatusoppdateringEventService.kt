package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

import no.nav.brukernotifikasjon.schemas.builders.util.ValidationUtil.validateNonNullFieldMaxLength
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import org.slf4j.LoggerFactory
import java.sql.Connection

class StatusoppdateringEventService(private val database: Database) {

    private val log = LoggerFactory.getLogger(StatusoppdateringEventService::class.java)

    suspend fun getAllGroupedEventsFromCacheForUser(bruker: InnloggetBruker, grupperingsid: String?, producer: String?): List<Statusoppdatering> {
        val grupperingsId = validateNonNullFieldMaxLength(grupperingsid, "grupperingsid", 100)
        val produsent = validateNonNullFieldMaxLength(producer, "produsent", 100)
        return getEvents { getAllGroupedStatusoppdateringEventsByIds(bruker, grupperingsId, produsent) }
    }

    private suspend fun getEvents(operationToExecute: Connection.() -> List<Statusoppdatering>): List<Statusoppdatering> {
        val events = database.queryWithExceptionTranslation {
            operationToExecute()
        }
        val eventsWithEmptyProdusent = events.filter { statusoppdatering -> statusoppdatering.produsent.isNullOrEmpty() }

        if (eventsWithEmptyProdusent.isNotEmpty()) {
            logEventsWithEmptyProdusent(eventsWithEmptyProdusent)
        }
        return events
    }

    fun logEventsWithEmptyProdusent(events: List<Statusoppdatering>) {
        events.forEach { statusoppdatering ->
            log.warn("Returnerer statusoppdatering-eventer med tom produsent til frontend. Kanskje er ikke systembrukeren lagt inn i systembruker-tabellen? ${statusoppdatering.toString()}")
        }
    }
}
