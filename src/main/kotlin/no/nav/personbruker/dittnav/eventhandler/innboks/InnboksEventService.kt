package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import org.slf4j.LoggerFactory
import java.sql.Connection

class InnboksEventService(private val database: Database) {

    private val log = LoggerFactory.getLogger(InnboksEventService::class.java)

    suspend fun getActiveCachedEventsForUser(bruker: InnloggetBruker): List<Innboks> {
        return getEvents {  getAktivInnboksForInnloggetBruker(bruker) }
    }

    suspend fun getInctiveCachedEventsForUser(bruker: InnloggetBruker): List<Innboks> {
        return getEvents { getInaktivInnboksForInnloggetBruker(bruker) }
    }

    suspend fun getAllCachedEventsForUser(bruker: InnloggetBruker): List<Innboks> {
        return getEvents { getAllInnboksForInnloggetBruker(bruker) }
    }

    private suspend fun getEvents(operationToExecute: Connection.() -> List<Innboks>): List<Innboks> {
        val events = database.queryWithExceptionTranslation {
            operationToExecute()
        }
        if(produsentIsEmpty(events)) {
            log.warn("Returnerer innboks-eventer med tom produsent til frontend. Kanskje er ikke systembrukeren lagt inn i systembruker-tabellen?")
        }
        return events
    }

    private fun produsentIsEmpty(events: List<Innboks>): Boolean {
        return events.any { innboks -> innboks.produsent.isNullOrEmpty() }
    }
}
