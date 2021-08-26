package no.nav.personbruker.dittnav.eventhandler.done

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import no.nav.personbruker.dittnav.eventhandler.beskjed.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.deleteBeskjed
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksObjectMother
import no.nav.personbruker.dittnav.eventhandler.innboks.createInnboks
import no.nav.personbruker.dittnav.eventhandler.innboks.deleteInnboks
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveObjectMother
import no.nav.personbruker.dittnav.eventhandler.oppgave.createOppgave
import no.nav.personbruker.dittnav.eventhandler.oppgave.deleteOppgave
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DoneQueriesTest {

    private val database = H2Database()
    private val fodselsnummer = "123"
    private val systembruker = "x-dittnav"
    private val grupperingsId = "xxx"
    private val utcDateTime = ZonedDateTime.now(ZoneOffset.UTC)
    private val osloDateTime = ZonedDateTime.ofInstant(utcDateTime.toInstant(), ZoneId.of("Europe/Oslo"))

    private val done1 = DoneObjectMother.createDone(systembruker = "x-dittnav", utcDateTime, fodselsnummer, "1", grupperingsId)
    private val done2 = DoneObjectMother.createDone(systembruker = "y-dittnav", utcDateTime, fodselsnummer, "2", grupperingsId)
    private val inaktivBeskjed = BeskjedObjectMother.createBeskjed(id = 1, eventId = "123", fodselsnummer = "00", synligFremTil = ZonedDateTime.now().plusHours(1), uid = "11", aktiv = false, systembruker = "x-dittnav")
    private val inaktivOppgave = OppgaveObjectMother.createOppgave(id = 1, eventId = "123", fodselsnummer = "01", aktiv = false, systembruker = "x-dittnav")
    private val inaktivInnboks = InnboksObjectMother.createInnboks(id = 1, eventId = "123", fodselsnummer = "02", aktiv = false, systembruker = "y-dittnav")

    @BeforeAll
    fun `populer testdata`() {
        createDone(listOf(done1, done2))
    }

    @AfterAll
    fun `slett testdata`() {
        deleteDone(listOf(done1, done2))
    }

    @Test
    fun `Finn alle cachede Done-eventer`() {
        runBlocking {
            database.dbQuery { getAllDoneEvents() }.size `should be equal to` 2
        }
    }

    @Test
    fun `Finn alle cachede `() {
        runBlocking {
            val doneEvents = database.dbQuery { getAllDoneEvents() }
            doneEvents.first().eventId `should be equal to` "1"
            doneEvents.first().systembruker `should be equal to` systembruker
            doneEvents.first().grupperingsId `should be equal to` grupperingsId
            doneEvents.first().fodselsnummer `should be equal to` fodselsnummer
            doneEvents.first().eventTidspunkt.toString() `should be equal to` osloDateTime.toString()
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte done-eventer basert paa systembruker`() {
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { getAllGroupedDoneEventsBySystemuser() }

            groupedEventsBySystemuser.size `should be equal to` 2
            groupedEventsBySystemuser.get(done1.systembruker) `should be equal to` 1
            groupedEventsBySystemuser.get(done2.systembruker) `should be equal to` 1
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte inaktive brukernotifikasjoner basert paa systembruker`() {
        createInactiveBrukernotifikasjoner()
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { countTotalNumberOfBrukernotifikasjonerByActiveStatus(aktiv = false) }

            groupedEventsBySystemuser.size `should be equal to` 2
            groupedEventsBySystemuser.get(inaktivBeskjed.systembruker) `should be equal to` 2
            groupedEventsBySystemuser.get(inaktivOppgave.systembruker) `should be equal to` 2
            groupedEventsBySystemuser.get(inaktivInnboks.systembruker) `should be equal to` 1
        }
        deleteInactiveBrukernotifikasjoner()
    }

    private fun createDone(backupDone: List<Done>) {
        runBlocking {
            database.dbQuery { createDone(backupDone) }
        }
    }

    private fun deleteDone(backupDone: List<Done>) {
        runBlocking {
            database.dbQuery { deleteDone(backupDone) }
        }
    }

    private fun  createInactiveBrukernotifikasjoner() {
        runBlocking {
            database.dbQuery {
                createBeskjed(listOf(inaktivBeskjed))
                createOppgave(listOf(inaktivOppgave))
                createInnboks(listOf(inaktivInnboks))
            }
        }
    }

    private fun deleteInactiveBrukernotifikasjoner() {
        runBlocking {
            database.dbQuery {
                deleteBeskjed(listOf(inaktivBeskjed))
                deleteOppgave(listOf(inaktivOppgave))
                deleteInnboks(listOf(inaktivInnboks))
            }
        }
    }
}
