package no.nav.personbruker.dittnav.eventhandler.melding

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

class MeldingQueriesTest {

    private val database = H2Database()

    private val aktorId1 = "12345"
    private val aktorId2 = "67890"

    @Test
    fun `should find all active events for aktorIds`() {
        runBlocking {
            database.dbQuery { getActiveMeldingByAktorId(aktorId1) }.size `should be equal to` 2
            database.dbQuery { getActiveMeldingByAktorId(aktorId2) }.size `should be equal to` 1
        }
    }

    @Test
    fun `should find all events for aktorIds`() {
        runBlocking {
            database.dbQuery { getAllMeldingByAktorId(aktorId1) }.size `should be equal to` 2
            database.dbQuery { getAllMeldingByAktorId(aktorId2) }.size `should be equal to` 2
        }
    }

    @Test
    fun `should return empty list if no events exists for aktorId`() {
        runBlocking {
            database.dbQuery { getAllMeldingByAktorId("VOID") }.size `should be equal to` 0
        }
    }
}