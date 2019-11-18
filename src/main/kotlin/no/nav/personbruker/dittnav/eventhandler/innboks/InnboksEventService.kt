package no.nav.personbruker.dittnav.eventhandler.innboks

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.Database

class InnboksEventService(private val database: Database) {

    fun getCachedActiveEventsForUser(aktorId: String): List<Innboks> {
        return runBlocking {
            database.dbQuery { getActiveInnboksByAktorId(aktorId) }
        }
    }

    fun getAllCachedEventsForUser(aktorId: String): List<Innboks> {
        return runBlocking {
            database.dbQuery {
                getAllInnboksByAktorId(aktorId) }
        }
    }
}