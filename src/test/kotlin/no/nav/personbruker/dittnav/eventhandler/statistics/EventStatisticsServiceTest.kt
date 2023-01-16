package no.nav.personbruker.dittnav.eventhandler.statistics

import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import no.nav.personbruker.dittnav.eventhandler.beskjed.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.deleteBeskjed
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.common.VarselType
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksObjectMother
import no.nav.personbruker.dittnav.eventhandler.innboks.createInnboks
import no.nav.personbruker.dittnav.eventhandler.innboks.deleteInnboks
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

    private val beskjed1 = BeskjedObjectMother.createBeskjed(
        fodselsnummer = fodselsnummer,
        synligFremTil = ZonedDateTime.now().plusHours(1),
        aktiv = true,
        systembruker = systembruker,
        tekst = "12",
    )
    private val beskjed2 = BeskjedObjectMother.createBeskjed(
        fodselsnummer = fodselsnummer,
        synligFremTil = ZonedDateTime.now().plusHours(1),
        aktiv = true,
        systembruker = systembruker,
    )
    private val beskjed3 = BeskjedObjectMother.createBeskjed(
        fodselsnummer = fodselsnummer,
        synligFremTil = ZonedDateTime.now().plusHours(1),
        aktiv = false,
        systembruker = systembruker,
    )
    private val beskjed4 = BeskjedObjectMother.createBeskjed(
        fodselsnummer = "54321",
        synligFremTil = ZonedDateTime.now().plusHours(1),
        aktiv = true,
        systembruker = "x-dittnav-2",
    )

    private val oppgave1 = OppgaveObjectMother.createOppgave(
        fodselsnummer = fodselsnummer,
        aktiv = true,
        systembruker = systembruker,
        tekst = "123",
        fristUtløpt = null
    )
    private val oppgave2 = OppgaveObjectMother.createOppgave(
        fodselsnummer = fodselsnummer,
        aktiv = true,
        systembruker = systembruker,
        fristUtløpt = null
    )
    private val oppgave3 = OppgaveObjectMother.createOppgave(
        fodselsnummer = fodselsnummer,
        aktiv = false,
        systembruker = systembruker,
        fristUtløpt = null
    )
    private val oppgave4 = OppgaveObjectMother.createOppgave(
        fodselsnummer = "54321",
        aktiv = true,
        systembruker = "x-dittnav-2",
        fristUtløpt = null
    )

    private val innboks1 = InnboksObjectMother.createInnboks(fodselsnummer = fodselsnummer, aktiv = true, systembruker = systembruker, tekst = "ab")
    private val innboks2 = InnboksObjectMother.createInnboks(fodselsnummer = fodselsnummer1, aktiv = true, systembruker = systembruker)
    private val innboks3 = InnboksObjectMother.createInnboks(fodselsnummer = fodselsnummer2, aktiv = true, systembruker = "x-dittnav-2")
    private val innboks4 = InnboksObjectMother.createInnboks(fodselsnummer = fodselsnummer2, aktiv = false, systembruker = systembruker)

    @BeforeAll
    fun `populer testdata`() {
        database.createBeskjed(listOf(beskjed1, beskjed2, beskjed3, beskjed4))
        database.createInnboks(listOf(innboks1, innboks2, innboks3, innboks4))
        database.createOppgave(listOf(oppgave1, oppgave2, oppgave3, oppgave4))
    }

    @AfterAll
    fun `slett testdata`() {
        database.deleteBeskjed(listOf(beskjed1, beskjed2, beskjed3, beskjed4))
        database.deleteInnboks(listOf(innboks1, innboks2, innboks3, innboks4))
        database.deleteOppgave(listOf(oppgave1, oppgave2, oppgave3, oppgave4))
    }

    @Test
    fun `Should run stats`() {
        runBlocking {
            eventStatisticsService.getEventsStatisticsPerUser(VarselType.BESKJED).max shouldBe 3
            eventStatisticsService.getActiveEventsStatisticsPerUser(VarselType.BESKJED).max shouldBe 2
            eventStatisticsService.getActiveRateEventsStatisticsPerUser(VarselType.BESKJED).min shouldBeGreaterThan 0.6
            eventStatisticsService.getEventsStatisticsPerGroupId(VarselType.BESKJED).max shouldBe 3
            eventStatisticsService.getGroupIdsPerUser(VarselType.BESKJED).max shouldBe 1
            eventStatisticsService.getTextLength(VarselType.BESKJED).min shouldBe 2
            eventStatisticsService.getCountUsersWithEvents(VarselType.BESKJED).count shouldBe 2
            eventStatisticsService.getActiveEventCount(VarselType.BESKJED).count shouldBe 3

            eventStatisticsService.getEventsStatisticsPerUser(VarselType.OPPGAVE).max shouldBe 3
            eventStatisticsService.getActiveEventsStatisticsPerUser(VarselType.OPPGAVE).max shouldBe 2
            eventStatisticsService.getActiveRateEventsStatisticsPerUser(VarselType.OPPGAVE).min shouldBeGreaterThan 0.6
            eventStatisticsService.getEventsStatisticsPerGroupId(VarselType.OPPGAVE).max shouldBe 3
            eventStatisticsService.getGroupIdsPerUser(VarselType.OPPGAVE).max shouldBe 1
            eventStatisticsService.getTextLength(VarselType.OPPGAVE).min shouldBe 3
            eventStatisticsService.getCountUsersWithEvents(VarselType.OPPGAVE).count shouldBe 2
            eventStatisticsService.getActiveEventCount(VarselType.OPPGAVE).count shouldBe 3

            eventStatisticsService.getEventsStatisticsPerUser(VarselType.INNBOKS).max shouldBe 2
            eventStatisticsService.getActiveEventsStatisticsPerUser(VarselType.INNBOKS).max shouldBe 2
            eventStatisticsService.getActiveRateEventsStatisticsPerUser(VarselType.INNBOKS).min shouldBeGreaterThanOrEqual 0.5
            eventStatisticsService.getEventsStatisticsPerGroupId(VarselType.INNBOKS).max shouldBe 2
            eventStatisticsService.getGroupIdsPerUser(VarselType.INNBOKS).max shouldBe 1
            eventStatisticsService.getTextLength(VarselType.INNBOKS).min shouldBe 2
            eventStatisticsService.getCountUsersWithEvents(VarselType.INNBOKS).count shouldBe 2
            eventStatisticsService.getActiveEventCount(VarselType.INNBOKS).count shouldBe 3

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
}
