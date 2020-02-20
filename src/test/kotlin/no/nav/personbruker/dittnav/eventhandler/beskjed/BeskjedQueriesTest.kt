package no.nav.personbruker.dittnav.eventhandler.beskjed

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.api.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import org.junit.jupiter.api.Test
import org.amshove.kluent.*

class BeskjedQueriesTest {

    private val database = H2Database()

    private val bruker = InnloggetBrukerObjectMother.createInnloggetBrukerWithSubject("12345")

    @Test
    fun `Finn alle cachede Beskjed-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getAllBeskjedByFodselsnummer(bruker) }.size `should be equal to` 3
        }
    }

    @Test
    fun `Finner kun aktive cachede Beskjed-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getActiveBeskjedByFodselsnummer(bruker) }.size `should be equal to` 2
        }
    }

    @Test
    fun `Returnerer tom liste hvis Beskjed-eventer for fodselsnummer ikke finnes`() {
        val brukerSomIkkeFinnes = InnloggetBrukerObjectMother.createInnloggetBrukerWithSubject("0")
        runBlocking {
            database.dbQuery { getActiveBeskjedByFodselsnummer(brukerSomIkkeFinnes) }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer tom liste hvis Beskjed-eventer hvis tom fodselsnummer`() {
        val fodselsnummerMangler = InnloggetBrukerObjectMother.createInnloggetBrukerWithSubject("")
        runBlocking {
            database.dbQuery { getActiveBeskjedByFodselsnummer(fodselsnummerMangler) }.`should be empty`()
        }
    }
}
