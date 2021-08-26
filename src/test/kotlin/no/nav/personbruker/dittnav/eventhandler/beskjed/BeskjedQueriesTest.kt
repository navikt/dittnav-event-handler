package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import no.nav.personbruker.dittnav.eventhandler.common.database.createProdusent
import no.nav.personbruker.dittnav.eventhandler.common.database.deleteProdusent
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.ZonedDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BeskjedQueriesTest {

    private val database = H2Database()

    private val bruker = InnloggetBrukerObjectMother.createInnloggetBruker("12345")
    private val uid = "22"
    private val eventId = "124"
    private val grupperingsid = "100${bruker.ident}"
    private val produsent = "x-dittnav-produsent"

    private val beskjed1 = BeskjedObjectMother.createBeskjed(id = 1, eventId = "123", fodselsnummer = bruker.ident,
            synligFremTil = ZonedDateTime.now().plusHours(1), uid = "11", aktiv = true, systembruker = "x-dittnav")
    private val beskjed2 = BeskjedObjectMother.createBeskjed(id = 2, eventId = eventId, fodselsnummer = bruker.ident,
            synligFremTil = ZonedDateTime.now().plusHours(1), uid = "22", aktiv = true, systembruker = "x-dittnav")
    private val beskjed3 = BeskjedObjectMother.createBeskjed(id = 3, eventId = "567", fodselsnummer = bruker.ident,
            synligFremTil = ZonedDateTime.now().plusHours(1), uid = "33", aktiv = false, systembruker = "x-dittnav")
    private val beskjed4 = BeskjedObjectMother.createBeskjed(id = 4, eventId = "789", fodselsnummer = "54321",
            synligFremTil = ZonedDateTime.now().plusHours(1), uid = "44", aktiv = true, systembruker = "y-dittnav")

    @BeforeAll
    fun `populer testdata`() {
        createBeskjed(listOf(beskjed1, beskjed2, beskjed3, beskjed4))
        createSystembruker(systembruker = "x-dittnav", produsentnavn = "x-dittnav-produsent")
        createSystembruker(systembruker = "y-dittnav", produsentnavn = "y-dittnav-produsent")
    }

    @AfterAll
    fun `slett testdata`() {
        deleteBeskjed(listOf(beskjed1, beskjed2, beskjed3, beskjed4))
        deleteSystembruker(systembruker = "x-dittnav")
        deleteSystembruker(systembruker = "y-dittnav")
    }

    @Test
    fun `Finn alle cachede Beskjed-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getAllBeskjedForInnloggetBruker(bruker) }.size `should be equal to` 3
        }
    }

    @Test
    fun `Finn kun aktive cachede Beskjed-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery {
                val aktivBeskjedByUser = getAktivBeskjedForInnloggetBruker(bruker)
                aktivBeskjedByUser
            }.size `should be equal to` 2
        }
    }

    @Test
    fun `Finn kun inaktive cachede Beskjed-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery {
                val inaktivBeskjedByUser = getInaktivBeskjedForInnloggetBruker(bruker)
                inaktivBeskjedByUser
            }.size `should be equal to` 1
        }
    }

    @Test
    fun `Returnerer tom liste hvis Beskjed-eventer for fodselsnummer ikke finnes`() {
        val brukerSomIkkeFinnes = InnloggetBrukerObjectMother.createInnloggetBruker("0")
        runBlocking {
            database.dbQuery { getAktivBeskjedForInnloggetBruker(brukerSomIkkeFinnes) }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer tom liste hvis fodselsnummer er tomt`() {
        val fodselsnummerMangler = InnloggetBrukerObjectMother.createInnloggetBruker("")
        runBlocking {
            database.dbQuery { getAktivBeskjedForInnloggetBruker(fodselsnummerMangler) }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for aktive eventer`() {
        runBlocking {
            val beskjed = database.dbQuery { getAktivBeskjedForInnloggetBruker(bruker) }.first()
            beskjed.produsent `should be equal to` "x-dittnav-produsent"
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for inaktive eventer`() {
        runBlocking {
            val beskjed = database.dbQuery { getInaktivBeskjedForInnloggetBruker(bruker) }.first()
            beskjed.produsent `should be equal to` "x-dittnav-produsent"
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for alle eventer`() {
        runBlocking {
            val beskjed = database.dbQuery { getAllBeskjedForInnloggetBruker(bruker) }.first()
            beskjed.produsent `should be equal to` "x-dittnav-produsent"
        }
    }

    @Test
    fun `Finn alle cachede events som matcher fodselsnummer, uid og eventId`() {
        runBlocking {
            database.dbQuery { getBeskjedByIds(bruker.ident, uid, eventId) }.size `should be equal to` 1
        }
    }

    @Test
    fun `Returnerer tom liste hvis Beskjed-eventer ikke stemmer med eventId`() {
        runBlocking {
            database.dbQuery { getBeskjedByIds(bruker.ident, uid, "dummyEventId") }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer tom liste hvis Beskjed-eventer ikke stemmer med fodselsnummer`() {
        val brukerSomIkkeFinnes = InnloggetBrukerObjectMother.createInnloggetBruker("000")
        runBlocking {
            database.dbQuery { getBeskjedByIds(brukerSomIkkeFinnes.ident, uid, eventId) }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer tom liste av Beskjed-eventer hvis fodselsnummer er tomt`() {
        val fodselsnummerMangler = InnloggetBrukerObjectMother.createInnloggetBruker("")
        runBlocking {
            database.dbQuery { getBeskjedByIds(fodselsnummerMangler.ident, uid, eventId) }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer tom streng for produsent hvis eventet er produsert av systembruker vi ikke har i systembruker-tabellen`() {
        var beskjedMedAnnenProdusent = BeskjedObjectMother.createBeskjed(id = 5, eventId = "111", fodselsnummer = "112233",
                synligFremTil = ZonedDateTime.now().plusHours(1), uid = "11", aktiv = true, systembruker = "ukjent-systembruker")
        createBeskjed(listOf(beskjedMedAnnenProdusent))
        val beskjed = runBlocking {
            database.dbQuery {
                getAllBeskjedForInnloggetBruker(InnloggetBrukerObjectMother.createInnloggetBruker("112233"))
            }.first()
        }
        beskjed.produsent `should be equal to` ""
        deleteBeskjed(listOf(beskjedMedAnnenProdusent))
    }

    @Test
    fun `Returnerer liste av alle Beskjed-eventer`() {
        runBlocking {
            database.dbQuery { getAllBeskjedEvents() }.size `should be equal to` 4
        }
    }

    @Test
    fun `Returnerer liste av alle inaktive Beskjed-eventer`() {
        runBlocking {
            database.dbQuery { getAllInactiveBeskjedEvents() }.size `should be equal to` 1
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte Beskjed-eventer`() {
        runBlocking {
            database.dbQuery {
                getAllGroupedBeskjedEventsByIds(bruker, grupperingsid, produsent)
            }.size `should be equal to` 3
        }
    }

    @Test
    fun `Returnerer en tom liste hvis produsent ikke matcher beskjed-eventet`() {
        val noMatchProdusent = "dummyProdusent"
        runBlocking {
            database.dbQuery {
                getAllGroupedBeskjedEventsByIds(bruker, grupperingsid, noMatchProdusent)
            }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer en tom liste hvis grupperingsid ikke matcher beskjed-eventet`() {
        val noMatchGrupperingsid = "dummyGrupperingsid"
        runBlocking {
            database.dbQuery {
                getAllGroupedBeskjedEventsByIds(bruker, noMatchGrupperingsid, produsent)
            }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte Beskjed-eventer basert paa systembruker`() {
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { getAllGroupedBeskjedEventsBySystemuser() }

            groupedEventsBySystemuser.size `should be equal to` 2
            groupedEventsBySystemuser.get(beskjed1.systembruker) `should be equal to` 3
            groupedEventsBySystemuser.get(beskjed4.systembruker) `should be equal to` 1
        }
    }

    private fun createBeskjed(beskjeder: List<Beskjed>) {
        runBlocking {
            database.dbQuery { createBeskjed(beskjeder) }
        }
    }

    private fun createSystembruker(systembruker: String, produsentnavn: String) {
        runBlocking {
            database.dbQuery { createProdusent(systembruker, produsentnavn) }
        }
    }

    private fun deleteBeskjed(beskjeder: List<Beskjed>) {
        runBlocking {
            database.dbQuery { deleteBeskjed(beskjeder) }
        }
    }

    private fun deleteSystembruker(systembruker: String) {
        runBlocking {
            database.dbQuery { deleteProdusent(systembruker) }
        }
    }
}
