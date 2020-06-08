package no.nav.personbruker.dittnav.eventhandler.done

import java.sql.Connection
import java.sql.Types
import java.time.ZoneId
import java.time.ZonedDateTime

internal class BackupDoneQueriesKtTest {

    private val fodselsnummer = "123"
    private val eventId = "11"
    private val systembruker = "x-dittnav"
    private val grupperingsId = "012"
    private val zonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Oslo"))

    val done1 = createDone(systembruker, zonedDateTime, fodselsnummer, eventId, grupperingsId)
    val done2 = createDone(systembruker, zonedDateTime, fodselsnummer, eventId, grupperingsId)

    fun createDone(systembruker: String, eventTidspunkt: ZonedDateTime, fodselsnummer: String, eventId: String, grupperingsId: String): BackupDone {
        return BackupDone(
                systembruker = systembruker,
                eventTidspunkt = eventTidspunkt,
                fodselsnummer = fodselsnummer,
                eventId = eventId,
                grupperingsId = grupperingsId)
    }

    fun Connection.createBackupDone(done: List<BackupDone>) =
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

    fun Connection.deleteBackupDone(done: List<BackupDone>) =
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