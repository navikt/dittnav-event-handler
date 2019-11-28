package no.nav.personbruker.dittnav.eventhandler.beskjed

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import org.junit.jupiter.api.Test
import org.amshove.kluent.*

class BeskjedQueriesTest {

    private val database = H2Database()

    @Test
    fun `Finn alle cachede Beskjed-eventer for aktorID`() {
        runBlocking {
            database.dbQuery { getAllBeskjedByAktorId("12345") }.size `should be equal to` 3
        }
    }

    @Test
    fun `Finner kun aktive cachede Beskjed-eventer for aktorID`() {
        runBlocking {
            database.dbQuery { getActiveBeskjedByAktorId("12345") }.size `should be equal to` 2
        }
    }

    @Test
    fun `Returnerer tom liste hvis Beskjed-eventer for aktorID ikke finnes`() {
        runBlocking {
            database.dbQuery { getActiveBeskjedByAktorId("finnesikke") }.`should be empty`()
        }
    }


    @Test
    fun `Returnerer tom liste hvis Beskjed-eventer hvis tom aktorID`() {
        runBlocking {
            database.dbQuery { getActiveBeskjedByAktorId("") }.`should be empty`()
        }
    }
}
