package no.nav.personbruker.dittnav.eventhandler.innboks

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.common.findCountFor
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.DoknotifikasjonStatusDto
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingInfoObjectMother
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingStatus.FEILET
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingStatus.OVERSENDT
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.createDoknotStatusInnboks
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.deleteDoknotStatusInnboks
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDate
import java.time.ZonedDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InnboksQueriesTest {

    private val database = LocalPostgresDatabase.cleanDb()

    private val fodselsnummer1 = "12345"
    private val fodselsnummer2 = "67890"
    private val systembruker = "x-dittnav"
    private val namespace = "localhost"
    private val appnavn = "dittnav"
    private val grupperingsid = "100${fodselsnummer1}"

    private val innboks1 = InnboksObjectMother.createInnboks(
        id = 1,
        eventId = "123",
        fodselsnummer = fodselsnummer1,
        grupperingsId = grupperingsid,
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn,
        eksternVarslingInfo = EksternVarslingInfoObjectMother.createEskternVarslingInfo(
            bestilt = true,
            prefererteKanaler = listOf("SMS", "EPOST")
        )
    )

    val doknotStatusForInnboks1 = DoknotifikasjonStatusDto(
        eventId = innboks1.eventId,
        status = OVERSENDT.name,
        melding = "melding",
        distribusjonsId = 123L,
        kanaler = "SMS"
    )

    private val innboks2 = InnboksObjectMother.createInnboks(
        id = 2,
        eventId = "345",
        fodselsnummer = fodselsnummer1,
        grupperingsId = grupperingsid,
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn,
        eksternVarslingInfo = EksternVarslingInfoObjectMother.createEskternVarslingInfo(
            bestilt = true,
            prefererteKanaler = listOf("SMS", "EPOST")
        )
    )

    val doknotStatusForInnboks2 = DoknotifikasjonStatusDto(
        eventId = innboks2.eventId,
        status = FEILET.name,
        melding = "feilet",
        distribusjonsId = null,
        kanaler = ""
    )

    private val innboks3 = InnboksObjectMother.createInnboks(
        id = 3,
        eventId = "567",
        fodselsnummer = fodselsnummer2,
        aktiv = true,
        systembruker = "x-dittnav-2",
        namespace = namespace,
        appnavn = "dittnav-2",
        forstBehandlet = ZonedDateTime.now().minusDays(5),
    )
    private val innboks4 = InnboksObjectMother.createInnboks(
        id = 4,
        eventId = "789",
        fodselsnummer = fodselsnummer2,
        aktiv = false,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn,
        forstBehandlet = ZonedDateTime.now().minusDays(15),
    )

    @BeforeAll
    fun `populer test-data`() {
        createInnboks(listOf(innboks1, innboks2, innboks3, innboks4))
        createDoknotStatuses(listOf(doknotStatusForInnboks1, doknotStatusForInnboks2))
    }

    @AfterAll
    fun `slett Innboks-eventer fra tabellen`() {
        deleteAllDoknotStatusInnboks()
        deleteInnboks(listOf(innboks1, innboks2, innboks3, innboks4))
    }

    @Test
    fun `Finn alle cachede Innboks-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getAllInnboksForFodselsnummer(fodselsnummer1) }.size shouldBe 2
            database.dbQuery { getAllInnboksForFodselsnummer(fodselsnummer2) }.size shouldBe 2
        }
    }

    @Test
    fun `Finn kun aktive cachede Innboks-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getAktivInnboksForFodselsnummer(fodselsnummer1) }.size shouldBe 2
            database.dbQuery { getAktivInnboksForFodselsnummer(fodselsnummer2) }.size shouldBe 1
        }
    }

    @Test
    fun `Finn kun inaktive cachede Innboks-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getInaktivInnboksForFodselsnummer(fodselsnummer1) }.shouldBeEmpty()
            database.dbQuery { getInaktivInnboksForFodselsnummer(fodselsnummer2) }.size shouldBe 1
        }
    }

    @Test
    fun `Returnerer tom liste hvis Innboks-eventer for fodselsnummer ikke finnes`() {
        val brukerUtenEventer = "0"
        runBlocking {
            database.dbQuery { getAllInnboksForFodselsnummer(brukerUtenEventer) }.size shouldBe 0
        }
    }

    @Test
    fun `Returnerer tom liste hvis fodselsnummer er tomt`() {
        val brukerUtenEventer = ""
        runBlocking {
            database.dbQuery { getAllInnboksForFodselsnummer(brukerUtenEventer) }.size shouldBe 0
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for aktive eventer`() {
        runBlocking {
            val innboks = database.dbQuery { getAktivInnboksForFodselsnummer(fodselsnummer1) }.first()
            innboks.produsent shouldBe appnavn
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for inaktive eventer`() {
        runBlocking {
            val innboks = database.dbQuery { getInaktivInnboksForFodselsnummer(fodselsnummer2) }.first()
            innboks.produsent shouldBe appnavn
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for alle eventer`() {
        runBlocking {
            val innboks = database.dbQuery { getAllInnboksForFodselsnummer(fodselsnummer1) }.first()
            innboks.produsent shouldBe appnavn
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte Innboks-eventer`() {
        runBlocking {
            database.dbQuery {
                getAllGroupedInnboksEventsByIds(fodselsnummer1, grupperingsid, appnavn)
            }.size shouldBe 2
        }
    }

    @Test
    fun `Returnerer en tom liste hvis produsent ikke matcher innboks-eventet`() {
        val noMatchProdusent = "dummyProdusent"
        runBlocking {
            database.dbQuery {
                getAllGroupedInnboksEventsByIds(fodselsnummer1, grupperingsid, noMatchProdusent)
            }.shouldBeEmpty()
        }
    }

    @Test
    fun `Returnerer en tom liste hvis grupperingsid ikke matcher innboks-eventet`() {
        val noMatchGrupperingsid = "dummyGrupperingsid"
        runBlocking {
            database.dbQuery {
                getAllGroupedInnboksEventsByIds(fodselsnummer1, noMatchGrupperingsid, appnavn)
            }.shouldBeEmpty()
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte innboks-eventer basert paa systembruker`() {
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { getAllGroupedInnboksEventsBySystemuser() }

            groupedEventsBySystemuser.size shouldBe 2
            groupedEventsBySystemuser[innboks1.systembruker] shouldBe 3
            groupedEventsBySystemuser[innboks3.systembruker] shouldBe 1
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte innboks-eventer basert paa produsent`() {
        runBlocking {
            val groupedEventsByProducer = database.dbQuery { getAllGroupedInnboksEventsByProducer() }

            groupedEventsByProducer.size shouldBe 2
            groupedEventsByProducer.findCountFor(innboks1.namespace, innboks1.appnavn) shouldBe 3
            groupedEventsByProducer.findCountFor(innboks3.namespace, innboks3.appnavn) shouldBe 1
        }
    }


    @Test
    fun `Returnerer kun eventer der forstBehandlet er nyere enn bestemt dato for aktive eventer`() {
        runBlocking {
            val recentEventsForFnr = database.dbQuery {
                getRecentAktivInnboksForFodselsnummer(fodselsnummer2, LocalDate.now().minusDays(10))
            }

            recentEventsForFnr.size shouldBe 1
            recentEventsForFnr.map { it.id } shouldContainAll listOf(3)
        }
    }

    @Test
    fun `Returnerer kun eventer der forstBehandlet er nyere enn bestemt dato for inaktive eventer`() {
        runBlocking {
            val recentEventsForFnr = database.dbQuery {
                getRecentInaktivInnboksForFodselsnummer(fodselsnummer2, LocalDate.now().minusDays(10))
            }

            recentEventsForFnr.size shouldBe 0
        }
    }

    @Test
    fun `Returnerer kun eventer der forstBehandlet er nyere enn bestemt dato for alle eventer`() {
        runBlocking {
            val recentEventsForFnr = database.dbQuery {
                getAllRecentInnboksForFodselsnummer(fodselsnummer2, LocalDate.now().minusDays(20))
            }

            recentEventsForFnr.size shouldBe 2
            recentEventsForFnr.map { it.id } shouldContainAll listOf(3, 4)
        }
    }

    @Test
    fun `Returnerer riktig info om ekstern varsling dersom status er mottat og oversendt`() = runBlocking {
        val innboks = database.dbQuery {
            getAktivInnboksForFodselsnummer(innboks1.fodselsnummer)
        }.filter {
            it.eventId == innboks1.eventId
        }.first()

        val eksternVarslingInfo = innboks.eksternVarslingInfo

        eksternVarslingInfo.bestilt shouldBe innboks1.eksternVarslingInfo.bestilt
        eksternVarslingInfo.prefererteKanaler shouldContainAll  innboks1.eksternVarslingInfo.prefererteKanaler
        eksternVarslingInfo.sendt shouldBe true
        eksternVarslingInfo.sendteKanaler shouldContain doknotStatusForInnboks1.kanaler
    }

    @Test
    fun `Returnerer riktig info om ekstern varsling dersom status er mottat og feilet`() = runBlocking {
        val innboks = database.dbQuery {
            getAktivInnboksForFodselsnummer(innboks2.fodselsnummer)
        }.filter {
            it.eventId == innboks2.eventId
        }.first()

        val eksternVarslingInfo = innboks.eksternVarslingInfo

        eksternVarslingInfo.bestilt shouldBe innboks2.eksternVarslingInfo.bestilt
        eksternVarslingInfo.prefererteKanaler shouldContainAll  innboks2.eksternVarslingInfo.prefererteKanaler
        eksternVarslingInfo.sendt shouldBe false
        eksternVarslingInfo.sendteKanaler.isEmpty() shouldBe true
    }

    @Test
    fun `Returnerer riktig info om ekstern varsling dersom status ikke er mottatt`() = runBlocking {
        val innboks = database.dbQuery {
            getAktivInnboksForFodselsnummer(innboks3.fodselsnummer)
        }.filter {
            it.eventId == innboks3.eventId
        }.first()

        val eksternVarslingInfo = innboks.eksternVarslingInfo

        eksternVarslingInfo.bestilt shouldBe innboks3.eksternVarslingInfo.bestilt
        eksternVarslingInfo.prefererteKanaler shouldContainAll  innboks3.eksternVarslingInfo.prefererteKanaler
        eksternVarslingInfo.sendt shouldBe false
        eksternVarslingInfo.sendteKanaler.isEmpty() shouldBe true
    }

    private fun createInnboks(innboks: List<Innboks>) {
        runBlocking {
            database.dbQuery { createInnboks(innboks) }
        }
    }

    private fun deleteInnboks(innboks: List<Innboks>) {
        runBlocking {
            database.dbQuery { deleteInnboks(innboks) }
        }
    }

    private fun createDoknotStatuses(statuses: List<DoknotifikasjonStatusDto>) = runBlocking {
        database.dbQuery {
            statuses.forEach { status ->
                createDoknotStatusInnboks(status)
            }
        }
    }

    private fun deleteAllDoknotStatusInnboks() = runBlocking {
        database.dbQuery {
            deleteDoknotStatusInnboks()
        }
    }
}
