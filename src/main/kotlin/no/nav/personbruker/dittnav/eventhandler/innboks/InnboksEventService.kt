package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.brukernotifikasjon.schemas.builders.util.ValidationUtil.validateNonNullFieldMaxLength
import no.nav.personbruker.dittnav.eventhandler.beskjed.getAllGroupedBeskjedEventsByProducer
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.statistics.EventCountForProducer
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import org.slf4j.LoggerFactory
import java.sql.Connection

class InnboksEventService(private val database: Database) {

    private val log = LoggerFactory.getLogger(InnboksEventService::class.java)

    suspend fun getActiveCachedEventsForUser(bruker: TokenXUser): List<InnboksDTO> {
        return getEvents { getAktivInnboksForInnloggetBruker(bruker) }
            .map { innboks -> innboks.toDTO()}
    }

    suspend fun getInctiveCachedEventsForUser(bruker: TokenXUser): List<InnboksDTO> {
        return getEvents { getInaktivInnboksForInnloggetBruker(bruker) }
            .map { innboks -> innboks.toDTO()}
    }

    suspend fun getAllCachedEventsForUser(bruker: TokenXUser): List<InnboksDTO> {
        return getEvents { getAllInnboksForInnloggetBruker(bruker) }
            .map { innboks -> innboks.toDTO()}
    }

    suspend fun getAllGroupedEventsFromCacheForUser(bruker: TokenXUser, grupperingsid: String?, producer: String?): List<InnboksDTO> {
        val grupperingsId = validateNonNullFieldMaxLength(grupperingsid, "grupperingsid", 100)
        val produsent = validateNonNullFieldMaxLength(producer, "produsent", 100)
        return getEvents { getAllGroupedInnboksEventsByIds(bruker, grupperingsId, produsent) }
            .map { innboks -> innboks.toDTO()}
    }

    suspend fun getAllGroupedEventsBySystemuserFromCache(): Map<String, Int> {
        return database.queryWithExceptionTranslation { getAllGroupedInnboksEventsBySystemuser() }
    }

    suspend fun getAllGroupedEventsByProducerFromCache(): List<EventCountForProducer> {
        return database.queryWithExceptionTranslation { getAllGroupedInnboksEventsByProducer() }
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
