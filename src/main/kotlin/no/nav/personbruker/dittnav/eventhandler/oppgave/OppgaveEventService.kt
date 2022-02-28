package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.brukernotifikasjon.schemas.builders.util.ValidationUtil.validateNonNullFieldMaxLength
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.modia.User
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import org.slf4j.LoggerFactory
import java.sql.Connection

class OppgaveEventService(private val database: Database) {

    private val log = LoggerFactory.getLogger(OppgaveEventService::class.java)

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

    suspend fun getAllGroupedEventsFromCacheForUser(bruker: TokenXUser, grupperingsid: String?, producer: String?): List<OppgaveDTO> {
        val grupperingsId = validateNonNullFieldMaxLength(grupperingsid, "grupperingsid", 100)
        val produsent = validateNonNullFieldMaxLength(producer, "produsent", 100)
        return getEvents { getAllGroupedOppgaveEventsByIds(bruker.ident, grupperingsId, produsent) }
            .map { oppgave -> oppgave.toDTO() }
    }

    suspend fun getAllGroupedEventsBySystemuserFromCache(): Map<String, Int> {
        return database.queryWithExceptionTranslation { getAllGroupedOppgaveEventsBySystemuser() }
    }

    suspend fun getAllGroupedEventsByProducerFromCache(): List<EventCountForProducer> {
        return database.queryWithExceptionTranslation { getAllGroupedOppgaveEventsByProducer() }
    }


    private suspend fun getEvents(operationToExecute: Connection.() -> List<Oppgave>): List<Oppgave> {
        val events = database.queryWithExceptionTranslation {
            operationToExecute()
        }
        val eventsWithEmptyProdusent = events.filter { oppgave -> oppgave.produsent.isNullOrEmpty() }

        if (eventsWithEmptyProdusent.isNotEmpty()) {
            logEventsWithEmptyProdusent(eventsWithEmptyProdusent)
        }
        return events
    }

    private fun logEventsWithEmptyProdusent(events: List<Oppgave>) {
        events.forEach { oppgave ->
            log.warn("Returnerer oppgave-eventer med tom produsent til frontend. Kanskje er ikke systembrukeren lagt inn i systembruker-tabellen? ${oppgave.toString()}")
        }
    }
}
