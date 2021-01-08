package no.nav.personbruker.dittnav.eventhandler.done

import java.sql.Connection
import java.sql.Types

fun Connection.createDoneInCache(done: List<Done>) =
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

fun Connection.deleteBackupDoneInCache(done: List<Done>) =
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
