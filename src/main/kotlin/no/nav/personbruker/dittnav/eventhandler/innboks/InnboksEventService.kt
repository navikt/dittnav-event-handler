package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.Database

class InnboksEventService(private val database: Database) {

    suspend fun getActiveCachedEventsForUser(bruker: InnloggetBruker): List<Innboks> {
        return database.queryWithExceptionTranslation { getAktivInnboksForInnloggetBruker(bruker) }
    }

    suspend fun getInctiveCachedEventsForUser(bruker: InnloggetBruker): List<Innboks> {
        return database.queryWithExceptionTranslation { getAktivInnboksForInnloggetBruker(bruker) }
    }

    suspend fun getAllCachedEventsForUser(bruker: InnloggetBruker): List<Innboks> {
        return database.queryWithExceptionTranslation { getAllInnboksForInnloggetBruker(bruker) }
    }
}
