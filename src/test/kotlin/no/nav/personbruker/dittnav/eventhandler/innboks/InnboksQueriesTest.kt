package no.nav.personbruker.dittnav.eventhandler.innboks

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

class InnboksQueriesTest {

    private val database = H2Database()

    private val fodselsnummer1 = "12345"
    private val fodselsnummer2 = "67890"

    @Test
    fun `should find all active events for fodselsnummers`() {
        runBlocking {
            database.dbQuery { getActiveInnboksByFodselsnummer(fodselsnummer1) }.size `should be equal to` 2
            database.dbQuery { getActiveInnboksByFodselsnummer(fodselsnummer2) }.size `should be equal to` 1
        }
    }

    @Test
    fun `should find all events for fodselsnummers`() {
        runBlocking {
            database.dbQuery { getAllInnboksByFodselsnummer(fodselsnummer1) }.size `should be equal to` 2
            database.dbQuery { getAllInnboksByFodselsnummer(fodselsnummer2) }.size `should be equal to` 2
        }
    }

    @Test
    fun `should return empty list if no events exists for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getAllInnboksByFodselsnummer("VOID") }.size `should be equal to` 0
        }
    }
}