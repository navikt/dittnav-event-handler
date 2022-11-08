package no.nav.personbruker.dittnav.eventhandler.beskjed

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.assert
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.appnavn
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.beskjed1Aktiv
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.beskjed2Aktiv
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.beskjed3Inaktiv
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.beskjed4Aktiv
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.beskjedTestFnr
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.doknotStatusForBeskjed1
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.doknotStatusForBeskjed2
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.grupperingsid
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.common.findCountFor
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BeskjedQueriesTest {

    private val database = LocalPostgresDatabase.cleanDb()

    @BeforeEach
    fun `populer testdata`() {
        database.createBeskjed(listOf(beskjed1Aktiv, beskjed2Aktiv, beskjed3Inaktiv, beskjed4Aktiv))
        database.createDoknotStatuses(listOf(doknotStatusForBeskjed1, doknotStatusForBeskjed2))
    }

    @AfterEach
    fun `slett testdata`() {
        database.deleteAllDoknotStatusBeskjed()
        database.deleteBeskjed(listOf(beskjed1Aktiv, beskjed2Aktiv, beskjed3Inaktiv, beskjed4Aktiv))
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

            database.dbQuery { setBeskjedInaktiv(beskjedTestFnr, beskjed1Aktiv.eventId) } shouldBe 1
            database.dbQuery { setBeskjedInaktiv(beskjedTestFnr, beskjed1Aktiv.eventId) } shouldBe 1
            database.dbQuery { getAktiveBeskjederForFodselsnummer(beskjedTestFnr) }.size shouldBe 1
            database.dbQuery { getInaktiveBeskjederForFodselsnummer(beskjedTestFnr) }.size shouldBe 2
        }
        assertThrows<BeskjedNotFoundException> {
            runBlocking {
                database.dbQuery { setBeskjedInaktiv("9631486855", beskjed1Aktiv.eventId) }
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
                this[beskjed1Aktiv.systembruker] shouldBe 3
                this[beskjed4Aktiv.systembruker] shouldBe 1
            }
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte Beskjed-eventer basert paa produsent`() = runBlocking {
        database.dbQuery { getAllGroupedBeskjedEventsByProducer() }.assert {
            size shouldBe 2
            findCountFor(beskjed1Aktiv.namespace, beskjed1Aktiv.appnavn) shouldBe 3
            findCountFor(beskjed4Aktiv.namespace, beskjed4Aktiv.appnavn) shouldBe 1
        }
    }

    @Test
    fun `Riktig ekstern varsling info`() = runBlocking {
        database.dbQuery {
            getBeskjedById(beskjed1Aktiv.fodselsnummer, beskjed1Aktiv.eventId)
        }.assert {
            eksternVarslingSendt shouldBe true
            eksternVarslingKanaler shouldBe beskjed1Aktiv.eksternVarslingKanaler
        }

        database.dbQuery { getBeskjedById(beskjed2Aktiv.fodselsnummer, beskjed2Aktiv.eventId)
        }.assert {
            eksternVarslingSendt shouldBe false
            eksternVarslingKanaler shouldBe emptyList()
        }

        database.dbQuery { getBeskjedById(beskjed3Inaktiv.fodselsnummer, beskjed3Inaktiv.eventId) }
            .assert {
                eksternVarslingSendt shouldBe false
                eksternVarslingKanaler shouldBe emptyList()
            }

    }

}

