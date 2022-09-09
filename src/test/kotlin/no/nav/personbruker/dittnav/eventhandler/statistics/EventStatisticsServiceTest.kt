package no.nav.personbruker.dittnav.eventhandler.statistics

import Beskjed
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import no.nav.personbruker.dittnav.eventhandler.beskjed.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.deleteBeskjed
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.event.EventType
import no.nav.personbruker.dittnav.eventhandler.innboks.Innboks
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksTestData
import no.nav.personbruker.dittnav.eventhandler.innboks.createInnboks
import no.nav.personbruker.dittnav.eventhandler.innboks.deleteInnboks
import no.nav.personbruker.dittnav.eventhandler.oppgave.Oppgave
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveObjectMother
import no.nav.personbruker.dittnav.eventhandler.oppgave.createOppgave
import no.nav.personbruker.dittnav.eventhandler.oppgave.deleteOppgave
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.ZonedDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventStatisticsServiceTest {

    private val database = LocalPostgresDatabase.cleanDb()
    private val eventStatisticsService = EventStatisticsService(database)

    private val fodselsnummer = "12345"
    private val fodselsnummer1 = "12345"
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

    private val innboks1 = InnboksTestData.createInnboks(fodselsnummer = fodselsnummer, aktiv = true, systembruker = systembruker, tekst = "ab")
    private val innboks2 = InnboksTestData.createInnboks(fodselsnummer = fodselsnummer1, aktiv = true, systembruker = systembruker)
    private val innboks3 = InnboksTestData.createInnboks(fodselsnummer = fodselsnummer2, aktiv = true, systembruker = "x-dittnav-2")
    private val innboks4 = InnboksTestData.createInnboks(fodselsnummer = fodselsnummer2, aktiv = false, systembruker = systembruker)

    @BeforeAll
    fun `populer testdata`() {
        createBeskjed(listOf(beskjed1, beskjed2, beskjed3, beskjed4))
        createInnboks(listOf(innboks1, innboks2, innboks3, innboks4))
        createOppgave(listOf(oppgave1, oppgave2, oppgave3, oppgave4))
    }

    @AfterAll
    fun `slett testdata`() {
        deleteBeskjed(listOf(beskjed1, beskjed2, beskjed3, beskjed4))
        deleteInnboks(listOf(innboks1, innboks2, innboks3, innboks4))
        deleteOppgave(listOf(oppgave1, oppgave2, oppgave3, oppgave4))
    }

    @Test
    fun `Should run stats`() {
        runBlocking {
            eventStatisticsService.getEventsStatisticsPerUser(EventType.BESKJED).max shouldBe 3
            eventStatisticsService.getActiveEventsStatisticsPerUser(EventType.BESKJED).max shouldBe 2
            eventStatisticsService.getActiveRateEventsStatisticsPerUser(EventType.BESKJED).min shouldBeGreaterThan 0.6
            eventStatisticsService.getEventsStatisticsPerGroupId(EventType.BESKJED).max shouldBe 3
            eventStatisticsService.getGroupIdsPerUser(EventType.BESKJED).max shouldBe 1
            eventStatisticsService.getTextLength(EventType.BESKJED).min shouldBe 2
            eventStatisticsService.getCountUsersWithEvents(EventType.BESKJED).count shouldBe 2
            eventStatisticsService.getActiveEventCount(EventType.BESKJED).count shouldBe 3

            eventStatisticsService.getEventsStatisticsPerUser(EventType.OPPGAVE).max shouldBe 3
            eventStatisticsService.getActiveEventsStatisticsPerUser(EventType.OPPGAVE).max shouldBe 2
            eventStatisticsService.getActiveRateEventsStatisticsPerUser(EventType.OPPGAVE).min shouldBeGreaterThan 0.6
            eventStatisticsService.getEventsStatisticsPerGroupId(EventType.OPPGAVE).max shouldBe 3
            eventStatisticsService.getGroupIdsPerUser(EventType.OPPGAVE).max shouldBe 1
            eventStatisticsService.getTextLength(EventType.OPPGAVE).min shouldBe 3
            eventStatisticsService.getCountUsersWithEvents(EventType.OPPGAVE).count shouldBe 2
            eventStatisticsService.getActiveEventCount(EventType.OPPGAVE).count shouldBe 3

            eventStatisticsService.getEventsStatisticsPerUser(EventType.INNBOKS).max shouldBe 2
            eventStatisticsService.getActiveEventsStatisticsPerUser(EventType.INNBOKS).max shouldBe 2
            eventStatisticsService.getActiveRateEventsStatisticsPerUser(EventType.INNBOKS).min shouldBeGreaterThanOrEqual 0.5
            eventStatisticsService.getEventsStatisticsPerGroupId(EventType.INNBOKS).max shouldBe 2
            eventStatisticsService.getGroupIdsPerUser(EventType.INNBOKS).max shouldBe 1
            eventStatisticsService.getTextLength(EventType.INNBOKS).min shouldBe 2
            eventStatisticsService.getCountUsersWithEvents(EventType.INNBOKS).count shouldBe 2
            eventStatisticsService.getActiveEventCount(EventType.INNBOKS).count shouldBe 3

            eventStatisticsService.getTotalEventsStatisticsPerUser().max shouldBe 8
            eventStatisticsService.getTotalActiveEventsStatisticsPerUser().max shouldBe 6
            eventStatisticsService.getTotalActiveRateEventsStatisticsPerUser().min shouldBeGreaterThanOrEqual 0.5
            eventStatisticsService.getTotalEventsStatisticsPerGroupId().max shouldBe 8
            eventStatisticsService.getTotalGroupIdsPerUser().max shouldBe 1
            eventStatisticsService.getTotalTextLength().min shouldBe 2
            eventStatisticsService.getTotalCountUsersWithEvents().count shouldBe 3
            eventStatisticsService.getTotalActiveEventCount().count shouldBe 9
        }
    }

    private fun createBeskjed(beskjeder: List<Beskjed>) {
        runBlocking {
            database.dbQuery { createBeskjed(beskjeder) }
        }
    }
    private fun deleteBeskjed(beskjeder: List<Beskjed>) {
        runBlocking {
            database.dbQuery { deleteBeskjed(beskjeder) }
        }
    }
    private fun createInnboks(innboks: List<Innboks>) {
        runBlocking {
            database.dbQuery { createInnboks(innboks) }
        }
    }
    private fun deleteInnboks(innboks: List<Innboks>) {
        runBlocking {
            database.dbQuery { deleteInnboks(innboks) }
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
