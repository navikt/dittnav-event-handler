package no.nav.personbruker.dittnav.eventhandler.done

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import java.sql.Connection
import java.sql.Types
import java.time.ZoneId
import java.time.ZonedDateTime

object BackupDoneObjectMother {

    private val database = H2Database()

    fun createBackupDone(eventId: String, fodselsnummer: String): BackupDone {
        return BackupDone(
                eventId = eventId,
                systembruker = "x-dittnav",
                fodselsnummer = fodselsnummer,
                grupperingsId = "100$fodselsnummer",
                eventTidspunkt = ZonedDateTime.now(ZoneId.of("Europe/Oslo"))
        )
    }

    fun createDoneBackup(systembruker: String, eventTidspunkt: ZonedDateTime, fodselsnummer: String, eventId: String, grupperingsId: String): BackupDone {
        return BackupDone(
                systembruker = systembruker,
                eventTidspunkt = eventTidspunkt,
                fodselsnummer = fodselsnummer,
                eventId = eventId,
                grupperingsId = grupperingsId)
    }


    fun createDone(doneList: List<BackupDone>) {
        runBlocking {
            database.dbQuery { createDoneInCache(doneList) }
        }
    }

    fun deleteDone(doneList: List<BackupDone>) {
        runBlocking {
            database.dbQuery { deleteBackupDoneInCache(doneList) }
        }
    }

    private fun Connection.createDoneInCache(done: List<BackupDone>) =
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

    private fun Connection.deleteBackupDoneInCache(done: List<BackupDone>) =
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
