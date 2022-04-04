package no.nav.personbruker.dittnav.eventhandler.done

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import no.nav.personbruker.dittnav.eventhandler.beskjed.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.deleteBeskjed
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.common.findCountFor
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
import java.time.ZoneOffset
import java.time.ZonedDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DoneQueriesTest {

    private val database = LocalPostgresDatabase.cleanDb()
    private val fodselsnummer = "123"
    private val systembruker = "x-dittnav"
    private val namespace = "localhost"
    private val appnavn = "dittnav"
    private val grupperingsId = "xxx"
    private val utcDateTime = ZonedDateTime.now(ZoneOffset.UTC)

    private val done1 = DoneObjectMother.createDone(systembruker = "x-dittnav", utcDateTime, fodselsnummer, "1", grupperingsId)
    private val done2 = DoneObjectMother.createDone(systembruker = "y-dittnav", utcDateTime, fodselsnummer, "2", grupperingsId)
    private val inaktivBeskjed = BeskjedObjectMother.createBeskjed(id = 1, eventId = "123", fodselsnummer = "00", synligFremTil = ZonedDateTime.now().plusHours(1), uid = "11", aktiv = false, systembruker = systembruker, namespace = namespace, appnavn = appnavn)
    private val inaktivOppgave = OppgaveObjectMother.createOppgave(id = 1, eventId = "123", fodselsnummer = "01", aktiv = false, systembruker = systembruker, namespace = namespace, appnavn = appnavn)
    private val inaktivInnboks = InnboksObjectMother.createInnboks(id = 1, eventId = "123", fodselsnummer = "02", aktiv = false, systembruker = "x-dittnav-2", namespace = namespace, appnavn = "dittnav-2")

    @BeforeAll
    fun `populer testdata`() {
        createDone(listOf(done1, done2))
    }

    @AfterAll
    fun `slett testdata`() {
        deleteDone(listOf(done1, done2))
    }

    @Test
    fun `Returnerer en liste av alle grupperte done-eventer basert paa systembruker`() {
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { getAllGroupedDoneEventsBySystemuser() }

            groupedEventsBySystemuser.size `should be equal to` 2
            groupedEventsBySystemuser[done1.systembruker] `should be equal to` 1
            groupedEventsBySystemuser[done2.systembruker] `should be equal to` 1
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte inaktive brukernotifikasjoner basert paa systembruker`() {
        createInactiveBrukernotifikasjoner()
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { countTotalNumberOfBrukernotifikasjonerByActiveStatus(aktiv = false) }

            groupedEventsBySystemuser.size `should be equal to` 2
            groupedEventsBySystemuser[inaktivBeskjed.systembruker] `should be equal to` 2
            groupedEventsBySystemuser[inaktivOppgave.systembruker] `should be equal to` 2
            groupedEventsBySystemuser[inaktivInnboks.systembruker] `should be equal to` 1
        }
        deleteInactiveBrukernotifikasjoner()
    }


    @Test
    fun `Returnerer en liste av alle grupperte inaktive brukernotifikasjoner basert paa produsent`() {
        createInactiveBrukernotifikasjoner()
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { countTotalNumberPerProducerByActiveStatus(aktiv = false) }

            groupedEventsBySystemuser.size `should be equal to` 2
            groupedEventsBySystemuser.findCountFor(inaktivBeskjed.namespace, inaktivBeskjed.appnavn) `should be equal to` 2
            groupedEventsBySystemuser.findCountFor(inaktivOppgave.namespace, inaktivOppgave.appnavn) `should be equal to` 2
            groupedEventsBySystemuser.findCountFor(inaktivInnboks.namespace, inaktivInnboks.appnavn) `should be equal to` 1
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
