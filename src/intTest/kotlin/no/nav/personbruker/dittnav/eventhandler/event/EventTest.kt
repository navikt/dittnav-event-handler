package no.nav.personbruker.dittnav.eventhandler.event

import Beskjed
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import no.nav.personbruker.dittnav.eventhandler.beskjed.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.innboks.Innboks
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksObjectMother
import no.nav.personbruker.dittnav.eventhandler.innboks.createInnboks
import no.nav.personbruker.dittnav.eventhandler.oppgave.Oppgave
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveObjectMother
import no.nav.personbruker.dittnav.eventhandler.oppgave.createOppgave
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.ZonedDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventTest {
    private val database = LocalPostgresDatabase.cleanDb()

    private val fodselsnummer = "12345678"
    private val uid = "22"
    private val eventId = "124"
    private val grupperingsid = "100${fodselsnummer}"
    private val systembruker = "x-dittnav"
    private val namespace = "localhost"
    private val appnavn = "dittnav"

    private val beskjed1 = BeskjedObjectMother.createBeskjed(
            id = 1,
            eventId = "123",
            fodselsnummer = fodselsnummer,
            grupperingsId = grupperingsid,
            synligFremTil = ZonedDateTime.now().plusHours(1),
            uid = "11",
            aktiv = false,
            systembruker = systembruker,
            namespace = namespace,
            appnavn = appnavn
    )

    private val oppgave1 = OppgaveObjectMother.createOppgave(
            id = 1,
            eventId = "123",
            fodselsnummer = fodselsnummer,
            grupperingsId = grupperingsid,
            aktiv = false,
            systembruker = systembruker,
            namespace = namespace,
            appnavn = appnavn
    )

    private val innboks1 = InnboksObjectMother.createInnboks(
            id = 1,
            eventId = "123",
            fodselsnummer = fodselsnummer,
            grupperingsId = grupperingsid,
            aktiv = false,
            systembruker = systembruker,
            namespace = namespace,
            appnavn = appnavn
    )

    @BeforeAll
    fun `populer testdata`() {
        createBeskjed(listOf(beskjed1))
        createOppgave(listOf(oppgave1))
        createInnboks(listOf(innboks1))
    }

    @Test
    fun `hente alle inaktive eventer for bruker`() {
        val eventRepository = EventRepository(database)
        runBlocking {
            val inaktiveEventer = eventRepository.getInactiveEvents(beskjed1.fodselsnummer)
            inaktiveEventer.size shouldBe 3
            inaktiveEventer.all { it.toEventDTO().fodselsnummer == beskjed1.fodselsnummer } shouldBe true
        }
    }

    private fun createBeskjed(beskjeder: List<Beskjed>) {
        runBlocking {
            database.dbQuery { createBeskjed(beskjeder) }
        }
    }

    private fun createOppgave(oppgaver: List<Oppgave>) {
        runBlocking {
            database.dbQuery { createOppgave(oppgaver) }
        }
    }

    private fun createInnboks(innboks: List<Innboks>) {
        runBlocking {
            database.dbQuery { createInnboks(innboks) }
        }
    }
}

