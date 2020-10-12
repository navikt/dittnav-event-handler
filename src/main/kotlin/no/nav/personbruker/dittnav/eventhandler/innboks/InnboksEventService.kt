package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import org.slf4j.LoggerFactory
import java.sql.Connection

class InnboksEventService(private val database: Database) {

    private val log = LoggerFactory.getLogger(InnboksEventService::class.java)

    suspend fun getActiveCachedEventsForUser(bruker: InnloggetBruker): List<Innboks> {
        return getEvents { getAktivInnboksForInnloggetBruker(bruker) }
    }

    suspend fun getInctiveCachedEventsForUser(bruker: InnloggetBruker): List<Innboks> {
        return getEvents { getInaktivInnboksForInnloggetBruker(bruker) }
    }

    suspend fun getAllCachedEventsForUser(bruker: InnloggetBruker): List<Innboks> {
        return getEvents { getAllInnboksForInnloggetBruker(bruker) }
    }

    suspend fun getAllGroupedEventsFromCacheForUser(bruker: InnloggetBruker, grupperingsid: String, produsent: String): List<Innboks> {
        return getEvents { getAllGroupedInnboksEventsByIds(bruker, grupperingsid, produsent) }
    }

    private suspend fun getEvents(operationToExecute: Connection.() -> List<Innboks>): List<Innboks> {
        val events = database.queryWithExceptionTranslation {
            operationToExecute()
        }
        val eventsWithEmptyProdusent = events.filter { innboks -> innboks.produsent.isNullOrEmpty() }

        if (eventsWithEmptyProdusent.isNotEmpty()) {
            logEventsWithEmptyProdusent(eventsWithEmptyProdusent)
        }
        return events
    }

    fun logEventsWithEmptyProdusent(events: List<Innboks>) {
        events.forEach { innboks ->
            log.warn("Returnerer innboks-eventer med tom produsent til frontend. Kanskje er ikke systembrukeren lagt inn i systembruker-tabellen? ${innboks.toString()}")
        }
    }
}