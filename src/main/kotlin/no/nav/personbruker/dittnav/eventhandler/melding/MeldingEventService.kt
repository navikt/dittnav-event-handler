package no.nav.personbruker.dittnav.eventhandler.melding

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.Database

class MeldingEventService(private val database: Database) {

    fun getCachedActiveEventsForUser(aktorId: String): List<Melding> {
        return runBlocking {
            database.dbQuery { getActiveMeldingByAktorId(aktorId) }
        }
    }

    fun getAllCachedEventsForUser(aktorId: String): List<Melding> {
        return runBlocking {
            database.dbQuery {
                getAllMeldingByAktorId(aktorId) }
        }
    }
}