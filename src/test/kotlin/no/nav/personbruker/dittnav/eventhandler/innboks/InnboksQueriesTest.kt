package no.nav.personbruker.dittnav.eventhandler.innboks

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.common.findCountFor
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InnboksQueriesTest {

    private val database = LocalPostgresDatabase.cleanDb()

    @BeforeAll
    fun `populer test-data`() {
        database.createInnboks(listOf(innboks1Aktiv, innboks2Aktiv, innboks3Aktiv, innboks4Inaktiv))
    }

    @AfterAll
    fun `slett Innboks-eventer fra tabellen`() {
        database.deleteInnboks()
    }

    @Test
    fun `Finn alle cachede Innboks-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getAllInnboksForFodselsnummer(innboksTestFnr1) }.size shouldBe 2
            database.dbQuery { getAllInnboksForFodselsnummer(innboksTestFnr2) }.size shouldBe 2
        }
    }

    @Test
    fun `Finn kun aktive cachede Innboks-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getAktivInnboksForFodselsnummer(innboksTestFnr1) }.size shouldBe 2
            database.dbQuery { getAktivInnboksForFodselsnummer(innboksTestFnr2) }.size shouldBe 1
        }
    }

    @Test
    fun `Finn kun inaktive cachede Innboks-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getInaktivInnboksForFodselsnummer(innboksTestFnr1) }.shouldBeEmpty()
            database.dbQuery { getInaktivInnboksForFodselsnummer(innboksTestFnr2) }.size shouldBe 1
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
            val innboks = database.dbQuery { getAktivInnboksForFodselsnummer(innboksTestFnr1) }.first()
            innboks.produsent shouldBe innboksTestAppnavn
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for inaktive eventer`() {
        runBlocking {
            val innboks = database.dbQuery { getInaktivInnboksForFodselsnummer(innboksTestFnr2) }.first()
            innboks.produsent shouldBe innboksTestAppnavn
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for alle eventer`() {
        runBlocking {
            val innboks = database.dbQuery { getAllInnboksForFodselsnummer(innboksTestFnr1) }.first()
            innboks.produsent shouldBe innboksTestAppnavn
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte Innboks-eventer`() {
        runBlocking {
            database.dbQuery {
                getAllGroupedInnboksEventsByIds(innboksTestFnr1, innboksTestgrupperingsid, innboksTestAppnavn)
            }.size shouldBe 2
        }
    }

    @Test
    fun `Returnerer en tom liste hvis produsent ikke matcher innboks-eventet`() {
        val noMatchProdusent = "dummyProdusent"
        runBlocking {
            database.dbQuery {
                getAllGroupedInnboksEventsByIds(innboksTestFnr1, innboksTestgrupperingsid, noMatchProdusent)
            }.shouldBeEmpty()
        }
    }

    @Test
    fun `Returnerer en tom liste hvis grupperingsid ikke matcher innboks-eventet`() {
        val noMatchGrupperingsid = "dummyGrupperingsid"
        runBlocking {
            database.dbQuery {
                getAllGroupedInnboksEventsByIds(innboksTestFnr1, noMatchGrupperingsid, innboksTestAppnavn)
            }.shouldBeEmpty()
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte innboks-eventer basert paa systembruker`() {
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { getAllGroupedInnboksEventsBySystemuser() }

            groupedEventsBySystemuser.size shouldBe 2
            groupedEventsBySystemuser[innboks1Aktiv.systembruker] shouldBe 3
            groupedEventsBySystemuser[innboks3Aktiv.systembruker] shouldBe 1
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte innboks-eventer basert paa produsent`() {
        runBlocking {
            val groupedEventsByProducer = database.dbQuery { getAllGroupedInnboksEventsByProducer() }

            groupedEventsByProducer.size shouldBe 2
            groupedEventsByProducer.findCountFor(innboks1Aktiv.namespace, innboks1Aktiv.appnavn) shouldBe 3
            groupedEventsByProducer.findCountFor(innboks3Aktiv.namespace, innboks3Aktiv.appnavn) shouldBe 1
        }
    }

    @Test
    fun `Returnerer riktig info om ekstern varsling dersom status er mottat og oversendt`() = runBlocking {
        val innboks = database.dbQuery {
            getAktivInnboksForFodselsnummer(innboks1Aktiv.fodselsnummer)
        }.filter {
            it.eventId == innboks1Aktiv.eventId
        }.first()

        val eksternVarslingInfo = innboks.eksternVarsling

        eksternVarslingInfo shouldNotBe null
        eksternVarslingInfo!!.prefererteKanaler shouldContainAll innboks1Aktiv.eksternVarsling!!.prefererteKanaler
        eksternVarslingInfo.sendt shouldBe true
        eksternVarslingInfo.sendteKanaler shouldContainAll innboks1Aktiv.eksternVarsling!!.sendteKanaler
    }

    @Test
    fun `Returnerer riktig info om ekstern varsling dersom status er mottat og feilet`() = runBlocking {
        val innboks = database.dbQuery {
            getAktivInnboksForFodselsnummer(innboks2Aktiv.fodselsnummer)
        }.filter {
            it.eventId == innboks2Aktiv.eventId
        }.first()

        val eksternVarslingInfo = innboks.eksternVarsling

        eksternVarslingInfo shouldNotBe null
        eksternVarslingInfo!!.prefererteKanaler shouldContainAll innboks2Aktiv.eksternVarsling!!.prefererteKanaler
        eksternVarslingInfo.sendt shouldBe false
        eksternVarslingInfo.sendteKanaler shouldContainAll innboks2Aktiv.eksternVarsling!!.sendteKanaler
    }

    @Test
    fun `Returnerer riktig info om ekstern varsling dersom status ikke er mottatt`() = runBlocking {
        val innboks = database.dbQuery {
            getAktivInnboksForFodselsnummer(innboks3Aktiv.fodselsnummer)
        }.filter {
            it.eventId == innboks3Aktiv.eventId
        }.first()

        val eksternVarslingInfo = innboks.eksternVarsling

        eksternVarslingInfo shouldBe null
    }
}
