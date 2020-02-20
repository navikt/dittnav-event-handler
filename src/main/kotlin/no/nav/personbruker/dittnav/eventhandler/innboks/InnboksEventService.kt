package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.Database

class InnboksEventService(private val database: Database) {

    suspend fun getCachedActiveEventsForUser(bruker: InnloggetBruker): List<Innboks> {
        return database.dbQuery { getActiveInnboksByUser(bruker) }
    }

    suspend fun getAllCachedEventsForUser(bruker: InnloggetBruker): List<Innboks> {
        return database.dbQuery { getAllInnboksByUser(bruker) }
    }
}