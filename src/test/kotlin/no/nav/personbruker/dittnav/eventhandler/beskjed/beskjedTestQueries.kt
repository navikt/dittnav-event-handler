package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import java.sql.Connection
import java.sql.Types

fun Connection.createBeskjed(beskjeder: List<Beskjed>) =
        prepareStatement("""INSERT INTO beskjed(id, produsent, eventTidspunkt, fodselsnummer, eventId, grupperingsId, tekst, link, sikkerhetsnivaa, sistOppdatert, aktiv, synligFremTil, uid)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""")
                .use {
                    beskjeder.forEach { beskjed ->
                        run {
                            it.setInt(1, beskjed.id)
                            it.setString(2, beskjed.produsent)
                            it.setObject(3, beskjed.eventTidspunkt.toLocalDateTime(), Types.TIMESTAMP)
                            it.setString(4, beskjed.fodselsnummer)
                            it.setString(5, beskjed.eventId)
                            it.setString(6, beskjed.grupperingsId)
                            it.setString(7, beskjed.tekst)
                            it.setString(8, beskjed.link)
                            it.setInt(9, beskjed.sikkerhetsnivaa)
                            it.setObject(10, beskjed.sistOppdatert.toLocalDateTime(), Types.TIMESTAMP)
                            it.setBoolean(11, beskjed.aktiv)
                            it.setObject(12, beskjed.synligFremTil?.toLocalDateTime(), Types.TIMESTAMP)
                            it.setString(13, beskjed.uid)
                            it.addBatch()
                        }
                    }
                    it.executeBatch()
                }

fun Connection.deleteBeskjed(beskjeder: List<Beskjed>) =
        prepareStatement("""DELETE FROM beskjed WHERE eventId = ?""")
                .use {
                    beskjeder.forEach { beskjed ->
                        run {
                            it.setString(1, beskjed.eventId)
                            it.addBatch()
                        }
                    }
                    it.executeBatch()
                }