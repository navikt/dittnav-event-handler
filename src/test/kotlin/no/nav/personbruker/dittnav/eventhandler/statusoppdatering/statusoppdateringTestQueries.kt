package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

import no.nav.personbruker.dittnav.eventhandler.common.database.mapList
import java.sql.Connection
import java.sql.Types


fun Connection.createStatusoppdatering(statusoppdateringer: List<Statusoppdatering>) =
        prepareStatement("""INSERT INTO statusoppdatering(id, systembruker, eventId, eventTidspunkt, fodselsnummer, grupperingsId, link, sikkerhetsnivaa, sistOppdatert, statusGlobal, statusIntern, sakstema, namespace, appnavn)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""")
                .use {
                    statusoppdateringer.forEach { statusoppdatering ->
                        run {
                            it.setInt(1, statusoppdatering.id)
                            it.setString(2, statusoppdatering.systembruker)
                            it.setString(3, statusoppdatering.eventId)
                            it.setObject(4, statusoppdatering.eventTidspunkt.toLocalDateTime(), Types.TIMESTAMP)
                            it.setString(5, statusoppdatering.fodselsnummer)
                            it.setString(6, statusoppdatering.grupperingsId)
                            it.setString(7, statusoppdatering.link)
                            it.setInt(8, statusoppdatering.sikkerhetsnivaa)
                            it.setObject(9, statusoppdatering.sistOppdatert.toLocalDateTime(), Types.TIMESTAMP)
                            it.setString(10, statusoppdatering.statusGlobal)
                            it.setString(11, statusoppdatering.statusIntern)
                            it.setString(12, statusoppdatering.sakstema)
                            it.setString(13, statusoppdatering.namespace)
                            it.setString(14, statusoppdatering.appnavn)
                            it.addBatch()
                        }
                    }
                    it.executeBatch()
                }

fun Connection.deleteStatusoppdatering(statusoppdateringer: List<Statusoppdatering>) =
        prepareStatement("""DELETE FROM statusoppdatering WHERE eventId = ?""")
                .use {
                    statusoppdateringer.forEach { statusoppdatering ->
                        run {
                            it.setString(1, statusoppdatering.eventId)
                            it.addBatch()
                        }
                    }
                    it.executeBatch()
                }

fun Connection.getAllStatusoppdateringEvents(): List<Statusoppdatering> =
        prepareStatement("""SELECT 
            |id,
            |eventTidspunkt,
            |fodselsnummer,
            |eventId, 
            |grupperingsId,
            |link,
            |sikkerhetsnivaa,
            |sistOppdatert,
            |statusGlobal,
            |statusIntern,
            |sakstema,
            |systembruker,
            |namespace,
            |appnavn
            |FROM statusoppdatering""".trimMargin())
                .use {
                    it.executeQuery().mapList {
                        toStatusoppdatering()
                    }
                }
