package no.nav.personbruker.dittnav.eventhandler.database.entity

import java.sql.Connection
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime

fun Connection.getInformasjonByAktorid(aktorid: String): List<Event> =
        prepareStatement("""SELECT * FROM INFORMASJON WHERE aktorid = ?""")
                .use {
                    it.setString(1, aktorid)
                    it.executeQuery().list {
                        toEvent()
                    }
                }

fun Connection.getOppgaveByAktorid(aktorid: String): List<Event> =
        prepareStatement("""SELECT * FROM OPPGAVE WHERE aktorid = ?""")
                .use {
                    it.setString(1, aktorid)
                    it.executeQuery().list {
                        toEvent()
                    }
                }

private fun ResultSet.toEvent(): Event {
    return Event(
            id = getInt("id"),
            produsent = getString("produsent"),
            eventTidspunkt = ZonedDateTime.ofInstant(getTimestamp("eventTidspunkt").toInstant(), ZoneId.of("Europe/Oslo")),
            aktorId = getString("aktorId"),
            eventId = getString("eventId"),
            dokumentId = getString("dokumentId"),
            tekst = getString("tekst"),
            link = getString("link"),
            sikkerhetsnivaa = getInt("sikkerhetsnivaa"),
            sistOppdatert = ZonedDateTime.ofInstant(getTimestamp("sistOppdatert").toInstant(), ZoneId.of("Europe/Oslo")),
            aktiv = getBoolean("aktiv")
    )
}

private fun <T> ResultSet.list(result: ResultSet.() -> T): List<T> =
        mutableListOf<T>().apply {
            while (next()) {
                add(result())
            }
        }
