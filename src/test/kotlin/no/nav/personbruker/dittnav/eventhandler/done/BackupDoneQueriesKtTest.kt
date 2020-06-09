package no.nav.personbruker.dittnav.eventhandler.done

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.ZoneId
import java.time.ZonedDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BackupDoneQueriesKtTest {


    private val database = H2Database()
    private val fodselsnummer = "123"
    private val systembruker = "x-dittnav"
    private val grupperingsId = "xxx"
    private val utcDateTime = ZonedDateTime.now(ZoneId.of("UTC"))
    private val osloDateTime = ZonedDateTime.ofInstant(utcDateTime.toInstant(), ZoneId.of("Europe/Oslo"))

    val done1 = BackupDoneObjectMother.createDoneBackup(systembruker, utcDateTime, fodselsnummer, "1", grupperingsId)
    val done2 = BackupDoneObjectMother.createDoneBackup(systembruker, utcDateTime, fodselsnummer, "2", grupperingsId)

    @BeforeAll
    fun `populer testdata`() {
        BackupDoneObjectMother.createDone(listOf(done1, done2))
    }

    @AfterAll
    fun `slett testdata`() {
        BackupDoneObjectMother.deleteDone(listOf(done1, done2))
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

}