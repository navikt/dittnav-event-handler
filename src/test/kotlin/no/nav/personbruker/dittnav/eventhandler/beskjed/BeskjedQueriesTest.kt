package no.nav.personbruker.dittnav.eventhandler.beskjed

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.appnavn
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.beskjed1Aktiv
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.beskjed2Aktiv
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.beskjed3Inaktiv
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.beskjed4Aktiv
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.beskjedTestFnr
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.doknotStatusForBeskjed1
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.doknotStatusForBeskjed2
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.eventId
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.grupperingsid
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.common.findCountFor
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BeskjedQueriesTest {

    private val database = LocalPostgresDatabase.cleanDb()

    @BeforeAll
    fun `populer testdata`() {
        database.createBeskjed(listOf(beskjed1Aktiv, beskjed2Aktiv, beskjed3Inaktiv, beskjed4Aktiv))
        database.createDoknotStatuses(listOf(doknotStatusForBeskjed1, doknotStatusForBeskjed2))
    }

    @AfterAll
    fun `slett testdata`() {
        database.deleteAllDoknotStatusBeskjed()
        database.deleteBeskjed(listOf(beskjed1Aktiv, beskjed2Aktiv, beskjed3Inaktiv, beskjed4Aktiv))
    }

    @Test
    fun `Finn alle cachede Beskjed-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getAllBeskjedForFodselsnummer(beskjedTestFnr) }.size shouldBe 3
        }
    }

    @Test
    fun `Finn kun aktive cachede Beskjed-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery {
                val aktivBeskjedByUser = getAktivBeskjedForFodselsnummer(beskjedTestFnr)
                aktivBeskjedByUser
            }.size shouldBe 2
        }
    }

    @Test
    fun `deaktiverer beskjeder`() {
        runBlocking {

            database.dbQuery { setBeskjedInaktiv(beskjedTestFnr, beskjed1Aktiv.eventId) } shouldBe 1
            database.dbQuery { setBeskjedInaktiv(beskjedTestFnr, beskjed1Aktiv.eventId) } shouldBe 1
            database.dbQuery { getAktivBeskjedForFodselsnummer(beskjedTestFnr) }.size shouldBe 1
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
    fun `Finn kun inaktive cachede Beskjed-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery {
                val inaktivBeskjedByUser = getInaktivBeskjedForFodselsnummer(beskjedTestFnr)
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
            val beskjed = database.dbQuery { getAktivBeskjedForFodselsnummer(beskjedTestFnr) }.first()
            beskjed.produsent shouldBe appnavn
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for inaktive eventer`() {
        runBlocking {
            val beskjed = database.dbQuery { getInaktivBeskjedForFodselsnummer(beskjedTestFnr) }.first()
            beskjed.produsent shouldBe appnavn
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for alle eventer`() {
        runBlocking {
            val beskjed = database.dbQuery { getAllBeskjedForFodselsnummer(beskjedTestFnr) }.first()
            beskjed.produsent shouldBe appnavn
        }
    }

    @Test
    fun `Finn alle cachede events som matcher fodselsnummer og eventId`() {
        runBlocking {
            database.dbQuery { getBeskjedByIds(beskjedTestFnr, eventId) }.size shouldBe 1
        }
    }

    @Test
    fun `Returnerer tom liste hvis Beskjed-eventer ikke stemmer med eventId`() {
        runBlocking {
            database.dbQuery { getBeskjedByIds(beskjedTestFnr, "dummyEventId") }.shouldBeEmpty()
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
                getAllGroupedBeskjedEventsByIds(beskjedTestFnr, grupperingsid, appnavn)
            }.size shouldBe 3
        }
    }

    @Test
    fun `Returnerer en tom liste hvis produsent ikke matcher beskjed-eventet`() {
        val noMatchProdusent = "dummyProdusent"
        runBlocking {
            database.dbQuery {
                getAllGroupedBeskjedEventsByIds(beskjedTestFnr, grupperingsid, noMatchProdusent)
            }.shouldBeEmpty()
        }
    }

    @Test
    fun `Returnerer en tom liste hvis grupperingsid ikke matcher beskjed-eventet`() {
        val noMatchGrupperingsid = "dummyGrupperingsid"
        runBlocking {
            database.dbQuery {
                getAllGroupedBeskjedEventsByIds(beskjedTestFnr, noMatchGrupperingsid, appnavn)
            }.shouldBeEmpty()
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
        val beskjed = database.dbQuery {
            getBeskjedByIds(beskjed1Aktiv.fodselsnummer, beskjed1Aktiv.eventId)
        }.first()

        val eksternVarslingInfo = beskjed.eksternVarslingInfo

        eksternVarslingInfo.bestilt shouldBe beskjed1Aktiv.eksternVarslingInfo.bestilt
        eksternVarslingInfo.prefererteKanaler shouldContainAll beskjed1Aktiv.eksternVarslingInfo.prefererteKanaler
        eksternVarslingInfo.sendt shouldBe true
        eksternVarslingInfo.sendteKanaler shouldContain doknotStatusForBeskjed1.kanaler
    }

    @Test
    fun `Returnerer riktig info om ekstern varsling dersom status er mottat og feilet`() = runBlocking {
        val beskjed = database.dbQuery {
            getBeskjedByIds(beskjed2Aktiv.fodselsnummer, beskjed2Aktiv.eventId)
        }.first()

        val eksternVarslingInfo = beskjed.eksternVarslingInfo

        eksternVarslingInfo.bestilt shouldBe beskjed2Aktiv.eksternVarslingInfo.bestilt
        eksternVarslingInfo.prefererteKanaler shouldContainAll beskjed2Aktiv.eksternVarslingInfo.prefererteKanaler
        eksternVarslingInfo.sendt shouldBe false
        eksternVarslingInfo.sendteKanaler.isEmpty() shouldBe true
    }

    @Test
    fun `Returnerer riktig info om ekstern varsling dersom status ikke er mottatt`() = runBlocking {
        val beskjed = database.dbQuery {
            getBeskjedByIds(beskjed3Inaktiv.fodselsnummer, beskjed3Inaktiv.eventId)
        }.first()

        val eksternVarslingInfo = beskjed.eksternVarslingInfo

        eksternVarslingInfo.bestilt shouldBe beskjed3Inaktiv.eksternVarslingInfo.bestilt
        eksternVarslingInfo.prefererteKanaler shouldContainAll beskjed3Inaktiv.eksternVarslingInfo.prefererteKanaler
        eksternVarslingInfo.sendt shouldBe false
        eksternVarslingInfo.sendteKanaler.isEmpty() shouldBe true
    }
}
