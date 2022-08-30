package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.brukernotifikasjon.schemas.builders.util.ValidationUtil.validateNonNullFieldMaxLength
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import java.sql.Connection

class OppgaveEventService(private val database: Database) {

    suspend fun getActiveEventsForFodselsnummer(fodselsnummer: String): List<OppgaveDTO> {
        return getEvents {
            getAktivOppgaveForFodselsnummer(fodselsnummer)
        }.map { oppgave -> oppgave.toDTO() }
    }

    suspend fun getInactiveEventsForFodselsnummer(fodselsnummer: String): List<OppgaveDTO> {
        return getEvents {
            getInaktivOppgaveForFodselsnummer(fodselsnummer)
        }.map { oppgave -> oppgave.toDTO() }
    }

    suspend fun getAllEventsForFodselsnummer(fodselsnummer: String): List<OppgaveDTO> {
        return getEvents {
            getAllOppgaveForFodselsnummer(fodselsnummer)
        }.map { oppgave -> oppgave.toDTO() }
    }

    suspend fun getAllGroupedEventsFromCacheForUser(bruker: TokenXUser, grupperingsid: String?, appnavn: String?): List<OppgaveDTO> {
        val grupperingsId = validateNonNullFieldMaxLength(grupperingsid, "grupperingsid", 100)
        val app = validateNonNullFieldMaxLength(appnavn, "appnavn", 100)
        return getEvents { getAllGroupedOppgaveEventsByIds(bruker.ident, grupperingsId, app) }
            .map { oppgave -> oppgave.toDTO() }
    }

    suspend fun getAllGroupedEventsByProducerFromCache(): List<EventCountForProducer> {
        return database.queryWithExceptionTranslation { getAllGroupedOppgaveEventsByProducer() }
    }

    private suspend fun getEvents(operationToExecute: Connection.() -> List<Oppgave>): List<Oppgave> {
        val events = database.queryWithExceptionTranslation {
            operationToExecute()
        }
        return events
    }
}
