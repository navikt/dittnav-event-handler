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
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.common.findCountFor
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BeskjedQueriesTest {

    private val database = LocalPostgresDatabase.cleanDb()

    @BeforeAll
    fun `populer testdata`() {
        database.createBeskjed(listOf(beskjed1Aktiv, beskjed2Aktiv, beskjed3Inaktiv, beskjed4Aktiv))
    }

    @AfterAll
    fun `slett testdata`() {
        database.deleteBeskjed()
    }

    @Test
    fun `Alle beskjed-varsler for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getAllBeskjedForFodselsnummer(beskjedTestFnr) }.size shouldBe 3
        }
    }

    @Test
    fun `Aktive beskjed-varsler for fodselsnummerr`() {
        runBlocking {
            database.dbQuery {
                val aktivBeskjedByUser = getAktivBeskjedForFodselsnummer(beskjedTestFnr)
                aktivBeskjedByUser
            }.size shouldBe 2
        }
    }

    @Test
    fun `Inaktive beskjed-varsler for fodselsnummer`() {
        runBlocking {
            database.dbQuery {
                val inaktivBeskjedByUser = getInaktivBeskjedForFodselsnummer(beskjedTestFnr)
                inaktivBeskjedByUser
            }.size shouldBe 1
        }
    }

    @Test
    fun `Tom liste hvis det ikke finnes beskjeder knyttet til fødelsnummeret`() {
        val brukerSomIkkeFinnes = "0"
        runBlocking {
            database.dbQuery { getAktivBeskjedForFodselsnummer(brukerSomIkkeFinnes) }.shouldBeEmpty()
            database.dbQuery { getAktivBeskjedForFodselsnummer("") }.shouldBeEmpty()
        }
    }


    @Test
    fun `Lesbart navn for produsent som kan eksponeres for aktive eventer`() {
        runBlocking {
            database.dbQuery { getAktivBeskjedForFodselsnummer(beskjedTestFnr) }.first()
                .produsent shouldBe appnavn
        }
    }

    @Test
    fun `Lesbart navn for produsent som kan eksponeres for inaktive eventer`() {
        runBlocking {
            database.dbQuery { getInaktivBeskjedForFodselsnummer(beskjedTestFnr) }.first()
                .produsent shouldBe appnavn
        }
    }

    @Test
    fun `Lesbart navn for produsent som kan eksponeres for alle eventer`() {
        runBlocking {
            database.dbQuery { getAllBeskjedForFodselsnummer(beskjedTestFnr) }.first()
                .produsent shouldBe appnavn
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte Beskjed-eventer basert paa systembruker`() {
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { getAllGroupedBeskjedEventsBySystemuser() }

            groupedEventsBySystemuser.size shouldBe 2
            groupedEventsBySystemuser[beskjed1Aktiv.systembruker] shouldBe 3
            groupedEventsBySystemuser[beskjed4Aktiv.systembruker] shouldBe 1
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte Beskjed-eventer basert paa produsent`() {
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { getAllGroupedBeskjedEventsByProducer() }

            groupedEventsBySystemuser.size shouldBe 2
            groupedEventsBySystemuser.findCountFor(beskjed1Aktiv.namespace, beskjed1Aktiv.appnavn) shouldBe 3
            groupedEventsBySystemuser.findCountFor(beskjed4Aktiv.namespace, beskjed4Aktiv.appnavn) shouldBe 1
        }
    }

    @Test
    fun `Returnerer riktig info om ekstern varsling dersom status er mottat og oversendt`() = runBlocking {
        database.dbQuery {
            getAllBeskjedForFodselsnummer(beskjed1Aktiv.fodselsnummer)
        }.first().assert {
            eksternVarslingSendt shouldBe true
            eksternVarslingKanaler shouldBe beskjed1Aktiv.eksternVarslingKanaler
        }


    }

    @Test
    fun `Ekstern varsling dersom status er mottat og feilet`() = runBlocking {
        database.dbQuery {
            getAllBeskjedForFodselsnummer(beskjed2Aktiv.fodselsnummer)
        }.first().assert {
            eksternVarslingSendt shouldBe false
            eksternVarslingKanaler shouldBe emptyList()
        }
    }

    @Test
    fun `Ekstern varsling dersom status ikke er mottatt`() = runBlocking {
        database.dbQuery {
            getAllBeskjedForFodselsnummer(beskjed3Inaktiv.fodselsnummer)
        }.first().assert {
            eksternVarslingSendt shouldBe false
            eksternVarslingKanaler shouldBe emptyList()
        }
    }
}

