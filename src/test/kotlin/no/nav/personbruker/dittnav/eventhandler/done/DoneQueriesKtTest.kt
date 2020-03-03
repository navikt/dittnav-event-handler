package no.nav.personbruker.dittnav.eventhandler.done

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

class DoneQueriesKtTest {

    private val database = H2Database()
    private val bruker = InnloggetBrukerObjectMother.createInnloggetBrukerWithSubject("12345")
    private val uid = "1"
    private val eventId = "125"

    @Test
    fun `Finn alle cachede events som matcher fodselsnummer, uid og eventId`() {
        runBlocking {
            database.dbQuery { getActiveBeskjedByIds(bruker.getIdent(), uid, eventId) }.size `should be equal to` 1
        }
    }

    @Test
    fun `Returnerer tom liste hvis Beskjed-eventer ikke stemmer med eventId`() {
        runBlocking {
            database.dbQuery { getActiveBeskjedByIds(bruker.getIdent(), uid, "dummyEventId") }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer tom liste hvis Beskjed-eventer ikke stemmer med fodselsnummer`() {
        val brukerSomIkkeFinnes = InnloggetBrukerObjectMother.createInnloggetBrukerWithSubject("000")
        runBlocking {
            database.dbQuery { getActiveBeskjedByIds(brukerSomIkkeFinnes.getIdent(), uid, eventId) }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer tom liste av Beskjed-eventer hvis fodselsnummer er tomt`() {
        val fodselsnummerMangler = InnloggetBrukerObjectMother.createInnloggetBrukerWithSubject("")
        runBlocking {
            database.dbQuery { getActiveBeskjedByIds(fodselsnummerMangler.getIdent(), uid, eventId) }.`should be empty`()
        }
    }
}