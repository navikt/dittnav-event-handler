package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.common.findCountFor
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.*
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingStatus.OVERSENDT
import org.junit.jupiter.api.*
import java.time.ZonedDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BeskjedQueriesTest {

    private val database = LocalPostgresDatabase.cleanDb()

    private val fodselsnummer = "12345"
    private val eventId = "124"
    private val grupperingsid = "100$fodselsnummer"
    private val systembruker = "x-dittnav"
    private val namespace = "localhost"
    private val appnavn = "dittnav"

    private val beskjed1 = BeskjedObjectMother.createBeskjed(
        id = 1,
        eventId = "123",
        fodselsnummer = fodselsnummer,
        grupperingsId = grupperingsid,
        synligFremTil = ZonedDateTime.now().plusHours(1),
        forstBehandlet = ZonedDateTime.now(),
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn,
        eksternVarslingInfo = EksternVarslingInfoObjectMother.createEskternVarslingInfo(
            bestilt = true,
            prefererteKanaler = listOf("SMS", "EPOST")
        )
    )

    val doknotStatusForBeskjed1 = DoknotifikasjonStatusDto(
        eventId = beskjed1.eventId,
        status = OVERSENDT.name,
        melding = "melding",
        distribusjonsId = 123L,
        kanaler = "SMS"
    )

    private val beskjed2 = BeskjedObjectMother.createBeskjed(
        id = 2,
        eventId = eventId,
        fodselsnummer = fodselsnummer,
        grupperingsId = grupperingsid,
        synligFremTil = ZonedDateTime.now().plusHours(1),
        forstBehandlet = ZonedDateTime.now().minusDays(5),
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn,
        eksternVarslingInfo = EksternVarslingInfoObjectMother.createEskternVarslingInfo(
            bestilt = true,
            prefererteKanaler = listOf("SMS", "EPOST")
        )
    )

    val doknotStatusForBeskjed2 = DoknotifikasjonStatusDto(
        eventId = beskjed2.eventId,
        status = EksternVarslingStatus.FEILET.name,
        melding = "feilet",
        distribusjonsId = null,
        kanaler = ""
    )

    private val beskjed3 = BeskjedObjectMother.createBeskjed(
        id = 3,
        eventId = "567",
        fodselsnummer = fodselsnummer,
        grupperingsId = grupperingsid,
        synligFremTil = ZonedDateTime.now().plusHours(1),
        forstBehandlet = ZonedDateTime.now().minusDays(15),
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
        forstBehandlet = ZonedDateTime.now().minusDays(25),
        aktiv = true,
        systembruker = "x-dittnav-2",
        namespace = namespace,
        appnavn = "dittnav-2"
    )

    @BeforeAll
    fun `populer testdata`() {
        createBeskjed(listOf(beskjed1, beskjed2, beskjed3, beskjed4))
        createDoknotStatuses(listOf(doknotStatusForBeskjed1, doknotStatusForBeskjed2))
    }

    @AfterAll
    fun `slett testdata`() {
        deleteAllDoknotStatusBeskjed()
        deleteBeskjed(listOf(beskjed1, beskjed2, beskjed3, beskjed4))
    }
    @Test
    fun `Finn alle cachede Beskjed-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getAllBeskjedForFodselsnummer(fodselsnummer) }.size shouldBe 3
        }
    }

    @Test
    fun `Finn kun aktive cachede Beskjed-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery {
                val aktivBeskjedByUser = getAktivBeskjedForFodselsnummer(fodselsnummer)
                aktivBeskjedByUser
            }.size shouldBe 2
        }
    }

    @Test
    fun `deaktiverer beskjeder`() {
        runBlocking {

            database.dbQuery { setBeskjedInaktiv(fodselsnummer, beskjed1.eventId) } shouldBe 1
            database.dbQuery { setBeskjedInaktiv(fodselsnummer, beskjed1.eventId) } shouldBe 1
            database.dbQuery { setBeskjedInaktiv("12345678910", beskjed1.eventId) } shouldBe 0
            database.dbQuery { setBeskjedInaktiv(fodselsnummer, "8879") } shouldBe 0
            database.dbQuery { getAktivBeskjedForFodselsnummer(fodselsnummer) }.size shouldBe 1
        }
    }

    @Test
    fun `Finn kun inaktive cachede Beskjed-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery {
                val inaktivBeskjedByUser = getInaktivBeskjedForFodselsnummer(fodselsnummer)
                inaktivBeskjedByUser
            }.size shouldBe 1
        }
    }

    @Test
    fun `Returnerer tom liste hvis Beskjed-eventer for fodselsnummer ikke finnes`() {
        val brukerSomIkkeFinnes = "0"
        runBlocking {
            database.dbQuery { getAktivBeskjedForFodselsnummer(brukerSomIkkeFinnes) }.shouldBeEmpty()
        }
    }

    @Test
    fun `Returnerer tom liste hvis fodselsnummer er tomt`() {
        val fodselsnummerMangler = ""
        runBlocking {
            database.dbQuery { getAktivBeskjedForFodselsnummer(fodselsnummerMangler) }.shouldBeEmpty()
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for aktive eventer`() {
        runBlocking {
            val beskjed = database.dbQuery { getAktivBeskjedForFodselsnummer(fodselsnummer) }.first()
            beskjed.produsent shouldBe appnavn
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for inaktive eventer`() {
        runBlocking {
            val beskjed = database.dbQuery { getInaktivBeskjedForFodselsnummer(fodselsnummer) }.first()
            beskjed.produsent shouldBe appnavn
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for alle eventer`() {
        runBlocking {
            val beskjed = database.dbQuery { getAllBeskjedForFodselsnummer(fodselsnummer) }.first()
            beskjed.produsent shouldBe appnavn
        }
    }

    @Test
    fun `Finn alle cachede events som matcher fodselsnummer og eventId`() {
        runBlocking {
            database.dbQuery { getBeskjedByIds(fodselsnummer, eventId) }.size shouldBe 1
        }
    }

    @Test
    fun `Returnerer tom liste hvis Beskjed-eventer ikke stemmer med eventId`() {
        runBlocking {
            database.dbQuery { getBeskjedByIds(fodselsnummer, "dummyEventId") }.shouldBeEmpty()
        }
    }

    @Test
    fun `Returnerer tom liste hvis Beskjed-eventer ikke stemmer med fodselsnummer`() {
        val brukerSomIkkeFinnes = "000"
        runBlocking {
            database.dbQuery { getBeskjedByIds(brukerSomIkkeFinnes, eventId) }.shouldBeEmpty()
        }
    }

    @Test
    fun `Returnerer tom liste av Beskjed-eventer hvis fodselsnummer er tomt`() {
        val fodselsnummerMangler = ""
        runBlocking {
            database.dbQuery { getBeskjedByIds(fodselsnummerMangler, eventId) }.shouldBeEmpty()
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte Beskjed-eventer`() {
        runBlocking {
            database.dbQuery {
                getAllGroupedBeskjedEventsByIds(fodselsnummer, grupperingsid, appnavn)
            }.size shouldBe 3
        }
    }

    @Test
    fun `Returnerer en tom liste hvis produsent ikke matcher beskjed-eventet`() {
        val noMatchProdusent = "dummyProdusent"
        runBlocking {
            database.dbQuery {
                getAllGroupedBeskjedEventsByIds(fodselsnummer, grupperingsid, noMatchProdusent)
            }.shouldBeEmpty()
        }
    }

    @Test
    fun `Returnerer en tom liste hvis grupperingsid ikke matcher beskjed-eventet`() {
        val noMatchGrupperingsid = "dummyGrupperingsid"
        runBlocking {
            database.dbQuery {
                getAllGroupedBeskjedEventsByIds(fodselsnummer, noMatchGrupperingsid, appnavn)
            }.shouldBeEmpty()
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte Beskjed-eventer basert paa systembruker`() {
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { getAllGroupedBeskjedEventsBySystemuser() }

            groupedEventsBySystemuser.size shouldBe 2
            groupedEventsBySystemuser[beskjed1.systembruker] shouldBe 3
            groupedEventsBySystemuser[beskjed4.systembruker] shouldBe 1
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte Beskjed-eventer basert paa produsent`() {
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { getAllGroupedBeskjedEventsByProducer() }

            groupedEventsBySystemuser.size shouldBe 2
            groupedEventsBySystemuser.findCountFor(beskjed1.namespace, beskjed1.appnavn) shouldBe 3
            groupedEventsBySystemuser.findCountFor(beskjed4.namespace, beskjed4.appnavn) shouldBe 1
        }
    }

    @Test
    fun `Returnerer riktig info om ekstern varsling dersom status er mottat og oversendt`() = runBlocking {
        val beskjed = database.dbQuery {
            getBeskjedByIds(beskjed1.fodselsnummer, beskjed1.eventId)
        }.first()

        val eksternVarslingInfo = beskjed.eksternVarslingInfo

        eksternVarslingInfo.bestilt shouldBe beskjed1.eksternVarslingInfo.bestilt
        eksternVarslingInfo.prefererteKanaler shouldContainAll beskjed1.eksternVarslingInfo.prefererteKanaler
        eksternVarslingInfo.sendt shouldBe true
        eksternVarslingInfo.sendteKanaler shouldContain doknotStatusForBeskjed1.kanaler
    }

    @Test
    fun `Returnerer riktig info om ekstern varsling dersom status er mottat og feilet`() = runBlocking {
        val beskjed = database.dbQuery {
            getBeskjedByIds(beskjed2.fodselsnummer, beskjed2.eventId)
        }.first()

        val eksternVarslingInfo = beskjed.eksternVarslingInfo

        eksternVarslingInfo.bestilt shouldBe beskjed2.eksternVarslingInfo.bestilt
        eksternVarslingInfo.prefererteKanaler shouldContainAll beskjed2.eksternVarslingInfo.prefererteKanaler
        eksternVarslingInfo.sendt shouldBe false
        eksternVarslingInfo.sendteKanaler.isEmpty() shouldBe true
    }

    @Test
    fun `Returnerer riktig info om ekstern varsling dersom status ikke er mottatt`() = runBlocking {
        val beskjed = database.dbQuery {
            getBeskjedByIds(beskjed3.fodselsnummer, beskjed3.eventId)
        }.first()

        val eksternVarslingInfo = beskjed.eksternVarslingInfo

        eksternVarslingInfo.bestilt shouldBe beskjed3.eksternVarslingInfo.bestilt
        eksternVarslingInfo.prefererteKanaler shouldContainAll beskjed3.eksternVarslingInfo.prefererteKanaler
        eksternVarslingInfo.sendt shouldBe false
        eksternVarslingInfo.sendteKanaler.isEmpty() shouldBe true
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

    private fun createDoknotStatuses(statuses: List<DoknotifikasjonStatusDto>) = runBlocking {
        database.dbQuery {
            statuses.forEach { status ->
                createDoknotStatusBeskjed(status)
            }
        }
    }

    private fun deleteAllDoknotStatusBeskjed() = runBlocking {
        database.dbQuery {
            deleteDoknotStatusBeskjed()
        }
    }
}
