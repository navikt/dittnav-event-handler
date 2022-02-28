package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

import no.nav.brukernotifikasjon.schemas.builders.util.ValidationUtil.validateNonNullFieldMaxLength
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.statistics.EventCountForProducer
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import java.sql.Connection

class StatusoppdateringEventService(private val database: Database) {

    suspend fun getAllGroupedEventsFromCacheForUser(bruker: TokenXUser, grupperingsid: String?, appnavn: String?): List<StatusoppdateringDTO> {
        val grupperingsId = validateNonNullFieldMaxLength(grupperingsid, "grupperingsid", 100)
        val app = validateNonNullFieldMaxLength(appnavn, "appnavn", 100)
        return getEvents { getAllGroupedStatusoppdateringEventsByIds(bruker, grupperingsId, app) }
            .map { statusoppdatering -> statusoppdatering.toDTO() }
    }

    suspend fun getAllGroupedEventsBySystemuserFromCache(): Map<String, Int> {
        return database.queryWithExceptionTranslation { getAllGroupedStatusoppdateringEventsBySystemuser() }
    }

    suspend fun getAllGroupedEventsByProducerFromCache(): List<EventCountForProducer> {
        return database.queryWithExceptionTranslation { getAllGroupedStatusoppdateringEventsByProducer() }
    }


    private suspend fun getEvents(operationToExecute: Connection.() -> List<Statusoppdatering>): List<Statusoppdatering> {
        val events = database.queryWithExceptionTranslation {
            operationToExecute()
        }
        return events
    }
}
