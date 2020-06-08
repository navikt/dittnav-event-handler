package no.nav.personbruker.dittnav.eventhandler.done

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.sql.Connection
import java.sql.Types
import java.time.ZoneId
import java.time.ZonedDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BackupDoneQueriesKtTest {

    private val database = H2Database()
    private val fodselsnummer = "123"
    private val systembruker = "x-dittnav"
    private val grupperingsId = "012"
    private val zonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Oslo"))

    val done1 = createDoneBackup(systembruker, zonedDateTime, fodselsnummer, "1", grupperingsId)
    val done2 = createDoneBackup(systembruker, zonedDateTime, fodselsnummer, "2", grupperingsId)

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
        }
    }


    fun createDoneBackup(systembruker: String, eventTidspunkt: ZonedDateTime, fodselsnummer: String, eventId: String, grupperingsId: String): BackupDone {
        return BackupDone(
                systembruker = systembruker,
                eventTidspunkt = eventTidspunkt,
                fodselsnummer = fodselsnummer,
                eventId = eventId,
                grupperingsId = grupperingsId)
    }

    private fun createDone(doneList: List<BackupDone>) {
        runBlocking {
            database.dbQuery { createDoneInCache(doneList) }
        }
    }

    private fun deleteDone(doneList: List<BackupDone>) {
        runBlocking {
            database.dbQuery { deleteBackupDoneInCache(doneList) }
        }
    }

    fun Connection.createDoneInCache(done: List<BackupDone>) =
            prepareStatement("""INSERT INTO done(systembruker, eventTidspunkt, fodselsnummer, eventId, grupperingsId)
            VALUES(?, ?, ?, ?, ?)""")
                    .use {
                        done.forEach { done ->
                            run {
                                it.setString(1, done.systembruker)
                                it.setObject(2, done.eventTidspunkt.toLocalDateTime(), Types.TIMESTAMP)
                                it.setString(3, done.fodselsnummer)
                                it.setString(4, done.eventId)
                                it.setString(5, done.grupperingsId)
                                it.addBatch()
                            }
                        }
                        it.executeBatch()
                    }

    fun Connection.deleteBackupDoneInCache(done: List<BackupDone>) =
            prepareStatement("""DELETE FROM done WHERE eventId = ?""")
                    .use {
                        done.forEach { done ->
                            run {
                                it.setString(1, done.eventId)
                                it.addBatch()
                            }
                        }
                        it.executeBatch()
                    }
}