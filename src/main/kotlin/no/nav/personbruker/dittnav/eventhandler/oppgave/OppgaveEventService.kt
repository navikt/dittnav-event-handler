package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.Database

class OppgaveEventService(
        private val database: Database
) {

    suspend fun getActiveCachedEventsForUser(bruker: InnloggetBruker): List<Oppgave> {
        return database.queryWithExceptionTranslation { getAktivOppgaveForInnloggetBruker(bruker) }
    }

    suspend fun getInactiveCachedEventsForUser(bruker: InnloggetBruker): List<Oppgave> {
        return database.queryWithExceptionTranslation { getInaktivOppgaveForInnloggetBruker(bruker) }
    }

    suspend fun getAllCachedEventsForUser(bruker: InnloggetBruker): List<Oppgave> {
        return database.queryWithExceptionTranslation { getAllOppgaveForInnloggetBruker(bruker) }
    }
}
