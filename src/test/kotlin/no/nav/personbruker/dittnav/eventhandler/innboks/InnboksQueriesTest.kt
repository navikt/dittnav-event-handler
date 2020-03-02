package no.nav.personbruker.dittnav.eventhandler.innboks

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

class InnboksQueriesTest {

    private val database = H2Database()

    private val bruker1 = InnloggetBrukerObjectMother.createInnloggetBrukerWithSubject("12345")
    private val bruker2 = InnloggetBrukerObjectMother.createInnloggetBrukerWithSubject("67890")

    @Test
    fun `should find all active events for fodselsnummers`() {
        runBlocking {
            database.dbQuery { getActiveInnboksByUser(bruker1) }.size `should be equal to` 2
            database.dbQuery { getActiveInnboksByUser(bruker2) }.size `should be equal to` 1
        }
    }

    @Test
    fun `should find all events for fodselsnummers`() {
        runBlocking {
            database.dbQuery { getAllInnboksByUser(bruker1) }.size `should be equal to` 2
            database.dbQuery { getAllInnboksByUser(bruker2) }.size `should be equal to` 2
        }
    }

    @Test
    fun `should return empty list if no events exists for fodselsnummer`() {
        val brukerUtenEventer = InnloggetBrukerObjectMother.createInnloggetBrukerWithSubject("0")
        runBlocking {
            database.dbQuery { getAllInnboksByUser(brukerUtenEventer) }.size `should be equal to` 0
        }
    }
}