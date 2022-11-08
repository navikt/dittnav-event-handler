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
    private val beskjedTestFnr = "12345678910"
    private val grupperingsid = "100$beskjedTestFnr"
    private val systembruker = "x-dittnav"
    private val namespace = "localhost"
    private val appnavn = "dittnav"


    private val database = LocalPostgresDatabase.cleanDb()

    @BeforeEach
    fun `populer testdata`() {
        database.createBeskjed(
            listOf(
                aktivMedSendtEksternVarsling.beskjed,
                aktivMedFeiletEksternVarsling.beskjed,
                inaktivBeskjedUtenEksternVarsling,
                aktivBeskjedMedAnnetpersonNummer
            )
        )
        database.createDoknotStatuses(
            listOf(
                aktivMedSendtEksternVarsling.doknotStatus,
                aktivMedFeiletEksternVarsling.doknotStatus
            )
        )
    }

    @AfterEach
    fun `slett testdata`() {
        database.deleteAllDoknotStatusBeskjed()
        database.deleteBeskjed(
            listOf(
                aktivMedSendtEksternVarsling.beskjed,
                aktivMedFeiletEksternVarsling.beskjed,
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

            database.dbQuery {
                setBeskjedInaktiv(
                    beskjedTestFnr,
                    aktivMedSendtEksternVarsling.beskjed.eventId
                )
            } shouldBe 1
            database.dbQuery {
                setBeskjedInaktiv(
                    beskjedTestFnr,
                    aktivMedSendtEksternVarsling.beskjed.eventId
                )
            } shouldBe 1
            database.dbQuery { getAktiveBeskjederForFodselsnummer(beskjedTestFnr) }.size shouldBe 1
            database.dbQuery { getInaktiveBeskjederForFodselsnummer(beskjedTestFnr) }.size shouldBe 2
        }
        assertThrows<BeskjedNotFoundException> {
            runBlocking {
                database.dbQuery { setBeskjedInaktiv("9631486855", aktivMedSendtEksternVarsling.beskjed.eventId) }
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
                this[aktivMedSendtEksternVarsling.beskjed.systembruker] shouldBe 3
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
                    aktivMedSendtEksternVarsling.beskjed.namespace,
                    aktivMedSendtEksternVarsling.beskjed.appnavn
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
                getBeskjedById(
                    aktivMedSendtEksternVarsling.beskjed.fodselsnummer,
                    aktivMedSendtEksternVarsling.beskjed.eventId
                )
            }.assert {
                eksternVarslingSendt shouldBe true
                eksternVarslingKanaler shouldBe aktivMedSendtEksternVarsling.beskjed.eksternVarslingKanaler
            }

            database.dbQuery {
                getBeskjedById(
                    aktivMedFeiletEksternVarsling.beskjed.fodselsnummer,
                    aktivMedFeiletEksternVarsling.beskjed.eventId
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

    private val aktivMedSendtEksternVarsling = createBeskjed(
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
    ).medEksternVarsling(sendt = true)

    private val aktivMedFeiletEksternVarsling = createBeskjed(
        id = 2,
        eventId = "887766",
        fodselsnummer = beskjedTestFnr,
        grupperingsId = grupperingsid,
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn,
        eksternVarslingKanaler = emptyList(),
        eksternVarslingSendt = false
    ).medEksternVarsling(sendt = false)

    private val inaktivBeskjedUtenEksternVarsling = createBeskjed(
        id = 3,
        eventId = "567",
        fodselsnummer = beskjedTestFnr,
        grupperingsId = grupperingsid,
        aktiv = false,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn
    )
    private val aktivBeskjedMedAnnetpersonNummer = createBeskjed(
        id = 4,
        eventId = "789",
        fodselsnummer = "54321",
        aktiv = true,
        systembruker = "x-dittnav-2",
        namespace = namespace,
        appnavn = "dittnav-2"
    )
}

