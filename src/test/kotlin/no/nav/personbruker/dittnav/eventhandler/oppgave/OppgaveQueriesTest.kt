package no.nav.personbruker.dittnav.eventhandler.oppgave

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.common.findCountFor
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveTestData.appnavn
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveTestData.grupperingsid
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveTestData.oppgave1Aktiv
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveTestData.oppgave2Aktiv
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveTestData.oppgave3Inaktiv
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveTestData.oppgave4
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveTestData.oppgaveTestFnr

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OppgaveQueriesTest {

    private val database = LocalPostgresDatabase.cleanDb()

    @BeforeAll
    fun `populer test-data`() {
        database.createOppgave(listOf(oppgave1Aktiv, oppgave2Aktiv, oppgave3Inaktiv, oppgave4))
    }

    @AfterAll
    fun `slett Oppgave-eventer fra tabellen`() {
        database.deleteOppgave()
    }

    @Test
    fun `Finn alle cachede Oppgave-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getAllOppgaveForFodselsnummer(oppgaveTestFnr) }.size shouldBe 3
        }
    }

    @Test
    fun `Finn kun aktive cachede Oppgave-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getAktivOppgaveForFodselsnummer(oppgaveTestFnr) }.size shouldBe 2
        }
    }

    @Test
    fun `Finn kun inaktive cachede Oppgave-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getInaktivOppgaveForFodselsnummer(oppgaveTestFnr) }.size shouldBe 1
        }
    }

    @Test
    fun `Returnerer tom liste hvis Oppgave-eventer for fodselsnummer ikke finnes`() {
        val brukerSomIkkeFinnes = "0"
        runBlocking {
            database.dbQuery { getAktivOppgaveForFodselsnummer(brukerSomIkkeFinnes) }.isEmpty()
        }
    }

    @Test
    fun `Returnerer tom liste hvis fodselsnummer er tomt`() {
        val fodselsnummerMangler = ""
        runBlocking {
            database.dbQuery { getAktivOppgaveForFodselsnummer(fodselsnummerMangler) }.isEmpty()
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for aktive eventer`() {
        runBlocking {
            val oppgave = database.dbQuery { getAktivOppgaveForFodselsnummer(oppgaveTestFnr) }.first()
            oppgave.produsent shouldBe appnavn
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for inaktive eventer`() {
        runBlocking {
            val oppgave = database.dbQuery { getInaktivOppgaveForFodselsnummer(oppgaveTestFnr) }.first()
            oppgave.produsent shouldBe appnavn
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for alle eventer`() {
        runBlocking {
            val oppgave = database.dbQuery { getAllOppgaveForFodselsnummer(oppgaveTestFnr) }.first()
            oppgave.produsent shouldBe appnavn
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte Oppgave-eventer`() {
        runBlocking {
            database.dbQuery {
                getAllGroupedOppgaveEventsByIds(oppgaveTestFnr, grupperingsid, appnavn)
            }.size shouldBe 3
        }
    }

    @Test
    fun `Returnerer en tom liste hvis produsent ikke matcher oppgave-eventet`() {
        val noMatchProdusent = "dummyProdusent"
        runBlocking {
            database.dbQuery {
                getAllGroupedOppgaveEventsByIds(oppgaveTestFnr, grupperingsid, noMatchProdusent)
            }.shouldBeEmpty()
        }
    }

    @Test
    fun `Returnerer en tom liste hvis grupperingsid ikke matcher oppgave-eventet`() {
        val noMatchGrupperingsid = "dummyGrupperingsid"
        runBlocking {
            database.dbQuery {
                getAllGroupedOppgaveEventsByIds(oppgaveTestFnr, noMatchGrupperingsid, appnavn)
            }.shouldBeEmpty()
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte Oppgave-eventer basert paa systembruker`() {
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { getAllGroupedOppgaveEventsBySystemuser() }

            groupedEventsBySystemuser.size shouldBe 2
            groupedEventsBySystemuser[oppgave1Aktiv.systembruker] shouldBe 3
            groupedEventsBySystemuser[oppgave4.systembruker] shouldBe 1
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte Oppgave-eventer basert paa produsent`() {
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { getAllGroupedOppgaveEventsByProducer() }

            groupedEventsBySystemuser.size shouldBe 2
            groupedEventsBySystemuser.findCountFor(oppgave1Aktiv.namespace, oppgave1Aktiv.appnavn) shouldBe 3
            groupedEventsBySystemuser.findCountFor(oppgave4.namespace, oppgave4.appnavn) shouldBe 1
        }
    }

    @Test
    fun `Returnerer riktig info om ekstern varsling dersom status er mottat og oversendt`() = runBlocking {
        val oppgave = database.dbQuery {
            getAktivOppgaveForFodselsnummer(oppgave1Aktiv.fodselsnummer)
        }.filter {
            it.eventId == oppgave1Aktiv.eventId
        }.first()

        val eksternVarslingInfo = oppgave.eksternVarsling

        eksternVarslingInfo shouldNotBe null
        eksternVarslingInfo!!.prefererteKanaler shouldContainAll oppgave1Aktiv.eksternVarsling!!.prefererteKanaler
        eksternVarslingInfo.sendt shouldBe true
        eksternVarslingInfo.sendteKanaler shouldContainAll oppgave1Aktiv.eksternVarsling!!.sendteKanaler
    }

    @Test
    fun `Returnerer riktig info om ekstern varsling dersom status er mottat og feilet`() = runBlocking {
        val oppgave = database.dbQuery {
            getAktivOppgaveForFodselsnummer(oppgave2Aktiv.fodselsnummer)
        }.filter {
            it.eventId == oppgave2Aktiv.eventId
        }.first()

        val eksternVarslingInfo = oppgave.eksternVarsling

        eksternVarslingInfo shouldNotBe null
        eksternVarslingInfo!!.prefererteKanaler shouldContainAll oppgave1Aktiv.eksternVarsling!!.prefererteKanaler
        eksternVarslingInfo.sendt shouldBe false
        eksternVarslingInfo.sendteKanaler shouldContainAll oppgave1Aktiv.eksternVarsling!!.sendteKanaler
    }

    @Test
    fun `Returnerer riktig info om ekstern varsling dersom status ikke er mottatt`() = runBlocking {
        val oppgave = database.dbQuery {
            getInaktivOppgaveForFodselsnummer(oppgave3Inaktiv.fodselsnummer)
        }.filter {
            it.eventId == oppgave3Inaktiv.eventId
        }.first()

        val eksternVarslingInfo = oppgave.eksternVarsling

        eksternVarslingInfo shouldBe null
    }
}
