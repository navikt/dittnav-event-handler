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
import java.time.ZonedDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventTest {
    private val database = LocalPostgresDatabase.cleanDb()
    private val eventRepository = EventRepository(database)

    private val alleEventTyper = setOf(
        EventType.BESKJED,
        EventType.OPPGAVE,
        EventType.INNBOKS
    )
    private val fodselsnummer = "12345678"

    private val gammelDato = ZonedDateTime.now().minusDays(400)
    private val dagensDato = ZonedDateTime.now()

    @BeforeAll
    fun `populer testdata`() {
        createBeskjeder(3, 5, dagensDato)
        createOppgaver(1, 4, dagensDato)
        createInnboks(2, 1, dagensDato)

        createBeskjeder(7, 9, gammelDato)
        createOppgaver(2, 1, gammelDato)
        createInnboks(3, 2, gammelDato)
    }

    @Test
    fun `hente brukers inaktive eventer nyere enn dato`() = runBlocking {
        val inaktiveEventer = eventRepository.getInactiveEvents(fodselsnummer)
        inaktiveEventer.size shouldBe 10
        inaktiveEventer.map { it.toEventDTO().type }.toSet() shouldBe alleEventTyper
    }

    @Test
    fun `hente brukers aktive eventer nyere enn dato`() = runBlocking {
        val aktiveEventer = eventRepository.getActiveEvents(fodselsnummer)
        aktiveEventer.size shouldBe 6
        aktiveEventer.map { it.toEventDTO().type }.toSet() shouldBe alleEventTyper
    }

    private fun createBeskjeder(antallAktive: Int, antallInaktive: Int, forstBehandlet:ZonedDateTime) {
        val beskjeder = (1..antallAktive).map {
            BeskjedObjectMother.createBeskjed(
                fodselsnummer = fodselsnummer,
                aktiv = true,
                forstBehandlet = forstBehandlet
            )
        } + (1..antallInaktive).map {
            BeskjedObjectMother.createBeskjed(
                fodselsnummer = fodselsnummer,
                aktiv = false,
                forstBehandlet = forstBehandlet
            )
        }

        runBlocking {
            database.dbQuery { createBeskjed(beskjeder) }
        }
    }

    private fun createOppgaver(antallAktive: Int, antallInaktive: Int, forstBehandlet:ZonedDateTime) {
        val oppgaver = (1..antallAktive).map {
            OppgaveObjectMother.createOppgave(
                fodselsnummer = fodselsnummer,
                aktiv = true,
                forstBehandlet = forstBehandlet
            )
        } + (1..antallInaktive).map {
            OppgaveObjectMother.createOppgave(
                fodselsnummer = fodselsnummer,
                aktiv = false,
                forstBehandlet = forstBehandlet
            )
        }

        runBlocking {
            database.dbQuery { createOppgave(oppgaver) }
        }
    }

    private fun createInnboks(antallAktive: Int, antallInaktive: Int, forstBehandlet:ZonedDateTime) {
        val innboks = (1..antallAktive).map {
            InnboksObjectMother.createInnboks(
                fodselsnummer = fodselsnummer,
                aktiv = true,
                forstBehandlet = forstBehandlet
            )
        } + (1..antallInaktive).map {
            InnboksObjectMother.createInnboks(
                fodselsnummer = fodselsnummer,
                aktiv = false,
                forstBehandlet = forstBehandlet
            )
        }

        runBlocking {
            database.dbQuery { createInnboks(innboks) }
        }
    }
}

