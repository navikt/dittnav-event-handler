package no.nav.personbruker.dittnav.eventhandler.innboks

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.Database

class InnboksEventService(private val database: Database) {

    fun getCachedActiveEventsForUser(fodselsnummer: String): List<Innboks> {
        return runBlocking {
            database.dbQuery { getActiveInnboksByFodselsnummer(fodselsnummer) }
        }
    }

    fun getAllCachedEventsForUser(fodselsnummer: String): List<Innboks> {
        return runBlocking {
            database.dbQuery {
                getAllInnboksByFodselsnummer(fodselsnummer) }
        }
    }
}