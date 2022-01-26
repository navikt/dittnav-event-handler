package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.TokenXUserObjectMother
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import no.nav.personbruker.dittnav.eventhandler.common.database.createProdusent
import no.nav.personbruker.dittnav.eventhandler.common.database.deleteProdusent
import no.nav.personbruker.dittnav.eventhandler.common.findCountFor
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

    private val fodselsnummer = "12345"
    private val uid = "22"
    private val eventId = "124"
    private val grupperingsid = "100${fodselsnummer}"
    private val produsent = "x-dittnav-produsent"

    private val beskjed1 = BeskjedObjectMother.createBeskjed(id = 1, eventId = "123", fodselsnummer = fodselsnummer,
            synligFremTil = ZonedDateTime.now().plusHours(1), uid = "11", aktiv = true, systembruker = "x-dittnav", namespace = "dummyNamespace", appnavn = "x-dittnav")
    private val beskjed2 = BeskjedObjectMother.createBeskjed(id = 2, eventId = eventId, fodselsnummer = fodselsnummer,
            synligFremTil = ZonedDateTime.now().plusHours(1), uid = "22", aktiv = true, systembruker = "x-dittnav", namespace = "dummyNamespace", appnavn = "x-dittnav")
    private val beskjed3 = BeskjedObjectMother.createBeskjed(id = 3, eventId = "567", fodselsnummer = fodselsnummer,
            synligFremTil = ZonedDateTime.now().plusHours(1), uid = "33", aktiv = false, systembruker = "x-dittnav", namespace = "dummyNamespace", appnavn = "x-dittnav")
    private val beskjed4 = BeskjedObjectMother.createBeskjed(id = 4, eventId = "789", fodselsnummer = "54321",
            synligFremTil = ZonedDateTime.now().plusHours(1), uid = "44", aktiv = true, systembruker = "y-dittnav", namespace = "dummyNamespace", appnavn = "y-dittnav")

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
            database.dbQuery { getAllBeskjedForInnloggetBruker(fodselsnummer) }.size `should be equal to` 3
        }
    }

    @Test
    fun `Finn kun aktive cachede Beskjed-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery {
                val aktivBeskjedByUser = getAktivBeskjedForInnloggetBruker(fodselsnummer)
                aktivBeskjedByUser
            }.size `should be equal to` 2
        }
    }

    @Test
    fun `Finn kun inaktive cachede Beskjed-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery {
                val inaktivBeskjedByUser = getInaktivBeskjedForInnloggetBruker(fodselsnummer)
                inaktivBeskjedByUser
            }.size `should be equal to` 1
        }
    }

    @Test
    fun `Returnerer tom liste hvis Beskjed-eventer for fodselsnummer ikke finnes`() {
        val brukerSomIkkeFinnes = "0"
        runBlocking {
            database.dbQuery { getAktivBeskjedForInnloggetBruker(brukerSomIkkeFinnes) }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer tom liste hvis fodselsnummer er tomt`() {
        val fodselsnummerMangler = ""
        runBlocking {
            database.dbQuery { getAktivBeskjedForInnloggetBruker(fodselsnummerMangler) }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for aktive eventer`() {
        runBlocking {
            val beskjed = database.dbQuery { getAktivBeskjedForInnloggetBruker(fodselsnummer) }.first()
            beskjed.produsent `should be equal to` "x-dittnav-produsent"
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for inaktive eventer`() {
        runBlocking {
            val beskjed = database.dbQuery { getInaktivBeskjedForInnloggetBruker(fodselsnummer) }.first()
            beskjed.produsent `should be equal to` "x-dittnav-produsent"
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for alle eventer`() {
        runBlocking {
            val beskjed = database.dbQuery { getAllBeskjedForInnloggetBruker(fodselsnummer) }.first()
            beskjed.produsent `should be equal to` "x-dittnav-produsent"
        }
    }

    @Test
    fun `Finn alle cachede events som matcher fodselsnummer, uid og eventId`() {
        runBlocking {
            database.dbQuery { getBeskjedByIds(fodselsnummer, uid, eventId) }.size `should be equal to` 1
        }
    }

    @Test
    fun `Returnerer tom liste hvis Beskjed-eventer ikke stemmer med eventId`() {
        runBlocking {
            database.dbQuery { getBeskjedByIds(fodselsnummer, uid, "dummyEventId") }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer tom liste hvis Beskjed-eventer ikke stemmer med fodselsnummer`() {
        val brukerSomIkkeFinnes = "000"
        runBlocking {
            database.dbQuery { getBeskjedByIds(brukerSomIkkeFinnes, uid, eventId) }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer tom liste av Beskjed-eventer hvis fodselsnummer er tomt`() {
        val fodselsnummerMangler = ""
        runBlocking {
            database.dbQuery { getBeskjedByIds(fodselsnummerMangler, uid, eventId) }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer tom streng for produsent hvis eventet er produsert av systembruker vi ikke har i systembruker-tabellen`() {
        var beskjedMedAnnenProdusent = BeskjedObjectMother.createBeskjed(id = 5, eventId = "111", fodselsnummer = "112233",
                synligFremTil = ZonedDateTime.now().plusHours(1), uid = "11", aktiv = true, systembruker = "ukjent-systembruker", namespace = "", appnavn = "")
        createBeskjed(listOf(beskjedMedAnnenProdusent))
        val beskjed = runBlocking {
            database.dbQuery {
                getAllBeskjedForInnloggetBruker(TokenXUserObjectMother.createInnloggetBruker("112233").ident)
            }.first()
        }
        beskjed.produsent `should be equal to` ""
        deleteBeskjed(listOf(beskjedMedAnnenProdusent))
    }

    @Test
    fun `Returnerer en liste av alle grupperte Beskjed-eventer`() {
        runBlocking {
            database.dbQuery {
                getAllGroupedBeskjedEventsByIds(fodselsnummer, grupperingsid, produsent)
            }.size `should be equal to` 3
        }
    }

    @Test
    fun `Returnerer en tom liste hvis produsent ikke matcher beskjed-eventet`() {
        val noMatchProdusent = "dummyProdusent"
        runBlocking {
            database.dbQuery {
                getAllGroupedBeskjedEventsByIds(fodselsnummer, grupperingsid, noMatchProdusent)
            }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer en tom liste hvis grupperingsid ikke matcher beskjed-eventet`() {
        val noMatchGrupperingsid = "dummyGrupperingsid"
        runBlocking {
            database.dbQuery {
                getAllGroupedBeskjedEventsByIds(fodselsnummer, noMatchGrupperingsid, produsent)
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

    @Test
    fun `Returnerer en liste av alle grupperte Beskjed-eventer basert paa produsent`() {
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { getAllGroupedBeskjedEventsByProducer() }

            groupedEventsBySystemuser.size `should be equal to` 2
            groupedEventsBySystemuser.findCountFor(beskjed1.namespace, beskjed1.appnavn) `should be equal to` 3
            groupedEventsBySystemuser.findCountFor(beskjed4.namespace, beskjed4.appnavn) `should be equal to` 1
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
