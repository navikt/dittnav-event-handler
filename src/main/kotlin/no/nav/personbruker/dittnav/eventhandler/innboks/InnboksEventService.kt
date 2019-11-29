package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.personbruker.dittnav.eventhandler.common.database.Database

class InnboksEventService(private val database: Database) {

    suspend fun getCachedActiveEventsForUser(fodselsnummer: String): List<Innboks> {
        return database.dbQuery { getActiveInnboksByFodselsnummer(fodselsnummer) }
    }

    suspend fun getAllCachedEventsForUser(fodselsnummer: String): List<Innboks> {
        return database.dbQuery { getAllInnboksByFodselsnummer(fodselsnummer) }
    }
}