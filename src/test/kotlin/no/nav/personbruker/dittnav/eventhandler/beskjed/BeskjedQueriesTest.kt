package no.nav.personbruker.dittnav.eventhandler.beskjed

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import org.junit.jupiter.api.Test
import org.amshove.kluent.*

class BeskjedQueriesTest {

    private val database = H2Database()

    @Test
    fun `Finn alle cachede Beskjed-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getAllBeskjedByFodselsnummer("12345") }.size `should be equal to` 3
        }
    }

    @Test
    fun `Finner kun aktive cachede Beskjed-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getActiveBeskjedByFodselsnummer("12345") }.size `should be equal to` 2
        }
    }

    @Test
    fun `Returnerer tom liste hvis Beskjed-eventer for fodselsnummer ikke finnes`() {
        runBlocking {
            database.dbQuery { getActiveBeskjedByFodselsnummer("finnesikke") }.`should be empty`()
        }
    }


    @Test
    fun `Returnerer tom liste hvis Beskjed-eventer hvis tom fodselsnummer`() {
        runBlocking {
            database.dbQuery { getActiveBeskjedByFodselsnummer("") }.`should be empty`()
        }
    }
}
