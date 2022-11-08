package no.nav.personbruker.dittnav.eventhandler.beskjed

import no.nav.brukernotifikasjon.schemas.builders.util.ValidationUtil.validateNonNullFieldMaxLength
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import java.sql.Connection

class BeskjedEventService(private val database: Database) {

    suspend fun getActiveEventsForFodselsnummer(fodselsnummer: String): List<Beskjed> =
        getEvents {
            getAktiveBeskjederForFodselsnummer(fodselsnummer)
        }


    suspend fun getInactiveEventsForFodselsnummer(fodselsnummer: String): List<Beskjed> =
        getEvents {
            getInaktiveBeskjederForFodselsnummer(fodselsnummer)
        }


    suspend fun getAllEventsForFodselsnummer(fodselsnummer: String): List<Beskjed> =
        getEvents {
            getAllBeskjederForFodselsnummer(fodselsnummer)
        }

    suspend fun getAllGroupedEventsFromCacheForUser(
        bruker: TokenXUser,
        grupperingsid: String?,
        appnavn: String?
    ): List<Beskjed> {
        val grupperingsId = validateNonNullFieldMaxLength(grupperingsid, "grupperingsid", 100)
        val app = validateNonNullFieldMaxLength(appnavn, "appnavn", 100)
        return getEvents { getAllGroupedBeskjederByGrupperingsId(bruker.ident, grupperingsId, app) }
    }

    suspend fun getAllGroupedEventsByProducerFromCache(): List<EventCountForProducer> =
        database.queryWithExceptionTranslation { getAllGroupedBeskjedEventsByProducer() }


    private suspend fun getEvents(operationToExecute: Connection.() -> List<Beskjed>): List<Beskjed> =
        database.queryWithExceptionTranslation {
            operationToExecute()
        }
}
