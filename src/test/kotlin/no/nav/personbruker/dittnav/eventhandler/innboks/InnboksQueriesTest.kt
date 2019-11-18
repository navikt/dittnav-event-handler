package no.nav.personbruker.dittnav.eventhandler.innboks

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

class InnboksQueriesTest {

    private val database = H2Database()

    private val aktorId1 = "12345"
    private val aktorId2 = "67890"

    @Test
    fun `should find all active events for aktorIds`() {
        runBlocking {
            database.dbQuery { getActiveInnboksByAktorId(aktorId1) }.size `should be equal to` 2
            database.dbQuery { getActiveInnboksByAktorId(aktorId2) }.size `should be equal to` 1
        }
    }

    @Test
    fun `should find all events for aktorIds`() {
        runBlocking {
            database.dbQuery { getAllInnboksByAktorId(aktorId1) }.size `should be equal to` 2
            database.dbQuery { getAllInnboksByAktorId(aktorId2) }.size `should be equal to` 2
        }
    }

    @Test
    fun `should return empty list if no events exists for aktorId`() {
        runBlocking {
            database.dbQuery { getAllInnboksByAktorId("VOID") }.size `should be equal to` 0
        }
    }
}