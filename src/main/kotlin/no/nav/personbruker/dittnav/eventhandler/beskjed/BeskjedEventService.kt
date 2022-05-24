package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import no.nav.brukernotifikasjon.schemas.builders.util.ValidationUtil.validateNonNullFieldMaxLength
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.daysAgo
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import java.sql.Connection

class BeskjedEventService(private val database: Database,
                          private val filterOldEvents: Boolean,
                          private val filterThresholdDays: Int) {

    suspend fun getActiveEventsForFodselsnummer(fodselsnummer: String): List<BeskjedDTO> {
        return getEvents {
            if (filterOldEvents) {
                getRecentAktivBeskjedForFodselsnummer(fodselsnummer, daysAgo(filterThresholdDays))
            } else {
                getAktivBeskjedForFodselsnummer(fodselsnummer)
            }
        }.map { beskjed -> beskjed.toDTO() }
    }

    suspend fun getInactiveEventsForFodselsnummer(fodselsnummer: String): List<BeskjedDTO> {
        return getEvents {
            if (filterOldEvents) {
                getRecentInaktivBeskjedForFodselsnummer(fodselsnummer, daysAgo(filterThresholdDays))
            } else {
                getInaktivBeskjedForFodselsnummer(fodselsnummer)
            }
        }.map { beskjed -> beskjed.toDTO() }
    }

    suspend fun getAllEventsForFodselsnummer(fodselsnummer: String): List<BeskjedDTO> {
        return getEvents {
            if (filterOldEvents) {
                getAllRecentBeskjedForFodselsnummer(fodselsnummer, daysAgo(filterThresholdDays))
            } else {
                getAllBeskjedForFodselsnummer(fodselsnummer)
            }
        }.map { beskjed -> beskjed.toDTO() }
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
