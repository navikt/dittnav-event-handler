package no.nav.personbruker.dittnav.eventhandler.done

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import no.nav.personbruker.dittnav.eventhandler.beskjed.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.deleteBeskjed
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.ZonedDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DoneQueriesTest {

    private val database = H2Database()
    private val bruker = InnloggetBrukerObjectMother.createInnloggetBruker("12345")
    private val uid = "22"
    private val eventId = "124"

    private val beskjed1 = BeskjedObjectMother.createBeskjed(id = 1, eventId = "123", fodselsnummer = "12345",
            synligFremTil = ZonedDateTime.now().plusHours(1), uid = "11", aktiv = true)
    private val beskjed2 = BeskjedObjectMother.createBeskjed(id = 2, eventId = "124", fodselsnummer = "12345",
            synligFremTil = ZonedDateTime.now().plusHours(1), uid = "22", aktiv = true)
    private val beskjed3 = BeskjedObjectMother.createBeskjed(id = 3, eventId = "125", fodselsnummer = "12345",
            synligFremTil = ZonedDateTime.now().plusHours(1), uid = "23", aktiv = false)
    private val beskjed4 = BeskjedObjectMother.createBeskjed(id = 4, eventId = "126", fodselsnummer = "54321",
            synligFremTil = ZonedDateTime.now().plusHours(1), uid = "24", aktiv = true)

    @BeforeAll
    fun `populer tabellen med Beskjed-eventer`() {
        runBlocking {
            database.dbQuery { createBeskjed(listOf(beskjed1, beskjed2, beskjed3, beskjed4)) }
        }
    }

    @AfterAll
    fun `slett Beskjed-eventer fra tabellen`() {
        runBlocking {
            database.dbQuery { deleteBeskjed(listOf(beskjed1, beskjed2, beskjed3, beskjed4)) }
        }
    }

    @Test
    fun `Finn alle cachede events som matcher fodselsnummer, uid og eventId`() {
        runBlocking {
            database.dbQuery { getActiveBeskjedByIds(bruker.ident, uid, eventId) }.size `should be equal to` 1
        }
    }

    @Test
    fun `Returnerer tom liste hvis Beskjed-eventer ikke stemmer med eventId`() {
        runBlocking {
            database.dbQuery { getActiveBeskjedByIds(bruker.ident, uid, "dummyEventId") }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer tom liste hvis Beskjed-eventer ikke stemmer med fodselsnummer`() {
        val brukerSomIkkeFinnes = InnloggetBrukerObjectMother.createInnloggetBruker("000")
        runBlocking {
            database.dbQuery { getActiveBeskjedByIds(brukerSomIkkeFinnes.ident, uid, eventId) }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer tom liste av Beskjed-eventer hvis fodselsnummer er tomt`() {
        val fodselsnummerMangler = InnloggetBrukerObjectMother.createInnloggetBruker("")
        runBlocking {
            database.dbQuery { getActiveBeskjedByIds(fodselsnummerMangler.ident, uid, eventId) }.`should be empty`()
        }
    }
}
