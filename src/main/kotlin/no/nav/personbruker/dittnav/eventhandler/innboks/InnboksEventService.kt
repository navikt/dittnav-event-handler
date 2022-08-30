package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.brukernotifikasjon.schemas.builders.util.ValidationUtil.validateNonNullFieldMaxLength
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import java.sql.Connection

class InnboksEventService(private val database: Database) {

    suspend fun getActiveEventsForFodselsnummer(fodselsnummer: String): List<InnboksDTO> {
        return getEvents {
            getAktivInnboksForFodselsnummer(fodselsnummer)
        }.map { innboks -> innboks.toDTO() }
    }

    suspend fun getInactiveEventsForFodselsnummer(fodselsnummer: String): List<InnboksDTO> {
        return getEvents {
            getInaktivInnboksForFodselsnummer(fodselsnummer)
        }.map { innboks -> innboks.toDTO() }
    }

    suspend fun getAllEventsForFodselsnummer(fodselsnummer: String): List<InnboksDTO> {
        return getEvents {
            getAllInnboksForFodselsnummer(fodselsnummer)
        }.map { innboks -> innboks.toDTO() }
    }

    suspend fun getAllGroupedEventsFromCacheForUser(bruker: TokenXUser, grupperingsid: String?, appnavn: String?): List<InnboksDTO> {
        val grupperingsId = validateNonNullFieldMaxLength(grupperingsid, "grupperingsid", 100)
        val app = validateNonNullFieldMaxLength(appnavn, "appnavn", 100)
        return getEvents { getAllGroupedInnboksEventsByIds(bruker.ident, grupperingsId, app) }
            .map { innboks -> innboks.toDTO() }
    }

    suspend fun getAllGroupedEventsByProducerFromCache(): List<EventCountForProducer> {
        return database.queryWithExceptionTranslation { getAllGroupedInnboksEventsByProducer() }
    }

    private suspend fun getEvents(operationToExecute: Connection.() -> List<Innboks>): List<Innboks> {
        val events = database.queryWithExceptionTranslation {
            operationToExecute()
        }
        return events
    }
}
