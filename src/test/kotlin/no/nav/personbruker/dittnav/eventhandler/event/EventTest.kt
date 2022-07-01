package no.nav.personbruker.dittnav.eventhandler.event

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import no.nav.personbruker.dittnav.eventhandler.beskjed.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksObjectMother
import no.nav.personbruker.dittnav.eventhandler.innboks.createInnboks
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveObjectMother
import no.nav.personbruker.dittnav.eventhandler.oppgave.createOppgave
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventTest {
    private val database = LocalPostgresDatabase.cleanDb()

    private val fodselsnummer = "12345678"

    @BeforeAll
    fun `populer testdata`() {
        createBeskjeder(3, 5)
        createOppgaver(1, 4)
        createInnboks(2, 1)
    }

    @Test
    fun `hente alle inaktive eventer for bruker`() {
        val eventRepository = EventRepository(database)
        runBlocking {
            val inaktiveEventer = eventRepository.getInactiveEvents(fodselsnummer)
            inaktiveEventer.size shouldBe 10
            inaktiveEventer.map { it.toEventDTO().type }.toSet() shouldBe setOf(EventType.BESKJED, EventType.OPPGAVE, EventType.INNBOKS)
        }
    }

    private fun createBeskjeder(antallAktive: Int, antallInaktive: Int) {
        val beskjeder = (1..antallAktive).map {
            BeskjedObjectMother.createBeskjed(
                fodselsnummer = fodselsnummer,
                aktiv = true
            )
        } + (1..antallInaktive).map {
            BeskjedObjectMother.createBeskjed(
                fodselsnummer = fodselsnummer,
                aktiv = false
            )
        }

        runBlocking {
            database.dbQuery { createBeskjed(beskjeder) }
        }
    }

    private fun createOppgaver(antallAktive: Int, antallInaktive: Int) {
        val oppgaver = (1..antallAktive).map {
            OppgaveObjectMother.createOppgave(
                fodselsnummer = fodselsnummer,
                aktiv = true
            )
        } + (1..antallInaktive).map {
            OppgaveObjectMother.createOppgave(
                fodselsnummer = fodselsnummer,
                aktiv = false
            )
        }

        runBlocking {
            database.dbQuery { createOppgave(oppgaver) }
        }
    }

    private fun createInnboks(antallAktive: Int, antallInaktive: Int) {
        val innboks = (1..antallAktive).map {
            InnboksObjectMother.createInnboks(
                fodselsnummer = fodselsnummer,
                aktiv = true
            )
        } + (1..antallInaktive).map {
            InnboksObjectMother.createInnboks(
                fodselsnummer = fodselsnummer,
                aktiv = false
            )
        }

        runBlocking {
            database.dbQuery { createInnboks(innboks) }
        }
    }
}

