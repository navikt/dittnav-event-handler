package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import no.nav.brukernotifikasjon.schemas.builders.util.ValidationUtil.validateNonNullFieldMaxLength
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.time.Instant
import java.time.ZoneId

class BeskjedEventService(private val database: Database) {

    private val log = LoggerFactory.getLogger(BeskjedEventService::class.java)

    suspend fun getActiveCachedEventsForUser(bruker: TokenXUser): List<BeskjedDTO> {
        return getEvents { getAktivBeskjedForInnloggetBruker(bruker) }
                .filter { beskjed -> !beskjed.isExpired() }
                .map { beskjed -> beskjed.toDTO()}
    }

    suspend fun getInactiveCachedEventsForUser(bruker: TokenXUser): List<BeskjedDTO> {
        val all = getAllEventsFromCacheForUser(bruker)
        val inactive = all.filter { beskjed -> !beskjed.aktiv }.map { beskjed -> beskjed.toDTO() }
        val expired = all.filter { beskjed -> beskjed.isExpired() }.map { beskjed -> beskjed.toDTO() }
        return inactive + expired
    }

    suspend fun getAllCachedEventsForUser(bruker: TokenXUser): List<BeskjedDTO> {
        val all = getAllEventsFromCacheForUser(bruker)
        return all.map { beskjed -> beskjed.toDTO() }
    }

    suspend fun getAllGroupedEventsFromCacheForUser(bruker: TokenXUser, grupperingsid: String?, producer: String?): List<BeskjedDTO> {
        val grupperingsId = validateNonNullFieldMaxLength(grupperingsid, "grupperingsid", 100)
        val produsent = validateNonNullFieldMaxLength(producer, "produsent", 100)
        return getEvents { getAllGroupedBeskjedEventsByIds(bruker, grupperingsId, produsent) }
                .map { beskjed -> beskjed.toDTO() }
    }

    suspend fun getAllGroupedEventsBySystemuserFromCache(): Map<String, Int> {
        return database.queryWithExceptionTranslation { getAllGroupedBeskjedEventsBySystemuser() }
    }

    private suspend fun getAllEventsFromCacheForUser(bruker: TokenXUser): List<Beskjed> {
        return getEvents { getAllBeskjedForInnloggetBruker(bruker) }
    }

    private fun Beskjed.isExpired(): Boolean = synligFremTil?.isBefore(Instant.now().atZone(ZoneId.of("Europe/Oslo"))) ?: false

    private suspend fun getEvents(operationToExecute: Connection.() -> List<Beskjed>): List<Beskjed> {
        val events = database.queryWithExceptionTranslation {
            operationToExecute()
        }
        val eventsWithEmptyProdusent = events.filter { beskjed -> beskjed.produsent.isNullOrEmpty() }

        if (eventsWithEmptyProdusent.isNotEmpty()) {
            logEventsWithEmptyProdusent(eventsWithEmptyProdusent)
        }
        return events
    }

    private fun logEventsWithEmptyProdusent(events: List<Beskjed>) {
        events.forEach { beskjed ->
            log.warn("Returnerer beskjed-eventer med tom produsent til frontend. Kanskje er ikke systembrukeren lagt inn i systembruker-tabellen? $beskjed")
        }
    }
}
