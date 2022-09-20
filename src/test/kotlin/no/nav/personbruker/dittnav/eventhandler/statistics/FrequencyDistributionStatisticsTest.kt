package no.nav.personbruker.dittnav.eventhandler.statistics

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import no.nav.personbruker.dittnav.eventhandler.beskjed.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.event.EventType
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksObjectMother
import no.nav.personbruker.dittnav.eventhandler.innboks.createInnboks
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveObjectMother
import no.nav.personbruker.dittnav.eventhandler.oppgave.createOppgave
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.ZonedDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FrequencyDistributionStatisticsTest {

    private val database = LocalPostgresDatabase.cleanDb()
    private val eventStatisticsService = EventStatisticsService(database)

    private val fodselsnummer = "12345"
    private val fodselsnummer2 = "67890"
    private val systembruker = "x-dittnav"

    private val beskjed1 = BeskjedObjectMother.createBeskjed(fodselsnummer = fodselsnummer, synligFremTil = ZonedDateTime.now().plusHours(1), aktiv = true, systembruker = systembruker, tekst = "12")
    private val beskjed2 = BeskjedObjectMother.createBeskjed(fodselsnummer = fodselsnummer, synligFremTil = ZonedDateTime.now().plusHours(1), aktiv = true, systembruker = systembruker)
    private val beskjed3 = BeskjedObjectMother.createBeskjed(fodselsnummer = fodselsnummer, synligFremTil = ZonedDateTime.now().plusHours(1), aktiv = false, systembruker = systembruker)
    private val beskjed4 = BeskjedObjectMother.createBeskjed(fodselsnummer = "54321", synligFremTil = ZonedDateTime.now().plusHours(1), aktiv = true, systembruker = "x-dittnav-2")

    private val oppgave1 = OppgaveObjectMother.createOppgave(fodselsnummer = fodselsnummer, aktiv = true, systembruker = systembruker, tekst = "123")
    private val oppgave2 = OppgaveObjectMother.createOppgave(fodselsnummer = fodselsnummer, aktiv = true, systembruker = systembruker)
    private val oppgave3 = OppgaveObjectMother.createOppgave(fodselsnummer = fodselsnummer, aktiv = false, systembruker = systembruker)
    private val oppgave4 = OppgaveObjectMother.createOppgave(fodselsnummer = "54321", aktiv = true, systembruker = "x-dittnav-2")
    private val oppgave5 = OppgaveObjectMother.createOppgave(fodselsnummer = "37226687654", aktiv = true, systembruker = systembruker)
    private val oppgave6 = OppgaveObjectMother.createOppgave(fodselsnummer = "37226687635", aktiv = true, systembruker = systembruker)

    private val innboks1 = InnboksObjectMother.createInnboks(fodselsnummer = fodselsnummer, aktiv = true, systembruker = systembruker, tekst = "ab")
    private val innboks2 = InnboksObjectMother.createInnboks(fodselsnummer = fodselsnummer2, aktiv = true, systembruker = "x-dittnav-2")
    private val innboks3 = InnboksObjectMother.createInnboks(fodselsnummer = fodselsnummer2, aktiv = false, systembruker = systembruker)

    @BeforeAll
    fun `populer testdata`() {
        database.createBeskjed(listOf(beskjed1, beskjed2, beskjed3, beskjed4))
        database.createInnboks(listOf(innboks1, innboks2, innboks3))
        database.createOppgave(listOf(oppgave1, oppgave2, oppgave3, oppgave4, oppgave5, oppgave6))
    }

    @Test
    fun `Frekvensfordeling av antall aktive beskjeder`() {
        runBlocking {
            val eventFrekvensFordeling = eventStatisticsService.getActiveEventsFrequencyDistribution(EventType.BESKJED)
            eventFrekvensFordeling.eventFrequencies.size shouldBe 2
            eventFrekvensFordeling.eventFrequencies.first { it.antallEventer == 2 }.antallBrukere shouldBe 1
        }
    }

    @Test
    fun `Frekvensfordeling av antall aktive oppgaver`() {
        runBlocking {
            val eventFrekvensFordeling = eventStatisticsService.getActiveEventsFrequencyDistribution(EventType.OPPGAVE)
            eventFrekvensFordeling.eventFrequencies.size shouldBe 2
            eventFrekvensFordeling.eventFrequencies.first { it.antallEventer == 1 }.antallBrukere shouldBe 3
        }
    }

    @Test
    fun `Frekvensfordeling av antall aktive innboks`() {
        runBlocking {
            val eventFrekvensFordeling = eventStatisticsService.getActiveEventsFrequencyDistribution(EventType.INNBOKS)
            eventFrekvensFordeling.eventFrequencies.size shouldBe 1
            eventFrekvensFordeling.eventFrequencies.first { it.antallEventer == 1 }.antallBrukere shouldBe 2
        }
    }
}
