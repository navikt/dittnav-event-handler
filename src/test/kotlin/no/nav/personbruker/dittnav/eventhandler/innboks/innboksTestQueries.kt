package no.nav.personbruker.dittnav.eventhandler.innboks

import java.sql.Connection
import java.sql.Types

fun Connection.createInnboks(innboks: List<Innboks>) =
        prepareStatement("""INSERT INTO innboks(id, produsent, eventTidspunkt, fodselsnummer, eventId, grupperingsId, tekst, link, sikkerhetsnivaa, sistOppdatert, aktiv)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""")
                .use {
                    innboks.forEach { i ->
                        run {
                            it.setInt(1, i.id)
                            it.setString(2, i.produsent)
                            it.setObject(3, i.eventTidspunkt.toLocalDateTime(), Types.TIMESTAMP)
                            it.setString(4, i.fodselsnummer)
                            it.setString(5, i.eventId)
                            it.setString(6, i.grupperingsId)
                            it.setString(7, i.tekst)
                            it.setString(8, i.link)
                            it.setInt(9, i.sikkerhetsnivaa)
                            it.setObject(10, i.sistOppdatert.toLocalDateTime(), Types.TIMESTAMP)
                            it.setBoolean(11, i.aktiv)
                            it.addBatch()
                        }
                    }
                    it.executeBatch()
                }

fun Connection.deleteInnboks(innboks: List<Innboks>) =
        prepareStatement("""DELETE FROM innboks WHERE eventId = ?""")
                .use {
                    innboks.forEach { i ->
                        run {
                            it.setString(1, i.eventId)
                            it.addBatch()
                        }
                    }
                    it.executeBatch()
                }
