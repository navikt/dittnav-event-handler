package no.nav.personbruker.dittnav.eventhandler.oppgave

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.common.findCountFor
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OppgaveQueriesTest {

    private val database = LocalPostgresDatabase.cleanDb()
    private val fodselsnummer = "12345"
    private val systembruker = "x-dittnav"
    private val namespace = "localhost"
    private val appnavn = "dittnav"
    private val grupperingsid = "100${fodselsnummer}"

    private val oppgave1 = OppgaveObjectMother.createOppgave(
        id = 1,
        eventId = "123",
        fodselsnummer = fodselsnummer,
        grupperingsId = grupperingsid,
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn
    )
    private val oppgave2 = OppgaveObjectMother.createOppgave(
        id = 2,
        eventId = "345",
        fodselsnummer = fodselsnummer,
        grupperingsId = grupperingsid,
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn
    )
    private val oppgave3 = OppgaveObjectMother.createOppgave(
        id = 3,
        eventId = "567",
        fodselsnummer = fodselsnummer,
        grupperingsId = grupperingsid,
        aktiv = false,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn
    )
    private val oppgave4 = OppgaveObjectMother.createOppgave(
        id = 4,
        eventId = "789",
        fodselsnummer = "54321",
        aktiv = true,
        systembruker = "x-dittnav-2",
        namespace = namespace,
        appnavn = "x-dittnav"
    )

    @BeforeAll
    fun `populer test-data`() {
        createOppgave(listOf(oppgave1, oppgave2, oppgave3, oppgave4))
    }

    @AfterAll
    fun `slett Oppgave-eventer fra tabellen`() {
        deleteOppgave(listOf(oppgave1, oppgave2, oppgave3, oppgave4))
    }

    @Test
    fun `Finn alle cachede Oppgave-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getAllOppgaveForInnloggetBruker(fodselsnummer) }.size shouldBe 3
        }
    }

    @Test
    fun `Finn kun aktive cachede Oppgave-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getAktivOppgaveForInnloggetBruker(fodselsnummer) }.size shouldBe 2
        }
    }

    @Test
    fun `Finn kun inaktive cachede Oppgave-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getInaktivOppgaveForInnloggetBruker(fodselsnummer) }.size shouldBe 1
        }
    }

    @Test
    fun `Returnerer tom liste hvis Oppgave-eventer for fodselsnummer ikke finnes`() {
        val brukerSomIkkeFinnes = "0"
        runBlocking {
            database.dbQuery { getAktivOppgaveForInnloggetBruker(brukerSomIkkeFinnes) }.isEmpty()
        }
    }

    @Test
    fun `Returnerer tom liste hvis fodselsnummer er tomt`() {
        val fodselsnummerMangler = ""
        runBlocking {
            database.dbQuery { getAktivOppgaveForInnloggetBruker(fodselsnummerMangler) }.isEmpty()
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for aktive eventer`() {
        runBlocking {
            val oppgave = database.dbQuery { getAktivOppgaveForInnloggetBruker(fodselsnummer) }.first()
            oppgave.produsent shouldBe appnavn
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for inaktive eventer`() {
        runBlocking {
            val oppgave = database.dbQuery { getInaktivOppgaveForInnloggetBruker(fodselsnummer) }.first()
            oppgave.produsent shouldBe appnavn
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for alle eventer`() {
        runBlocking {
            val oppgave = database.dbQuery { getAllOppgaveForInnloggetBruker(fodselsnummer) }.first()
            oppgave.produsent shouldBe appnavn
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte Oppgave-eventer`() {
        runBlocking {
            database.dbQuery {
                getAllGroupedOppgaveEventsByIds(fodselsnummer, grupperingsid, appnavn)
            }.size shouldBe 3
        }
    }

    @Test
    fun `Returnerer en tom liste hvis produsent ikke matcher oppgave-eventet`() {
        val noMatchProdusent = "dummyProdusent"
        runBlocking {
            database.dbQuery {
                getAllGroupedOppgaveEventsByIds(fodselsnummer, grupperingsid, noMatchProdusent)
            }.shouldBeEmpty()
        }
    }

    @Test
    fun `Returnerer en tom liste hvis grupperingsid ikke matcher oppgave-eventet`() {
        val noMatchGrupperingsid = "dummyGrupperingsid"
        runBlocking {
            database.dbQuery {
                getAllGroupedOppgaveEventsByIds(fodselsnummer, noMatchGrupperingsid, appnavn)
            }.shouldBeEmpty()
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte Oppgave-eventer basert paa systembruker`() {
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { getAllGroupedOppgaveEventsBySystemuser() }

            groupedEventsBySystemuser.size shouldBe 2
            groupedEventsBySystemuser[oppgave1.systembruker] shouldBe 3
            groupedEventsBySystemuser[oppgave4.systembruker] shouldBe 1
        }
    }


    @Test
    fun `Returnerer en liste av alle grupperte Oppgave-eventer basert paa produsent`() {
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { getAllGroupedOppgaveEventsByProducer() }

            groupedEventsBySystemuser.size shouldBe 2
            groupedEventsBySystemuser.findCountFor(oppgave1.namespace, oppgave1.appnavn) shouldBe 3
            groupedEventsBySystemuser.findCountFor(oppgave4.namespace, oppgave4.appnavn) shouldBe 1
        }
    }

    private fun createOppgave(oppgaver: List<Oppgave>) {
        runBlocking {
            database.dbQuery { createOppgave(oppgaver) }
        }
    }

    private fun deleteOppgave(oppgaver: List<Oppgave>) {
        runBlocking {
            database.dbQuery { deleteOppgave(oppgaver) }
        }
    }
}
