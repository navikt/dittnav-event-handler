package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
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

    private val database = LocalPostgresDatabase.cleanDb()

    private val fodselsnummer = "12345"
    private val uid = "22"
    private val eventId = "124"
    private val grupperingsid = "100${fodselsnummer}"
    private val systembruker = "x-dittnav"
    private val namespace = "localhost"
    private val appnavn = "dittnav"

    private val beskjed1 = BeskjedObjectMother.createBeskjed(
        id = 1,
        eventId = "123",
        fodselsnummer = fodselsnummer,
        grupperingsId = grupperingsid,
        synligFremTil = ZonedDateTime.now().plusHours(1),
        uid = "11",
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn
    )
    private val beskjed2 = BeskjedObjectMother.createBeskjed(
        id = 2,
        eventId = eventId,
        fodselsnummer = fodselsnummer,
        grupperingsId = grupperingsid,
        synligFremTil = ZonedDateTime.now().plusHours(1),
        uid = "22",
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn
    )
    private val beskjed3 = BeskjedObjectMother.createBeskjed(
        id = 3,
        eventId = "567",
        fodselsnummer = fodselsnummer,
        grupperingsId = grupperingsid,
        synligFremTil = ZonedDateTime.now().plusHours(1),
        uid = "33",
        aktiv = false,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn
    )
    private val beskjed4 = BeskjedObjectMother.createBeskjed(
        id = 4,
        eventId = "789",
        fodselsnummer = "54321",
        synligFremTil = ZonedDateTime.now().plusHours(1),
        uid = "44",
        aktiv = true,
        systembruker = "x-dittnav-2",
        namespace = namespace,
        appnavn = "dittnav-2"
    )

    @BeforeAll
    fun `populer testdata`() {
        createBeskjed(listOf(beskjed1, beskjed2, beskjed3, beskjed4))
    }

    @AfterAll
    fun `slett testdata`() {
        deleteBeskjed(listOf(beskjed1, beskjed2, beskjed3, beskjed4))
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
            beskjed.produsent `should be equal to` appnavn
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for inaktive eventer`() {
        runBlocking {
            val beskjed = database.dbQuery { getInaktivBeskjedForInnloggetBruker(fodselsnummer) }.first()
            beskjed.produsent `should be equal to` appnavn
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for alle eventer`() {
        runBlocking {
            val beskjed = database.dbQuery { getAllBeskjedForInnloggetBruker(fodselsnummer) }.first()
            beskjed.produsent `should be equal to` appnavn
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
    fun `Returnerer en liste av alle grupperte Beskjed-eventer`() {
        runBlocking {
            database.dbQuery {
                getAllGroupedBeskjedEventsByIds(fodselsnummer, grupperingsid, appnavn)
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
                getAllGroupedBeskjedEventsByIds(fodselsnummer, noMatchGrupperingsid, appnavn)
            }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte Beskjed-eventer basert paa systembruker`() {
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { getAllGroupedBeskjedEventsBySystemuser() }

            groupedEventsBySystemuser.size `should be equal to` 2
            groupedEventsBySystemuser[beskjed1.systembruker] `should be equal to` 3
            groupedEventsBySystemuser[beskjed4.systembruker] `should be equal to` 1
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

    private fun deleteBeskjed(beskjeder: List<Beskjed>) {
        runBlocking {
            database.dbQuery { deleteBeskjed(beskjeder) }
        }
    }
}
