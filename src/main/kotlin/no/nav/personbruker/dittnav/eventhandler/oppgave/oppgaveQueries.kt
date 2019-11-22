package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.personbruker.dittnav.eventhandler.common.database.map
import java.sql.Connection
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.ZoneId

fun Connection.getAllOppgaveByAktorId(aktorId: String): List<Oppgave> =
        prepareStatement("""SELECT * FROM OPPGAVE WHERE aktorId = ?""")
                .use {
                    it.setString(1, aktorId)
                    it.executeQuery().map {
                        toOppgave()
                    }
                }

fun Connection.getActiveOppgaveByAktorId(aktorId: String): List<Oppgave> =
        prepareStatement("""SELECT * FROM OPPGAVE WHERE aktorId = ? AND aktiv = true""")
                .use {
                    it.setString(1, aktorId)
                    it.executeQuery().map {
                        toOppgave()
                    }
                }

private fun ResultSet.toOppgave(): Oppgave {
    return Oppgave(
            id = getInt("id"),
            produsent = getString("produsent"),
            eventTidspunkt = LocalDateTime.ofInstant(getTimestamp("eventTidspunkt").toInstant(), ZoneId.of("Europe/Oslo")),
            aktorId = getString("aktorId"),
            eventId = getString("eventId"),
            dokumentId = getString("dokumentId"),
            tekst = getString("tekst"),
            link = getString("link"),
            sikkerhetsnivaa = getInt("sikkerhetsnivaa"),
            sistOppdatert = LocalDateTime.ofInstant(getTimestamp("sistOppdatert").toInstant(), ZoneId.of("Europe/Oslo")),
            aktiv = getBoolean("aktiv")
    )
}