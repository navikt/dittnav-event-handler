package no.nav.personbruker.dittnav.eventhandler.statusOppdatering

import java.sql.Connection
import java.sql.Types


fun Connection.createStatusOppdatering(statusOppdateringer: List<StatusOppdatering>) =
        prepareStatement("""INSERT INTO statusoppdatering(id, systembruker, eventId, eventTidspunkt, fodselsnummer, grupperingsId, link, sikkerhetsnivaa, sistOppdatert, statusGlobal, statusIntern, sakstema)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""")
                .use {
                    statusOppdateringer.forEach { statusOppdatering ->
                        run {
                            it.setInt(1, statusOppdatering.id)
                            it.setString(2, statusOppdatering.systembruker)
                            it.setString(3, statusOppdatering.eventId)
                            it.setObject(4, statusOppdatering.eventTidspunkt.toLocalDateTime(), Types.TIMESTAMP)
                            it.setString(5, statusOppdatering.fodselsnummer)
                            it.setString(6, statusOppdatering.grupperingsId)
                            it.setString(7, statusOppdatering.link)
                            it.setInt(8, statusOppdatering.sikkerhetsnivaa)
                            it.setObject(9, statusOppdatering.sistOppdatert.toLocalDateTime(), Types.TIMESTAMP)
                            it.setString(10, statusOppdatering.statusGlobal)
                            it.setString(11, statusOppdatering.statusIntern)
                            it.setString(12, statusOppdatering.sakstema)
                            it.addBatch()
                        }
                    }
                    it.executeBatch()
                }

fun Connection.deleteStatusOppdatering(statusOppdateringer: List<StatusOppdatering>) =
        prepareStatement("""DELETE FROM statusOppdatering WHERE eventId = ?""")
                .use {
                    statusOppdateringer.forEach { statusOppdatering ->
                        run {
                            it.setString(1, statusOppdatering.eventId)
                            it.addBatch()
                        }
                    }
                    it.executeBatch()
                }
