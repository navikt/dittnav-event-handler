package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import no.nav.brukernotifikasjon.schemas.builders.util.ValidationUtil.validateNonNullFieldMaxLength
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.modia.User
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import java.sql.Connection

class BeskjedEventService(private val database: Database) {

    suspend fun getActiveCachedEventsForUser(bruker: TokenXUser): List<BeskjedDTO> {
        return getEvents { getAktivBeskjedForInnloggetBruker(bruker.ident) }
                .map { beskjed -> beskjed.toDTO()}
    }

    suspend fun getActiveCachedEventsForUser(bruker: User): List<BeskjedDTO> {
        return getEvents { getAktivBeskjedForInnloggetBruker(bruker.fodselsnummer) }
            .map { beskjed -> beskjed.toDTO()}
    }

    suspend fun getInactiveCachedEventsForUser(bruker: TokenXUser): List<BeskjedDTO> {
        return getEvents { getInaktivBeskjedForInnloggetBruker(bruker.ident) }
            .map { beskjed -> beskjed.toDTO()}
    }

    suspend fun getInactiveCachedEventsForUser(bruker: User): List<BeskjedDTO> {
        return getEvents { getInaktivBeskjedForInnloggetBruker(bruker.fodselsnummer) }
            .map { beskjed -> beskjed.toDTO()}
    }

    suspend fun getAllCachedEventsForUser(bruker: TokenXUser): List<BeskjedDTO> {
        return getEvents { getAllBeskjedForInnloggetBruker(bruker.ident) }
            .map { beskjed -> beskjed.toDTO() }
    }

    suspend fun getAllCachedEventsForUser(bruker: User): List<BeskjedDTO> {
        return getEvents { getAllBeskjedForInnloggetBruker(bruker.fodselsnummer) }
            .map { beskjed -> beskjed.toDTO() }
    }

    suspend fun getAllGroupedEventsFromCacheForUser(bruker: TokenXUser, grupperingsid: String?, appnavn: String?): List<BeskjedDTO> {
        val grupperingsId = validateNonNullFieldMaxLength(grupperingsid, "grupperingsid", 100)
        val app = validateNonNullFieldMaxLength(appnavn, "appnavn", 100)
        return getEvents { getAllGroupedBeskjedEventsByIds(bruker.ident, grupperingsId, app) }
                .map { beskjed -> beskjed.toDTO() }
    }

    suspend fun getAllGroupedEventsByProducerFromCache(): List<EventCountForProducer> {
        return database.queryWithExceptionTranslation { getAllGroupedBeskjedEventsByProducer() }
    }

    private suspend fun getEvents(operationToExecute: Connection.() -> List<Beskjed>): List<Beskjed> {
        val events = database.queryWithExceptionTranslation {
            operationToExecute()
        }
        return events
    }
}
