package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.brukernotifikasjon.schemas.builders.util.ValidationUtil.validateNonNullFieldMaxLength
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.modia.User
import no.nav.personbruker.dittnav.eventhandler.common.oneYearAgo
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import java.sql.Connection

class OppgaveEventService(private val database: Database) {

    suspend fun getActiveCachedEventsForUser(bruker: TokenXUser): List<OppgaveDTO> {
        return getEvents { getAktivOppgaveForInnloggetBruker(bruker.ident) }
            .map { oppgave -> oppgave.toDTO() }
    }

    suspend fun getActiveCachedEventsForUser(bruker: User): List<OppgaveDTO> {
        return getEvents { getAktivOppgaveForInnloggetBruker(bruker.fodselsnummer) }
            .map { oppgave -> oppgave.toDTO() }
    }

    suspend fun getInactiveCachedEventsForUser(bruker: TokenXUser): List<OppgaveDTO> {
        return getEvents { getInaktivOppgaveForInnloggetBruker(bruker.ident) }
            .map { oppgave -> oppgave.toDTO() }
    }

    suspend fun getInactiveCachedEventsForUser(bruker: User): List<OppgaveDTO> {
        return getEvents { getInaktivOppgaveForInnloggetBruker(bruker.fodselsnummer) }
            .map { oppgave -> oppgave.toDTO() }
    }

    suspend fun getAllCachedEventsForUser(bruker: TokenXUser): List<OppgaveDTO> {
        return getEvents { getAllOppgaveForInnloggetBruker(bruker.ident) }
            .map { oppgave -> oppgave.toDTO() }
    }

    suspend fun getAllCachedEventsForUser(bruker: User): List<OppgaveDTO> {
        return getEvents { getAllOppgaveForInnloggetBruker(bruker.fodselsnummer) }
            .map { oppgave -> oppgave.toDTO() }
    }

    suspend fun getRecentActiveEventsForUser(bruker: TokenXUser): List<OppgaveDTO> {
        return getEvents { getAktivOppgaveForFodselsnummerByForstBehandlet(bruker.ident, oneYearAgo()) }
            .map { oppgave -> oppgave.toDTO() }
    }

    suspend fun getRecentActiveEventsForUser(bruker: User): List<OppgaveDTO> {
        return getEvents { getAktivOppgaveForFodselsnummerByForstBehandlet(bruker.fodselsnummer, oneYearAgo()) }
            .map { oppgave -> oppgave.toDTO() }
    }

    suspend fun getRecentInactiveEventsForUser(bruker: TokenXUser): List<OppgaveDTO> {
        return getEvents { getInaktivOppgaveForFodselsnummerByForstBehandlet(bruker.ident, oneYearAgo()) }
            .map { oppgave -> oppgave.toDTO() }
    }

    suspend fun getRecentInactiveEventsForUser(bruker: User): List<OppgaveDTO> {
        return getEvents { getInaktivOppgaveForFodselsnummerByForstBehandlet(bruker.fodselsnummer, oneYearAgo()) }
            .map { oppgave -> oppgave.toDTO() }
    }

    suspend fun getAllRecentEventsForUser(bruker: TokenXUser): List<OppgaveDTO> {
        return getEvents { getOppgaveForFodselsnummerByForstBehandlet(bruker.ident, oneYearAgo()) }
            .map { oppgave -> oppgave.toDTO() }
    }

    suspend fun getAllRecentEventsForUser(bruker: User): List<OppgaveDTO> {
        return getEvents { getOppgaveForFodselsnummerByForstBehandlet(bruker.fodselsnummer, oneYearAgo()) }
            .map { oppgave -> oppgave.toDTO() }
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
