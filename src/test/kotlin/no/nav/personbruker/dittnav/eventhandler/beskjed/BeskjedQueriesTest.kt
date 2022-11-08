package no.nav.personbruker.dittnav.eventhandler.beskjed

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.OsloDateTime
import no.nav.personbruker.dittnav.eventhandler.assert
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.common.findCountFor
import no.nav.personbruker.dittnav.eventhandler.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.DoknotifikasjonTestStatus
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingStatus
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BeskjedQueriesTest {

    private val database = LocalPostgresDatabase.cleanDb()
    private val beskjedTestFnr = "12345678910"
    internal val grupperingsid = "100$beskjedTestFnr"
    internal val systembruker = "x-dittnav"
    internal val namespace = "localhost"
    internal val appnavn = "dittnav"


    @BeforeEach
    fun `populer testdata`() {
        database.createBeskjed(
            listOf(
                aktivBeskjedMedEksternVarsling,
                aktivBeskjedMedFeiletInternVarsling,
                inaktivBeskjedUtenEksternVarsling,
                aktivBeskjedMedAnnetpersonNummer
            )
        )
        database.createDoknotStatuses(listOf(doknotStatusForBeskjed1, doknotStatusForBeskjed2))
    }

    @AfterEach
    fun `slett testdata`() {
        database.deleteAllDoknotStatusBeskjed()
        database.deleteBeskjed(
            listOf(
                aktivBeskjedMedEksternVarsling,
                aktivBeskjedMedFeiletInternVarsling,
                inaktivBeskjedUtenEksternVarsling,
                aktivBeskjedMedAnnetpersonNummer
            )
        )
    }

    @Test
    fun `Beskjeder for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getAllBeskjederForFodselsnummer(beskjedTestFnr) }.size shouldBe 3
            database.dbQuery { getAktiveBeskjederForFodselsnummer(beskjedTestFnr) }.size shouldBe 2
            database.dbQuery { getInaktiveBeskjederForFodselsnummer(beskjedTestFnr) }.size shouldBe 1
        }
    }

    @Test
    fun `Tom liste hvis det ikke finnes beskjeder knyttet til fødelsnummeret`() {
        val brukerSomIkkeFinnes = "0"
        runBlocking {
            database.dbQuery { getAktiveBeskjederForFodselsnummer(brukerSomIkkeFinnes) }.shouldBeEmpty()
            database.dbQuery { getAktiveBeskjederForFodselsnummer("") }.shouldBeEmpty()
        }
    }


    @Test
    fun `Deaktiverer beskjeder`() {
        runBlocking {

            database.dbQuery { setBeskjedInaktiv(beskjedTestFnr, aktivBeskjedMedEksternVarsling.eventId) } shouldBe 1
            database.dbQuery { setBeskjedInaktiv(beskjedTestFnr, aktivBeskjedMedEksternVarsling.eventId) } shouldBe 1
            database.dbQuery { getAktiveBeskjederForFodselsnummer(beskjedTestFnr) }.size shouldBe 1
            database.dbQuery { getInaktiveBeskjederForFodselsnummer(beskjedTestFnr) }.size shouldBe 2
        }
        assertThrows<BeskjedNotFoundException> {
            runBlocking {
                database.dbQuery { setBeskjedInaktiv("9631486855", aktivBeskjedMedEksternVarsling.eventId) }
            }
        }
        assertThrows<BeskjedNotFoundException> {
            runBlocking {
                database.dbQuery { setBeskjedInaktiv(beskjedTestFnr, "8879") } shouldBe 0
            }
        }
    }

    @Test
    fun `Lesbart navn for produsent`() {
        runBlocking {
            database.dbQuery { getAktiveBeskjederForFodselsnummer(beskjedTestFnr) }.first()
                .produsent shouldBe appnavn
            database.dbQuery { getInaktiveBeskjederForFodselsnummer(beskjedTestFnr) }.first()
                .produsent shouldBe appnavn
            database.dbQuery { getAllBeskjederForFodselsnummer(beskjedTestFnr) }.first()
                .produsent shouldBe appnavn
        }
    }

    @Test
    fun `Liste av alle grupperte beskjeder`() {
        runBlocking {
            database.dbQuery {
                getAllGroupedBeskjederByGrupperingsId(beskjedTestFnr, grupperingsid, appnavn)
            }.size shouldBe 3
        }
    }

    @Test
    fun `Tom liste hvis produsent ikke matcher beskjed-eventet`() {
        val noMatchProdusent = "dummyProdusent"
        runBlocking {
            database.dbQuery {
                getAllGroupedBeskjederByGrupperingsId(beskjedTestFnr, grupperingsid, noMatchProdusent)
            }.shouldBeEmpty()
        }
    }

    @Test
    fun `Tom liste hvis grupperingsid ikke matcher beskjed-eventet`() {
        runBlocking {
            database.dbQuery {
                getAllGroupedBeskjederByGrupperingsId(beskjedTestFnr, "dummyGrupperingsid", appnavn)
            }.shouldBeEmpty()
        }
    }

    @Test
    fun `Liste av alle grupperte Beskjed-eventer basert paa systembruker`() {
        runBlocking {
            database.dbQuery { getAllGroupedBeskjederBySystemuser() }.assert {
                size shouldBe 2
                this[aktivBeskjedMedEksternVarsling.systembruker] shouldBe 3
                this[aktivBeskjedMedAnnetpersonNummer.systembruker] shouldBe 1
            }
        }
    }

    @Test
    fun `Alle grupperte Beskjed-eventer basert paa produsent`() {
        runBlocking {
            database.dbQuery { getAllGroupedBeskjedEventsByProducer() }.assert {
                size shouldBe 2
                findCountFor(
                    aktivBeskjedMedEksternVarsling.namespace,
                    aktivBeskjedMedEksternVarsling.appnavn
                ) shouldBe 3
                findCountFor(
                    aktivBeskjedMedAnnetpersonNummer.namespace,
                    aktivBeskjedMedAnnetpersonNummer.appnavn
                ) shouldBe 1
            }
        }
    }

    @Test
    fun `Riktig ekstern varsling info`() {
        runBlocking {
            database.dbQuery {
                getBeskjedById(aktivBeskjedMedEksternVarsling.fodselsnummer, aktivBeskjedMedEksternVarsling.eventId)
            }.assert {
                eksternVarslingSendt shouldBe true
                eksternVarslingKanaler shouldBe aktivBeskjedMedEksternVarsling.eksternVarslingKanaler
            }

            database.dbQuery {
                getBeskjedById(
                    aktivBeskjedMedFeiletInternVarsling.fodselsnummer,
                    aktivBeskjedMedFeiletInternVarsling.eventId
                )
            }.assert {
                eksternVarslingSendt shouldBe false
                eksternVarslingKanaler shouldBe emptyList()
            }

            database.dbQuery {
                getBeskjedById(
                    inaktivBeskjedUtenEksternVarsling.fodselsnummer,
                    inaktivBeskjedUtenEksternVarsling.eventId
                )
            }
                .assert {
                    eksternVarslingSendt shouldBe false
                    eksternVarslingKanaler shouldBe emptyList()
                }

        }
    }

    private val aktivBeskjedMedEksternVarsling = createBeskjed(
        id = 1,
        eventId = "123",
        fodselsnummer = beskjedTestFnr,
        grupperingsId = grupperingsid,
        synligFremTil = OsloDateTime.now().plusHours(1),
        forstBehandlet = OsloDateTime.now(),
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn,
        eksternVarslingSendt = true,
        eksternVarslingKanaler = listOf("SMS", "EPOST")

    )

    private val doknotStatusForBeskjed1 = DoknotifikasjonTestStatus(
        eventId = aktivBeskjedMedEksternVarsling.eventId,
        status = EksternVarslingStatus.OVERSENDT.name,
        melding = "melding",
        distribusjonsId = 123L,
        kanaler = "SMS,EPOST"
    )

    private val aktivBeskjedMedFeiletInternVarsling = createBeskjed(
        id = 2,
        eventId = "887766",
        fodselsnummer = beskjedTestFnr,
        grupperingsId = grupperingsid,
        synligFremTil = OsloDateTime.now().plusHours(1),
        forstBehandlet = OsloDateTime.now().minusDays(5),
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn,
        eksternVarslingKanaler = emptyList(),
        eksternVarslingSendt = false
    )

    private val doknotStatusForBeskjed2 = DoknotifikasjonTestStatus(
        eventId = aktivBeskjedMedFeiletInternVarsling.eventId,
        status = EksternVarslingStatus.FEILET.name,
        melding = "feilet",
        distribusjonsId = null,
        kanaler = ""
    )

    private val inaktivBeskjedUtenEksternVarsling = createBeskjed(
        id = 3,
        eventId = "567",
        fodselsnummer = beskjedTestFnr,
        grupperingsId = grupperingsid,
        synligFremTil = OsloDateTime.now().plusHours(1),
        forstBehandlet = OsloDateTime.now().minusDays(15),
        aktiv = false,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn
    )
    private val aktivBeskjedMedAnnetpersonNummer = createBeskjed(
        id = 4,
        eventId = "789",
        fodselsnummer = "54321",
        synligFremTil = OsloDateTime.now().plusHours(1),
        forstBehandlet = OsloDateTime.now().minusDays(25),
        aktiv = true,
        systembruker = "x-dittnav-2",
        namespace = namespace,
        appnavn = "dittnav-2"
    )

}

